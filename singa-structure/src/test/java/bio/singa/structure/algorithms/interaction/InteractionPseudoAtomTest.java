package bio.singa.structure.algorithms.interaction;

import bio.singa.structure.parser.plip.InteractionContainer;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author fk
 */
public class InteractionPseudoAtomTest {

    @Test
    @Ignore
    public void shouldCreatePseudoAtomsFromInteraction() {

        InteractionContainer interactionContainer = null;
        InteractionPseudoAtom pseudoAtom = InteractionPseudoAtom.of(interactionContainer.getInteractions().get(0));
//        pseudoAtom.getMode() == PseudoAtomMode.CENTER
//        pseudoAtom.getType() = InteractionType.HYDROGEN_BOND
//        pseudoAtom.getDirection() = InteractionDirection.LIGAND_PROTEIN
        pseudoAtom.getPosition();
    }
}