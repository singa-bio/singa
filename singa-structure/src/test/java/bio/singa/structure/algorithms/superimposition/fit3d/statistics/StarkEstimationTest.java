package bio.singa.structure.algorithms.superimposition.fit3d.statistics;

import bio.singa.core.utility.Resources;
import bio.singa.structure.algorithms.superimposition.fit3d.Fit3D;
import bio.singa.structure.algorithms.superimposition.fit3d.Fit3DBuilder;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.general.StructuralEntityFilter;
import bio.singa.structure.model.general.StructuralMotif;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.io.general.iterators.StructureIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class StarkEstimationTest {

    private static StructuralMotif queryMotif;
    private static StructureParserOptions structureParserOptions;

    @BeforeAll
    static void initialize() {
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                PdbLeafIdentifier.of("E-57", "E-102", "E-195"));
        structureParserOptions = StructureParserOptions.withSettings(
                StructureParserOptions.Setting.OMIT_EDGES,
                StructureParserOptions.Setting.OMIT_HYDROGENS,
                StructureParserOptions.Setting.OMIT_LIGAND_INFORMATION,
                StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME);
    }

    @Test
    void shouldCalculatePvalues() {
        StarkEstimation starkEstimation = new StarkEstimation();
        StructureIterator multiParser = StructureParser.mmtf()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("nrpdb_BLAST_10e80_100.txt")), "_")
                .everything();
        multiParser.setOptions(structureParserOptions);
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .statisticalModel(starkEstimation)
                .run();
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != 0.0));
        assertTrue(fit3dBatch.getMatches().stream()
                .anyMatch(match -> match.getPvalue() != Double.NaN));
    }

}