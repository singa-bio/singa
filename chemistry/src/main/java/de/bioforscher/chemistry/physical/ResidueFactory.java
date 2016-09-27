package de.bioforscher.chemistry.physical;

import java.util.EnumMap;

import static de.bioforscher.chemistry.physical.AtomName.*;

/**
 * Created by Christoph on 22.09.2016.
 */
public class ResidueFactory {

    public static Residue createResidueFromAtoms(int identifier, ResidueType residueType, EnumMap<AtomName, Atom> atoms) {

        // create new residue
        Residue residue = new Residue(identifier, residueType);
        residue.addAllNodes(atoms.values());

        // backbone
        residue.connect(atoms.get(N), atoms.get(CA));
        residue.connect(atoms.get(CA), atoms.get(C));
        residue.connect(atoms.get(C), atoms.get(O));

        switch (residueType) {
            case ALANINE: {
                // c alpha hydrogen
                residue.connect(atoms.get(CA), atoms.get(HA));
                // side chain
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(HB1));
                residue.connect(atoms.get(CB), atoms.get(HB2));
                residue.connect(atoms.get(CB), atoms.get(HB3));
                // if this is first or last residue
                residue.connect(atoms.get(N), atoms.get(H));
                residue.connect(atoms.get(N), atoms.get(H2));
                residue.connect(atoms.get(C), atoms.get(OXT));
                residue.connect(atoms.get(OXT), atoms.get(HXT));
                break;
            }
            case CYSTEINE: {
                // c alpha hydrogen
                residue.connect(atoms.get(CA), atoms.get(HA));
                // side chain
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(HB1));
                residue.connect(atoms.get(CB), atoms.get(HB2));
                residue.connect(atoms.get(CB), atoms.get(SG));
                residue.connect(atoms.get(SG), atoms.get(HG));
                // if this is first or last residue
                residue.connect(atoms.get(N), atoms.get(H));
                residue.connect(atoms.get(N), atoms.get(H2));
                residue.connect(atoms.get(C), atoms.get(OXT));
                residue.connect(atoms.get(OXT), atoms.get(HXT));
                break;
            }
            default: {
                break;
            }
        }


        return residue;
    }


}



