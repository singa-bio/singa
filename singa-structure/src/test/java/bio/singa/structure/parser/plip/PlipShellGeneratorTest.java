package bio.singa.structure.parser.plip;


import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.Ligand;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureSelector;
import org.junit.jupiter.api.Test;

import static bio.singa.core.utility.Resources.getResourceAsStream;

class PlipShellGeneratorTest {

    @Test
    void shouldFindShellsForLigand() {

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .parse();

        Chain chain = structure.getFirstChain();

        Ligand reference = StructureSelector.selectFrom(chain)
                .atomContainer(831)
                .selectAtomContainer();


        InteractionContainer interInteractions = PlipParser.parse("1c0a", getResourceAsStream("plip/1c0a.xml"));

        InteractionContainer ligandInteractions = PlipParser.parse("1c0a", getResourceAsStream("plip/1c0a_ligand.xml"));
        System.out.println();

        PlipShellGenerator.getInteractionShellsForLigand(chain, reference, interInteractions, ligandInteractions);
    }
}