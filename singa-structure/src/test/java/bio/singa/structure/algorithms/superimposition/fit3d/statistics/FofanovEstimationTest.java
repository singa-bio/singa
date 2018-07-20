package bio.singa.structure.algorithms.superimposition.fit3d.statistics;

import bio.singa.core.utility.Resources;
import bio.singa.structure.algorithms.superimposition.fit3d.Fit3D;
import bio.singa.structure.algorithms.superimposition.fit3d.Fit3DBuilder;
import bio.singa.structure.model.identifiers.LeafIdentifiers;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
import org.junit.Before;
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
    public void shouldCalculatePvalues() {
        FofanovEstimation fofanovEstimation = new FofanovEstimation(2.5);
        StructureParser.MultiParser multiParser = StructureParser.mmtf()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("nrpdb_BLAST_10e80_100.txt")), "_")
                .setOptions(structureParserOptions);
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .statisticalModel(fofanovEstimation)
                .run();
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != 0.0));
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != Double.NaN));
    }

    @Test
    public void shouldCalculatePvaluesWithCorrectnessCutoff() {
        double modelCorrectnessCutoff = 3.0;
        double epsilon = FofanovEstimation.determineEpsilon(queryMotif, modelCorrectnessCutoff);
        FofanovEstimation fofanovEstimation = new FofanovEstimation(epsilon, FofanovEstimation.DEFAULT_REFERENCE_SIZE, modelCorrectnessCutoff);
        StructureParser.MultiParser multiParser = StructureParser.mmtf()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("nrpdb_BLAST_10e80_100.txt")), "_")
                .setOptions(structureParserOptions);
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .statisticalModel(fofanovEstimation)
                .rmsdCutoff(epsilon)
                .run();
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != 0.0));
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != Double.NaN));
    }

}