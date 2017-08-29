package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureSelector;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Test;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

public class PlipShellGeneratorTest {

    @Test
    public void shouldFindShellsForLigand() {

        Structure structure = StructureParser.online()
                .pdbIdentifier("1c0a")
                .parse();

        Chain chain = structure.getFirstChain().get();

        LeafSubstructure<?,?> reference = StructureSelector.selectFrom(chain)
                .atomContainer(831)
                .selectAtomContainer();


        InteractionContainer interInteractions = PlipParser.parse("1c0a", getResourceAsStream("plip/1c0a.xml"));

        InteractionContainer ligandInteractions = PlipParser.parse("1c0a", getResourceAsStream("plip/1c0a_ligand.xml"));
        System.out.println();

        PlipShellGenerator.getInteractionShellsForLigand(chain, reference, interInteractions, ligandInteractions);
    }
}