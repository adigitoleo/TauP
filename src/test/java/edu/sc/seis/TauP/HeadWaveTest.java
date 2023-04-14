package edu.sc.seis.TauP;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class HeadWaveTest {

    TauModel tMod;
    boolean DEBUG = false;

    @BeforeEach
    protected void setUp() throws Exception {
        String modelName = "iasp91";
        tMod = TauModelLoader.load(modelName);
    }

    public void checkHeadWave(String phaseName, int numHeadLegs, double distDeg) throws TauModelException {
        double receiverDepth = 0;
        SimpleSeismicPhase phase = SeismicPhaseFactory.createPhase(phaseName, tMod, tMod.getSourceDepth(), receiverDepth, DEBUG);
        assertEquals(numHeadLegs, phase.headOrDiffractSeq.size());
        List<Arrival> arrivalList = phase.calcTime(distDeg);
        for (Arrival a : arrivalList) {
            TimeDist[] td = a.getPierce();
            assertEquals(a.getDist(), td[td.length-1].getDistRadian(), 0.000000001);
            assertEquals(a.getTime(), td[td.length-1].getTime(), 0.000001);
            TimeDist[] path_td = a.getPath();
            assertEquals(a.getDist(), path_td[path_td.length-1].getDistRadian(), 0.000000001);
            assertEquals(a.getTime(), path_td[path_td.length-1].getTime(), 0.000001);
        }
    }

    @Test
    public void pierce_Pdiff() throws TauModelException {
        checkHeadWave("Pdiff", 1, 210);
    }

    @Test
    public void pierce_PdiffPdiff() throws TauModelException {
        checkHeadWave("PdiffPdiff", 2, 210);
    }

    @Test
    public void pierce_Pn() throws TauModelException {
        checkHeadWave("Pn", 1, 1);
    }

    @Test
    public void pierce_PnPn() throws TauModelException {
        checkHeadWave("PnPn", 2, 2.5);
    }

    @Test
    public void pierce_PKdiffP() throws TauModelException {
        checkHeadWave("PKdiffP", 1, 210);
    }

    @Test
    public void pierce_PKdiffKdiffP() throws TauModelException {
        checkHeadWave("PKdiffKdiffP", 2, 210);
    }
}
