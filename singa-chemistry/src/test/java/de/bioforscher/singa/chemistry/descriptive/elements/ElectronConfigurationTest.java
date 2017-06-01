package de.bioforscher.singa.chemistry.descriptive.elements;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class ElectronConfigurationTest {

    @Test
    public void shouldParseElectronConfiguration() {
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p1");
        assertEquals(configuration.toString(), "1s2-2s2-2p1");
    }

    @Test
    public void shouldCalculateTotalNumberOfElectrons() {
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p1");
        assertEquals(configuration.getTotalNumberOfElectrons(), 5, 0);
    }

    @Test
    public void shouldResolveOuterMostShell() {
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p1");
        assertEquals(configuration.getOuterMostShell(), 2, 0);
    }

    @Test
    public void shouldCollectIncompleteShells() {
        // chromium
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p6-3s2-3p6-3d5-4s1");
        assertEquals(configuration.getNumberOfValenceElectrons(), 6, 0);
    }

}
