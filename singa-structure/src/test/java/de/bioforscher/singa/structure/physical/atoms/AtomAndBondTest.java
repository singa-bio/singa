package de.bioforscher.singa.structure.physical.atoms;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.oak.OakAtom;
import de.bioforscher.singa.structure.model.oak.OakBond;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class AtomAndBondTest {

    @Test
    public void shouldCreateCopyOfAtom() {
        OakAtom atom = new OakAtom(1, ElementProvider.CARBON, "CA", new Vector3D(1.0, 2.0, 3.0));
        Atom copiedAtom = atom.getCopy();

        // by contract
        // class is the same
        assertTrue(atom.getClass() == copiedAtom.getClass());
        // the atoms are NOT identical
        assertFalse(atom == copiedAtom);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertTrue(atom.equals(copiedAtom));
    }

    @Test
    public void shouldCreateCopyOfBond() {
        OakAtom source = new OakAtom(1, ElementProvider.HYDROGEN, "H", new Vector3D(1.0, 2.0, 3.0));
        OakAtom target = new OakAtom(2, ElementProvider.NITROGEN, "N", new Vector3D(4.0, 5.0, 6.0));

        OakBond bond = new OakBond(0);
        bond.setSource(source);
        bond.setTarget(target);
        OakBond copiedBond = new OakBond(bond);

        // by contract
        // class is the same
        assertTrue(bond.getClass() == copiedBond.getClass());
        // the atoms are NOT identical
        assertFalse(bond == copiedBond);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertTrue(bond.equals(copiedBond));
    }

}
