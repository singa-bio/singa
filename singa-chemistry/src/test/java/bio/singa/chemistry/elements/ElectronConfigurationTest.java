package bio.singa.chemistry.elements;

import bio.singa.chemistry.model.elements.ElectronConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class ElectronConfigurationTest {

    @Test
    void shouldParseElectronConfiguration() {
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p1");
        assertEquals(configuration.toString(), "1s2-2s2-2p1");
    }

    @Test
    void shouldCalculateTotalNumberOfElectrons() {
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p1");
        assertEquals(configuration.getTotalNumberOfElectrons(), 5);
    }

    @Test
    void shouldResolveOuterMostShell() {
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p1");
        assertEquals(configuration.getOuterMostShell(), 2);
    }

    @Test
    void shouldCollectIncompleteShells() {
        // chromium
        ElectronConfiguration configuration = ElectronConfiguration.parseElectronConfigurationFromString("1s2-2s2-2p6-3s2-3p6-3d5-4s1");
        assertEquals(configuration.getNumberOfValenceElectrons(), 6);
    }

}
