package edu.sc.seis.TauP;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jline.Completor;
import jline.ConsoleReader;
import jline.Terminal;

import org.python.core.Py;
import org.python.core.PyArray;
import org.python.core.PyException;
import org.python.core.PyJavaType;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.InteractiveConsole;

//import edu.sc.seis.fissuresUtil.simple.Initializer;

public class TauP_Console extends InteractiveConsole {

    public TauP_Console() throws IOException {
        super();
        // get some defaults
       // Initializer.loadProps((JythonConsole.class).getClassLoader()
       //         .getResourceAsStream(DEFAULT_PROPS), props);
        Terminal.setupTerminal();
        reader = new ConsoleReader();
        reader.getHistory()
                .setHistoryFile(new File(System.getProperty("user.home"),
                                         historyFilename));
        reader.addCompletor(new DirCompleter());
        //cflags.generator_allowed = true;
        systemState.ps1 = new PyString("taup> ");
        exec("import sys");
        exec("sys.path.append('"+formatForJythonSysPath(TauP_Console.class, "edu/sc/seis/TauP/jython")+"')");
        //exec("print sys.path");
        exec("from taup import *");
        set("history", PyJavaType.wrapJavaObject(reader.getHistory()));
        exec("def h(num):\n    his = history.getHistoryList().toArray()[-num:]\n    for line in his:\n        print line");
    }

    public void interact() {
        interact(prompt, null);
    }

    public String raw_input(PyObject prompt) {
        String line = null;
        try {
            line = reader.readLine(prompt.toString());
        } catch(IOException io) {
            throw new PyException(Py.IOError);
        }
        if(line == null) {
            throw new PyException(Py.EOFError); // Ctrl-D exit
        }
        return line.endsWith("\n") ? line.substring(0, line.length() - 1)
                : line;
    }
    
    public static String formatForJythonSysPath(Class c, String jarDir) {
        String out = c.getClassLoader().getResource(jarDir).toString().substring("jar:file:".length()).replaceAll("\\!","");
        return out;
    }

    protected String prompt = "TauP " + Version.getVersion();

    protected String historyFilename = ".jline-taup.history";

    private static Properties props = System.getProperties();
    
    public static final String DEFAULT_PROPS = "edu/sc/seis/bag/bag.props";
    
    private ConsoleReader reader;

    public class DirCompleter implements Completor {

        public int complete(String buffer, int cursor, List candidates) {
            try {
            Matcher m = rightmostPeriod.matcher(buffer);
            int knownEnd = 0;
            String partial = buffer;
            if(m.matches()) {
                knownEnd = m.start(1);
                partial = buffer.substring(knownEnd + 1);
            }
            PyObject res;
            if(buffer.trim().length() != 0) {
                res = eval("dir(" + buffer.substring(0, knownEnd) + ")");
            } else {
                res = new PyArray(String.class, 1);
                res.__setitem__(0, new PyString("  "));
            }
            for(int i = 0; i < res.__len__(); i++) {
                String possible = res.__getitem__(i).toString();
                if(possible.startsWith(partial)) {
                    candidates.add(possible);
                }
            }
            return m.matches() ? knownEnd + 1 : 0;
            } catch (Throwable e) {
                // I guess we can't complete
                return 0;
            }
        }

        Pattern rightmostPeriod = Pattern.compile(".*(\\.)[^.]*");
    }

    public static void main(String[] args) throws IOException {
        
        TauP_Console ic = new TauP_Console();
        if(args.length > 0) {
            ic.execfile(args[0]);
        } else {
            ic.interact();
        }
    }

}