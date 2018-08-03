package bio.singa.structure.algorithms.superimposition.affinity;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakAminoAcid;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class AffinityAlignmentTest {

    private List<StructuralMotif> input;

    @Before
    public void setUp() throws Exception {
        StructureParserOptions structureParserOptions = new StructureParserOptions();
        structureParserOptions.inferIdentifierFromFileName(true);
        input = Files.list(Paths.get(Resources.getResourceAsFileLocation("consensus_alignment")))
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
    public void shouldRunAffinityAlignment() {
        AffinityAlignment affinityAlignment = AffinityAlignment.create()
                .inputStructuralMotifs(input)
                .run();
        Assert.assertEquals(3, affinityAlignment.getClusters().size());
        Assert.assertEquals(input.size(), affinityAlignment.getClusters().values().stream()
                .mapToInt(Collection::size)
                .sum());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInputOfDifferentSize() {
        input.get(0).addLeafSubstructure(new OakAminoAcid(new LeafIdentifier(0), AminoAcidFamily.ALANINE));
        AffinityAlignment.create()
                .inputStructuralMotifs(input)
                .run();
    }

}