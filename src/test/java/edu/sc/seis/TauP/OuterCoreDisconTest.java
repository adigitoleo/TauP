package edu.sc.seis.TauP;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class OuterCoreDisconTest {


    public OuterCoreDisconTest() throws VelocityModelException, SlownessModelException, TauModelException, IOException {
        vMod = VelocityModelTest.loadTestVelMod(modelName);
        TauP_Create taupCreate = new TauP_Create();
        tMod = taupCreate.createTauModel(vMod);
    }

    String modelName = "outerCoreDiscon.nd";
    VelocityModel vMod;
    TauModel tMod;
    String[] phaseNames = { "PKiKP", "PKv3000kp", "PKik^3000KiKP", "PKv3000kKiKP"};


    @Test
    void dicsonPhasesTest() throws TauModelException {
        for(String name : phaseNames) {
            SeismicPhase phase = SeismicPhaseFactory.createPhase(name, tMod);
            List<Arrival> arrivals = phase.calcTime(0);
            assertEquals( 1, arrivals.size(), name);

        }
    }

    @Test
    void notSamePcP() throws TauModelException {
        SeismicPhase phase_PcP = SeismicPhaseFactory.createPhase("PcP", tMod);
        List<Arrival> arrivals_PcP = phase_PcP.calcTime(0);
        SeismicPhase phase_PKv3000KP = SeismicPhaseFactory.createPhase("PKv3000KP", tMod);
        List<Arrival> arrivals_PKv3000KP = phase_PKv3000KP.calcTime(0);
        assertEquals(arrivals_PcP.size(), arrivals_PKv3000KP.size());
        assertNotEquals(arrivals_PcP.get(0).getTime(), arrivals_PKv3000KP.get(0).getTime());
    }


    @Test
    void deepSource() throws TauModelException {
        double depth = 3050;
        TauModel tauModelDepth = tMod.depthCorrect(depth);
        SeismicPhase phase_kP = SeismicPhaseFactory.createPhase("kP", tMod, depth);
        List<Arrival> arrivals_kP = phase_kP.calcTime(0);
        assertEquals( 1, arrivals_kP.size(), "kP");

        SeismicPhase phase_kKv3000kp = SeismicPhaseFactory.createPhase("kKv3000kp", tMod, depth);
        List<Arrival> arrivals_kKv3000kp = phase_kKv3000kp.calcTime(0);
        assertEquals( 1, arrivals_kKv3000kp.size(), "kP");

        SeismicPhase phase_under_ref = SeismicPhaseFactory.createPhase("k^3000KIKP", tMod, depth);
        List<Arrival> arrivals__under_ref = phase_under_ref.calcTime(180);
        assertEquals(1, arrivals__under_ref.size());
    }
}
