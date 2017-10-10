package de.bioforscher.singa.chemistry.algorithms.superimposition;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void shouldRunAffinityAlignment() {
        AffinityAlignment affinityAlignment = new AffinityAlignment(this.input, null, true, StructuralEntityFilter.AtomFilter.isArbitrary());

    }
}