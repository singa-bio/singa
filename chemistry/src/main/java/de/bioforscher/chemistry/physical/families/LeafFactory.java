package de.bioforscher.chemistry.physical.families;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.leafes.Residue;

import java.util.EnumMap;

import static de.bioforscher.chemistry.physical.atoms.AtomName.*;

/**
 * The residue factory is used to create residues from a set of Atoms with their AtomNames. This also connects the atoms
 * in the residues, where possible. No distance criterion is used but the knowledge of the residues and the usually
 * connected atoms. Different options can be set.
 */
public class LeafFactory {

    /**
     * the factory used to parse all residues.
     */
    private static final LeafFactory factory = new LeafFactory();

    /**
     * Tries to saturate the residue with hydrogen atoms if they are in the given map of atoms.
     */
    private boolean connectHydrogens = false;

    /**
     * Omits all hydrogen (and eventually deuterium) atoms. Those atoms are not added to the resulting residue.
     */
    private boolean omitHydrogens = false;

    private LeafFactory() {
    }

    public static Nucleotide createNucleotideFromAtoms(int identifier, NucleotideFamily nucleotideFamily, EnumMap<AtomName, Atom> atoms) {
        // create new Nucleotide
        Nucleotide nucleotide = new Nucleotide(identifier, nucleotideFamily);
        if (factory.omitHydrogens) {
            // without hydrogens
            atoms.values().stream()
                    .filter(atom -> !atom.isHydrogen())
                    .forEach(nucleotide::addNode);
        } else {
            // all
            atoms.values().forEach(nucleotide::addNode);
        }

        connectRibose(nucleotide, atoms);
        connectPhosphateGroup(nucleotide, atoms);
        nucleotide.addEdgeBetween(atoms.get(P), atoms.get(O5Pr));

        switch (nucleotideFamily) {
            case ADENOSINE: {
                nucleotide.addEdgeBetween(atoms.get(C2Pr), atoms.get(O2Pr));
                connectPurine(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C6), atoms.get(N6));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N9));
                break;
            }
            case CYTIDINE: {
                nucleotide.addEdgeBetween(atoms.get(C2Pr), atoms.get(O2Pr));
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(N4));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N1));
                break;
            }
            case DESOXYADENOSINE: {
                connectPurine(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C6), atoms.get(N6));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N9));
                break;
            }
            case DESOXYCYTIDINE: {
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(N4));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N1));
                break;
            }
            case DESOXYGUANOSINE: {
                connectPurine(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C6), atoms.get(O6));
                nucleotide.addEdgeBetween(atoms.get(C2), atoms.get(N2));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N9));
                break;
            }
            case DESOXYTHYMIDINE: {
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(O4));
                nucleotide.addEdgeBetween(atoms.get(C5), atoms.get(C7));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N1));
                break;
            }
            case DESOXYURIDINE: {
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(O4));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N1));
                break;
            }
            case GUANOSINE: {
                nucleotide.addEdgeBetween(atoms.get(C2Pr), atoms.get(O2Pr));
                connectPurine(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C6), atoms.get(O6));
                nucleotide.addEdgeBetween(atoms.get(C2), atoms.get(N2));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N9));
                break;
            }
            case THYMIDINE: {
                nucleotide.addEdgeBetween(atoms.get(C2Pr), atoms.get(O2Pr));
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(O4));
                nucleotide.addEdgeBetween(atoms.get(C5), atoms.get(C7));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N1));
                break;
            }
            case URIDINE: {
                nucleotide.addEdgeBetween(atoms.get(C2Pr), atoms.get(O2Pr));
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(O4));
                nucleotide.addEdgeBetween(atoms.get(C1Pr), atoms.get(N1));
                break;
            }
        }

        return nucleotide;
    }

    private static void connectRibose(Nucleotide nucleotide, EnumMap<AtomName, Atom> atoms) {
        connectInOrder(nucleotide, atoms, C1Pr, C2Pr, C3Pr, C4Pr, O4Pr, C1Pr);
        nucleotide.addEdgeBetween(atoms.get(C3Pr), atoms.get(O3Pr));
        nucleotide.addEdgeBetween(atoms.get(C4Pr), atoms.get(C5Pr));
        nucleotide.addEdgeBetween(atoms.get(C5Pr), atoms.get(O5Pr));
    }

    private static void connectPhosphateGroup(Nucleotide nucleotide, EnumMap<AtomName, Atom> atoms) {
        nucleotide.addEdgeBetween(atoms.get(P), atoms.get(OP1));
        nucleotide.addEdgeBetween(atoms.get(P), atoms.get(OP2));
        nucleotide.addEdgeBetween(atoms.get(P), atoms.get(OP3));
    }

    private static void connectPyrimidin(Nucleotide nucleotide, EnumMap<AtomName, Atom> atoms) {
        connectInOrder(nucleotide, atoms, N1, C2, N3, C4, C5, C6, N1);
        nucleotide.addEdgeBetween(atoms.get(C2), atoms.get(O2));
    }

    private static void connectPurine(Nucleotide nucleotide, EnumMap<AtomName, Atom> atoms) {
        connectInOrder(nucleotide, atoms, N1, C2, N3, C4, N9, C8, N7, C5, C6, N1);
        nucleotide.addEdgeBetween(atoms.get(C4), atoms.get(C5));
    }

    public static Residue createResidueFromAtoms(int identifier, ResidueFamily residueFamily, EnumMap<AtomName, Atom> atoms) {
        // create new Residue
        Residue residue = new Residue(identifier, residueFamily);
        // and add atoms
        if (factory.omitHydrogens) {
            // without hydrogens
            atoms.values().stream()
                    .filter(atom -> !atom.isHydrogen())
                    .forEach(residue::addNode);
        } else {
            // all
            atoms.values().forEach(residue::addNode);
        }
        // connect backbone atoms first
        connectBackboneAtoms(residue, atoms);

        // TODO maybe order by relative occurrence to speedup
        switch (residueFamily) {
            case ALANINE: {
                residue.addEdgeBetween(atoms.get(CA), atoms.get(CB));
                break;
            }
            case ARGININE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, NE, CZ, NH1);
                residue.addEdgeBetween(atoms.get(CZ), atoms.get(NH2));
                break;
            }
            case ASPARAGINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, OD1);
                residue.addEdgeBetween(atoms.get(CG), atoms.get(ND2));
                break;
            }
            case ASPARTIC_ACID: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, OD1);
                residue.addEdgeBetween(atoms.get(CG), atoms.get(OD2));
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
                residue.addEdgeBetween(atoms.get(CD), atoms.get(NE2));
                break;
            }
            case GLUTAMIC_ACID: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, OE1);
                residue.addEdgeBetween(atoms.get(CD), atoms.get(OE2));
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
                residue.addEdgeBetween(atoms.get(CB), atoms.get(CG2));
                break;
            }
            case LEUCINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD1);
                residue.addEdgeBetween(atoms.get(CG), atoms.get(CD2));
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
                residue.addEdgeBetween(atoms.get(CB), atoms.get(CG2));
                break;
            }
            case TRYPTOPHAN: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD, CD1, CE2, CE2, CZ2, CH2, CZ2, CE3, CD2, CG);
                residue.addEdgeBetween(atoms.get(CD2), atoms.get(CE2));
                break;
            }
            case TYROSINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG, CD1, CE1, CZ, CE2, CD2, CG);
                residue.addEdgeBetween(atoms.get(CZ), atoms.get(OH));
                break;
            }
            case VALINE: {
                connectInOrder(residue, atoms,
                        CA, CB, CG1);
                residue.addEdgeBetween(atoms.get(CB), atoms.get(CG2));
                break;
            }

            default: {
                break;
            }
        }
        return residue;
    }

    /**
     * Connects the atoms in the given order.
     *
     * @param substructure The residue to connect in.
     * @param atoms The atoms to take from.
     * @param names The names that should be connected.
     */
    private static void connectInOrder(LeafSubstructure substructure, EnumMap<AtomName, Atom> atoms, AtomName... names) {
        if (names.length < 2) {
            throw new IllegalArgumentException("Two or more atom names are required in order to connect them.");
        }
        for (int i = 1; i < names.length; i++) {
            substructure.addEdgeBetween(atoms.get(names[i - 1]), atoms.get(names[i]));
        }
    }

    /**
     * Connects the backbone atoms N-to-CA-to-C-to-O.
     * @param residue The residue to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectBackboneAtoms(Residue residue, EnumMap<AtomName, Atom> atoms) {
        residue.addEdgeBetween(atoms.get(N), atoms.get(CA));
        residue.addEdgeBetween(atoms.get(CA), atoms.get(C));
        residue.addEdgeBetween(atoms.get(C), atoms.get(O));
    }

    /**
     * Connects the N terminal Hydrogens N-to-H and N-to-H2.
     * @param residue The residue to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectNTerminalAtoms(Residue residue, EnumMap<AtomName, Atom> atoms) {
        if (factory.connectHydrogens) {
            residue.addEdgeBetween(atoms.get(N), atoms.get(H));
            residue.addEdgeBetween(atoms.get(N), atoms.get(H2));
        }
    }

    /**
     * Connects the C terminal Atoms C-to-OXT-to-HXT.
     * @param residue The residue to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectCTerminaAtoms(Residue residue, EnumMap<AtomName, Atom> atoms) {
        residue.addEdgeBetween(atoms.get(C), atoms.get(OXT));
        if (factory.connectHydrogens) {
            residue.addEdgeBetween(atoms.get(OXT), atoms.get(HXT));
        }
    }

    private static void saturateCarbon(Residue residue, EnumMap<AtomName, Atom> atoms, AtomName anyCarbon) {
        final int currentSaturation = residue.getAtomByName(anyCarbon).getDegree();

        switch (anyCarbon) {
            case CA: {


                break;
            }

        }


    }

    /**
     * Sets to omit all hydrogen (and deuterium) atoms. Those atoms are not added to the resulting residue.
     * @param omitHydrogens True, if no hydrogen should be parsed, false otherwise.
     */
    public static void setToOmitHydrogens(boolean omitHydrogens) {
        factory.omitHydrogens = omitHydrogens;
    }

}



