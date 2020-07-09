package bio.singa.structure.parser.chebi;

import bio.singa.chemistry.features.databases.chebi.ChEBIImageService;
import bio.singa.chemistry.features.logp.LogP;
import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.features.quantities.MolarMass;
import bio.singa.features.quantities.MolarVolume;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cl
 */
class ChEBIParserServiceTest {

    @Test
    void shouldParseMethanolFromChEBIOnline() {
        SmallMolecule methanol = ChEBIParserService.parse("CHEBI:17790");
        assertEquals("methanol", methanol.getNames().iterator().next().toLowerCase());
        assertEquals(32.04186, methanol.getFeature(MolarMass.class).getValue().doubleValue());
    }

    @Test
    void shouldFetchImageForMethanolFromChEBIDatabase() {
        ChEBIImageService service = new ChEBIImageService("CHEBI:17790");
        assertNotNull(service.parse());
    }

    @Test
    void shouldBeAbleToFetchLogPWithChEBISpecies() {
        SmallMolecule testSpecies = ChEBIParserService.parse("CHEBI:8772");
        // get feature
        LogP feature = testSpecies.getFeature(LogP.class);
        // assert attributes and values
        assertEquals("PubChem Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(5.2, feature.getContent().doubleValue());
    }

    @Test
    @Disabled
    void shouldUseChEBIToCalculateVolume() {
        // values from http://pubs.acs.org/doi/pdf/10.1021/ja00354a007

        SmallMolecule ammonia = ChEBIParserService.parse("CHEBI:16134");
        // Species propane = ChEBIParserService.parse("CHEBI:32879");
        // Species benzene = ChEBIParserService.parse("CHEBI:16716");
        // Species biphenyl = ChEBIParserService.parse("CHEBI:17097");

        final MolarVolume ammoniaFeature = ammonia.getFeature(MolarVolume.class);
        assertEquals(21.91, ammoniaFeature.getValue().doubleValue(), 2.0);

        // TODO no actual 3d structures are available from chebi
        // maybe try to go to pdb and fetch ligand information there
        // alternatively parse mole file
        // propane.setFeature(MolarVolume.class);
        // final MolarVolume propaneFeature = propane.getFeature(MolarVolume.class);
        // assertEquals(61.39, propaneFeature.getValue().doubleValue(), 5.0);
        //
        // benzene.setFeature(MolarVolume.class);
        // final MolarVolume benzeneFeature = benzene.getFeature(MolarVolume.class);
        // assertEquals(85.39, benzeneFeature.getValue().doubleValue(), 5.0);
        //
        // biphenyl.setFeature(MolarVolume.class);
        // final MolarVolume biphenylFeature = biphenyl.getFeature(MolarVolume.class);
        // assertEquals(157.1, biphenylFeature.getValue().doubleValue(), 5.0);

    }

}