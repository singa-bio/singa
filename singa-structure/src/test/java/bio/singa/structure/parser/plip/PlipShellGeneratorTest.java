package bio.singa.structure.parser.plip;


import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.Ligand;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakStructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureSelector;
import bio.singa.structure.parser.plip.PlipShellGenerator.InteractionShell;
import org.junit.jupiter.api.Test;

import static bio.singa.core.utility.Resources.getResourceAsStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        interInteractions.validateWithStructure((OakStructure) structure);

        InteractionContainer ligandInteractions = PlipParser.parse("1c0a", getResourceAsStream("plip/1c0a_ligand.xml"));
        ligandInteractions.validateWithStructure((OakStructure) structure);

        PlipShellGenerator interactionShellsForLigand = PlipShellGenerator.getInteractionShellsForLigand(chain, reference, interInteractions, ligandInteractions);
        assertEquals(16, interactionShellsForLigand.getShells().get(InteractionShell.FIRST).size());
        assertEquals(27, interactionShellsForLigand.getShells().get(InteractionShell.SECOND).size());
        assertEquals(33, interactionShellsForLigand.getShells().get(InteractionShell.THIRD).size());
    }
}