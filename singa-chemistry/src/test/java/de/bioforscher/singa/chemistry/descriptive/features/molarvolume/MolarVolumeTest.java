package de.bioforscher.singa.chemistry.descriptive.features.molarvolume;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.structure.features.molarvolume.MolarVolume;
import org.junit.Test;

/**
 * @author cl
 */
public class MolarVolumeTest {

    @Test
    public void shouldUseChEBIToCalculateVolume() {
        Species ammonia = ChEBIParserService.parse("CHEBI:16134");
        Species propane = ChEBIParserService.parse("CHEBI:32879");
        Species benzene = ChEBIParserService.parse("CHEBI:16716");
        Species biphenyl = ChEBIParserService.parse("CHEBI:17097");

        ammonia.setFeature(MolarVolume.class);
        final MolarVolume ammoniaFeature = ammonia.getFeature(MolarVolume.class);
//         assertEquals(21.91, ammoniaFeature.getValue().doubleValue(), 5.0);

        propane.setFeature(MolarVolume.class);
        final MolarVolume propaneFeature = propane.getFeature(MolarVolume.class);
//        assertEquals(61.39, propaneFeature.getValue().doubleValue(), 5.0);

        benzene.setFeature(MolarVolume.class);
        final MolarVolume benzeneFeature = benzene.getFeature(MolarVolume.class);
//        assertEquals(85.39, benzeneFeature.getValue().doubleValue(), 5.0);

        biphenyl.setFeature(MolarVolume.class);
        final MolarVolume biphenylFeature = biphenyl.getFeature(MolarVolume.class);
//        assertEquals(157.1, biphenylFeature.getValue().doubleValue(), 5.0);

    }

}