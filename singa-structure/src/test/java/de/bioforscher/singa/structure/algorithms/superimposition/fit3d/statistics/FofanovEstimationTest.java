package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.statistics;

import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.Fit3D;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifiers;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class FofanovEstimationTest {

    private StructuralMotif queryMotif;
    private StructureParserOptions structureParserOptions;

    @Before
    public void setUp() {
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        structureParserOptions = StructureParserOptions.withSettings(
                StructureParserOptions.Setting.OMIT_EDGES,
                StructureParserOptions.Setting.OMIT_HYDROGENS,
                StructureParserOptions.Setting.OMIT_LIGAND_INFORMATION,
                StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME);
    }

    @Test
    @Ignore
    public void shouldCalculatePvalues() {
        FofanovEstimation fofanovEstimation = new FofanovEstimation(2.5);
        StructureParser.MultiParser multiParser = StructureParser.online()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("nrpdb_BLAST_10e80_500.txt")), "_")
                .setOptions(structureParserOptions);
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(AtomFilter.isArbitrary())
                .statisticalModel(fofanovEstimation)
                .run();
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != 0.0));
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != Double.NaN));
    }

    @Test
    @Ignore
    public void shouldCalculatePvaluesWithCorrectnessCutoff() {
        double modelCorrectnessCutoff = 3.0;
        double epsilon = FofanovEstimation.determineEpsilon(queryMotif, modelCorrectnessCutoff);
        FofanovEstimation fofanovEstimation = new FofanovEstimation(epsilon, FofanovEstimation.DEFAULT_REFERENCE_SIZE, modelCorrectnessCutoff);
        StructureParser.MultiParser multiParser = StructureParser.online()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("nrpdb_BLAST_10e80_500.txt")), "_")
                .setOptions(structureParserOptions);
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(AtomFilter.isArbitrary())
                .statisticalModel(fofanovEstimation)
                .rmsdCutoff(epsilon)
                .run();
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != 0.0));
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != Double.NaN));
    }

}