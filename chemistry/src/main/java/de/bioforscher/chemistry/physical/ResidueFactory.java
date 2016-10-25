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
        // and add all atoms
        residue.addAllNodes(atoms.values());
        // connect backbone atoms first
        connectBackboneAtoms(residue, atoms);

        switch (residueType) {
            case ALANINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                break;
            }
            case ARGININE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(CD));
                residue.connect(atoms.get(NE), atoms.get(CZ));
                residue.connect(atoms.get(CZ), atoms.get(NH1));
                residue.connect(atoms.get(CZ), atoms.get(NH2));
                break;
            }
            case ASPARAGINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(OD1));
                residue.connect(atoms.get(CG), atoms.get(ND2));
                break;
            }
            case ASPARTIC_ACID: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(OD1));
                residue.connect(atoms.get(CG), atoms.get(OD2));
                break;
            }
            case CYSTEINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(SG));
                break;
            }
            case GLUTAMINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(CD));
                residue.connect(atoms.get(CD), atoms.get(OE1));
                residue.connect(atoms.get(CD), atoms.get(NE2));
                break;
            }
            case GLUTAMIC_ACID: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(CD));
                residue.connect(atoms.get(CD), atoms.get(OE1));
                residue.connect(atoms.get(CD), atoms.get(OE2));
                break;
            }
            case GLYCINE: {
                // nothing ...
            }
            case HISTIDINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(CD2));
                residue.connect(atoms.get(CD2), atoms.get(NE2));
                residue.connect(atoms.get(NE2), atoms.get(CE1));
                residue.connect(atoms.get(CE1), atoms.get(ND1));
                residue.connect(atoms.get(ND1), atoms.get(CG));
                break;
            }
            case ISOLEUCINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG1));
                residue.connect(atoms.get(CB), atoms.get(CG2));
                residue.connect(atoms.get(CG1), atoms.get(CD1));
                break;
            }
            case LEUCINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(CD1));
                residue.connect(atoms.get(CG), atoms.get(CD2));
                break;
            }
            case LYSINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                residue.connect(atoms.get(CB), atoms.get(CG));
                residue.connect(atoms.get(CG), atoms.get(CD));
                residue.connect(atoms.get(CD), atoms.get(CE));
                residue.connect(atoms.get(CE), atoms.get(NZ));
                break;
            }


            default: {
                break;
            }
        }


        return residue;
    }

    private static void connectBackboneAtoms(Residue residue, EnumMap<AtomName, Atom> atoms) {
        residue.connect(atoms.get(N), atoms.get(CA));
        residue.connect(atoms.get(CA), atoms.get(C));
        residue.connect(atoms.get(C), atoms.get(O));
    }

    private static void connectNTerminalAtoms(Residue residue, EnumMap<AtomName, Atom> atoms) {
        residue.connect(atoms.get(N), atoms.get(H));
        residue.connect(atoms.get(N), atoms.get(H2));
    }

    private static void connectCTerminaAtoms(Residue residue, EnumMap<AtomName, Atom> atoms) {
        residue.connect(atoms.get(C), atoms.get(OXT));
        residue.connect(atoms.get(OXT), atoms.get(HXT));
    }

    private static void saturateCarbon(Residue residue, EnumMap<AtomName, Atom> atoms, AtomName anyCarbon) {
        final int currentSaturation = residue.getAtomByName(anyCarbon).orElseThrow(IllegalArgumentException::new).getDegree();


        switch (anyCarbon) {
            case CA: {
                // decide whether tho add hydrogen atoms or not

                break;
            }

        }


    }


}



