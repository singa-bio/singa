package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.vectors.Vector3D;

import static de.bioforscher.chemistry.descriptive.elements.TableOfElements.CARBON;
import static de.bioforscher.chemistry.descriptive.elements.TableOfElements.HYDROGEN;
import static de.bioforscher.chemistry.physical.BondType.COVALENT_BOND;

public class MoleculeFactory {

    public static Molecule createMethane() {
        Molecule methane = new Molecule();
        methane.addNode(new Atom(1, new Vector3D(100, 100, 0), CARBON));
        methane.addNode(new Atom(2, new Vector3D(90, 90, 0), HYDROGEN));
        methane.addNode(new Atom(3, new Vector3D(110, 90, 0), HYDROGEN));
        methane.addNode(new Atom(4, new Vector3D(90, 110, 0), HYDROGEN));
        methane.addNode(new Atom(5, new Vector3D(110, 110, 0), HYDROGEN));
        methane.connect(1, methane.getNode(1), methane.getNode(2), COVALENT_BOND);
        methane.connect(2, methane.getNode(1), methane.getNode(3), COVALENT_BOND);
        methane.connect(3, methane.getNode(1), methane.getNode(4), COVALENT_BOND);
        methane.connect(4, methane.getNode(1), methane.getNode(5), COVALENT_BOND);
        return methane;
    }

}
