package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.model.Bond;
import de.bioforscher.mathematics.vectors.Vector3D;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Christoph on 16/11/2016.
 */
public class AtomAndBondTest {

    @Test
    public void shouldCreateCopyOfAtom() {
        Atom atom = new RegularAtom(1, ElementProvider.CARBON, "CA", new Vector3D(1.0, 2.0, 3.0));
        Atom copiedAtom = new RegularAtom(atom);

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
        Atom source = new RegularAtom(1, ElementProvider.HYDROGEN, "H", new Vector3D(1.0, 2.0, 3.0));
        Atom target = new RegularAtom(2, ElementProvider.NITROGEN, "N", new Vector3D(4.0, 5.0, 6.0));

        Bond bond = new Bond();
        bond.setSource(source);
        bond.setTarget(target);
        Bond copiedBond = new Bond(bond);

        // by contract
        // class is the same
        assertTrue(bond.getClass() == copiedBond.getClass());
        // the atoms are NOT identical
        assertFalse(bond == copiedBond);
        // but equal (all attributes that are crucial to the identification of the atom are equal)
        assertTrue(bond.equals(copiedBond));
    }


}
