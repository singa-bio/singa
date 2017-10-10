package de.bioforscher.singa.chemistry.algorithms.superimposition.affinity;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class AffinityAlignmentTest {

    private List<StructuralMotif> input;

    @Before
    public void setUp() throws Exception {
        StructureParserOptions structureParserOptions = new StructureParserOptions();
        structureParserOptions.inferIdentifierFromFileName(true);
        this.input = Files.list(Paths.get(Resources.getResourceAsFileLocation("consensus_alignment")))
                .map(path -> StructureParser.local()
                        .fileLocation(path.toString())
                        .everything()
                        .setOptions(structureParserOptions)
                        .parse())
                .map(Structure::getAllLeafSubstructures)
                .map(StructuralMotif::fromLeafSubstructures)
                .collect(Collectors.toList());
    }

    @Test
    public void shouldRunAffinityAlignment() throws IOException {
        AffinityAlignment affinityAlignment = AffinityAlignment.create()
                .inputStructuralMotifs(this.input)
                .run();
        Assert.assertEquals(3, affinityAlignment.getClusters().size());
        Assert.assertEquals(this.input.size(), affinityAlignment.getClusters().values().stream()
                .mapToInt(Collection::size)
                .sum());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInputOfDifferentSize() {
        this.input.get(0).addSubstructure(new AminoAcid(0, AminoAcidFamily.ALANINE));
        AffinityAlignment.create()
                .inputStructuralMotifs(this.input)
                .run();
    }
}