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

        // TODO maybe order by relative occurrence to speedup
        switch (residueType) {
            case ALANINE: {
                residue.connect(atoms.get(CA), atoms.get(CB));
                break;
            }
            case ARGININE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, NE, CZ, NH1);
                residue.connect(atoms.get(CZ), atoms.get(NH2));
                break;
            }
            case ASPARAGINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, OD1);
                residue.connect(atoms.get(CG), atoms.get(ND2));
                break;
            }
            case ASPARTIC_ACID: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, OD1);
                residue.connect(atoms.get(CG), atoms.get(OD2));
                break;
            }
            case CYSTEINE: {
                connectInOrder(residue, atoms,
                        CA, CB, SG);
                break;
            }
            case GLUTAMINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, OE1);
                residue.connect(atoms.get(CD), atoms.get(NE2));
                break;
            }
            case GLUTAMIC_ACID: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, OE1);
                residue.connect(atoms.get(CD), atoms.get(OE2));
                break;
            }
            case GLYCINE: {
                // nothing ...
            }
            case HISTIDINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD2, NE2, CE1, ND1, CG);
                break;
            }
            case ISOLEUCINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG1, CD1);
                residue.connect(atoms.get(CB), atoms.get(CG2));
                break;
            }
            case LEUCINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD1);
                residue.connect(atoms.get(CG), atoms.get(CD2));
                break;
            }
            case LYSINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, CE, NZ);
                break;
            }
            case METHIONINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, SD, CE);
                break;
            }
            case PHENYLALANINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD2, CE2, CZ, CE1, CD1, CG);
                break;
            }
            case PROLINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, N, CA);
                break;
            }
            case SERINE: {
                connectInOrder(residue, atoms,
                        CA, CB, OG);
                break;
            }
            case THREONINE: {
                connectInOrder(residue, atoms,
                        CA, CB, OG1);
                residue.connect(atoms.get(CB), atoms.get(CG2));
                break;
            }
            case TRYPTOPHAN: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, CD1, CE2, CE2, CZ2, CH2, CZ2, CE3, CD2, CG);
                residue.connect(atoms.get(CD2), atoms.get(CE2));
                break;
            }
            case TYROSINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD1, CE1, CZ, CE2, CD2, CG);
                residue.connect(atoms.get(CZ), atoms.get(OH));
                break;
            }
            case VALINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG1);
                residue.connect(atoms.get(CB), atoms.get(CG2));
                break;
            }

            default: {
                break;
            }
        }
        return residue;
    }

    private static void connectInOrder(Residue residue, EnumMap<AtomName, Atom> atoms, AtomName... names) {
        if (names.length < 2) {
            throw new IllegalArgumentException("Two or more atom names are required in order to connect them.");
        }
        for (int i = 1; i < names.length; i++) {
            residue.connect(atoms.get(names[i-1]), atoms.get(names[i]));
        }
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



