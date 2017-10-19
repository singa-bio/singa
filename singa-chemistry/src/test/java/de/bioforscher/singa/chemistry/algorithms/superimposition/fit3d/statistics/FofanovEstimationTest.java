package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.statistics;

import de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.Fit3D;
import de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.Fit3DBuilder;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifiers;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class FofanovEstimationTest {

    private StructuralMotif queryMotif;

    @Before
    public void setUp() {
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        this.queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
    }

    @Test
    public void shouldCalculatePvalues() throws IOException, InterruptedException {
        StructureParserOptions structureParserOptions = StructureParserOptions.withSettings(
                StructureParserOptions.Setting.OMIT_EDGES,
                StructureParserOptions.Setting.OMIT_HYDROGENS,
                StructureParserOptions.Setting.OMIT_LIGAND_INFORMATION,
                StructureParserOptions.Setting.GET_IDENTIFIER_FROM_FILENAME);
        FofanovEstimation fofanovEstimation = new FofanovEstimation(2.5);
        StructureParser.MultiParser multiParser = StructureParser.online()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("nrpdb_BLAST_10e80_500.txt")), "_")
                .setOptions(structureParserOptions);
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(this.queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(AtomFilter.isArbitrary())
                .statisticalModel(fofanovEstimation)
                .run();
        assertTrue(fit3dBatch.getMatches().stream()
                .noneMatch(match -> match.getPvalue() == Double.NaN));
    }
}