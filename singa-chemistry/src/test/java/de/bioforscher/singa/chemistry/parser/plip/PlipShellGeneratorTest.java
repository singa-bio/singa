package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureSelector;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Test;

import java.io.InputStream;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

public class PlipShellGeneratorTest {

    @Test
    public void shouldFindShellsForLigand() {

        Structure structure = StructureParser.online()
                .pdbIdentifier("1acj")
                .parse();

        Chain chain = structure.getFirstChain().get();

        LeafSubstructure<?,?> reference = StructureSelector.selectFrom(chain)
                .atomContainer(999)
                .selectAtomContainer();

        InputStream inputStream = getResourceAsStream("plip/1c0a.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);

        PlipShellGenerator.getInteractionShellsForLigand(chain, reference, interactionContainer, null);
    }
}