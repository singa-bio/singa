package de.bioforscher.chemistry.physical;

import java.util.EnumMap;

/**
 * Created by Christoph on 22.09.2016.
 */
public class ResidueFactory {

    public static Residue createResidueFromAtoms(int identifier, ResidueType residueType, EnumMap<AtomName, Atom> atoms) {

        // create new residue
        Residue residue = new Residue(identifier, residueType);
        residue.addAllNodes(atoms.values());

        // backbone
        residue.connect(atoms.get(AtomName.N), atoms.get(AtomName.CA));
        residue.connect(atoms.get(AtomName.CA), atoms.get(AtomName.C));
        residue.connect(atoms.get(AtomName.C), atoms.get(AtomName.O));

        switch (residueType) {
            case ALANINE: {

                break;
            }
            default: {
                break;
            }
        }


        return residue;
    }


}



