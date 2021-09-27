package bio.singa.structure.model.oak;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.LigandFamily;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;

import java.util.Map;
import java.util.Set;

/**
 * The residue factory is used to create residues from a set of AtomFilter with their AtomNames. This also connects the
 * atoms in the residues, where possible. No distance criterion is used but the knowledge of the residues and the
 * usually connected atoms. Different options can be set.
 */
public class LeafSubstructureFactory {

    private LeafSubstructureFactory() {

    }

    public static OakLeafSubstructure<?> createLeafSubstructure(LeafIdentifier leafIdentifier, StructuralFamily<?> family) {
        if (family instanceof AminoAcidFamily) {
            return new OakAminoAcid(leafIdentifier, (AminoAcidFamily) family);
        } else if (family instanceof NucleotideFamily) {
            return new OakNucleotide(leafIdentifier, (NucleotideFamily) family);
        }
        return new OakLigand(leafIdentifier, (LigandFamily) family);
    }

    public static void connectNucleotide(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        connectRibose(nucleotide, atoms);
        connectPhosphateGroup(nucleotide, atoms);
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("O5'"));
        switch (nucleotide.getFamily()) {
            case ADENOSINE: {
                nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                connectPurine(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N6"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
                break;
            }
            case CYTIDINE: {
                nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C4"), atoms.get("N4"));
                nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                break;
            }
            case DESOXYADENOSINE: {
                connectPurine(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N6"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
                break;
            }
            case DESOXYCYTIDINE: {
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C4"), atoms.get("N4"));
                nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                break;
            }
            case DESOXYGUANOSINE: {
                connectPurine(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N1"));
                nucleotide.addBondBetween(atoms.get("C6"), atoms.get("O6"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N2"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
                break;
            }
            case DESOXYTHYMIDINE: {
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
                nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C7"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                break;
            }
            case DESOXYURIDINE: {
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
                nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                break;
            }
            case GUANOSINE: {
                nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                connectPurine(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N1"));
                nucleotide.addBondBetween(atoms.get("C6"), atoms.get("O6"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N2"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
                break;
            }
            case THYMIDINE: {
                nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
                nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C7"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                break;
            }
            case URIDINE: {
                nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                connectPyrimidin(nucleotide, atoms);
                nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
                nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
                nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                break;
            }
        }
    }

    private static void connectRibose(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        connectInOrder(nucleotide, atoms, "C1'", "C2'", "C3'", "C4'", "O4'", "C1'");
        nucleotide.addBondBetween(atoms.get("C3'"), atoms.get("O3'"));
        nucleotide.addBondBetween(atoms.get("C4'"), atoms.get("C5'"));
        nucleotide.addBondBetween(atoms.get("C5'"), atoms.get("O5'"));
    }

    private static void connectPhosphateGroup(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP1"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP2"));
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP3"));
    }

    private static void connectPyrimidin(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        nucleotide.addBondBetween(atoms.get("N1"), atoms.get("C2"));
        nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N3"));
        nucleotide.addBondBetween(atoms.get("C4"), atoms.get("C5"));
        nucleotide.addBondBetween(atoms.get("C2"), atoms.get("O2"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("C5"), atoms.get("O6"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N1"));
    }

    private static void connectPurine(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        nucleotide.addBondBetween(atoms.get("N1"), atoms.get("C2"));
        nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N3"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
        nucleotide.addBondBetween(atoms.get("C4"), atoms.get("N9"));
        nucleotide.addBondBetween(atoms.get("N9"), atoms.get("C8"));
        nucleotide.addBondBetween(atoms.get("C8"), atoms.get("N7"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("N7"), atoms.get("C5"));
        nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C6"));
        nucleotide.addBondBetween(atoms.get("C4"), atoms.get("C5"), CovalentBondType.DOUBLE_BOND);
    }

    public static OakLeafSubstructure<?> createLeafSubstructure(LeafIdentifier leafIdentifier, StructuralFamily<?> family, Set<OakAtom> atoms) {
        OakLeafSubstructure<?> leafSubstructure;
        if (family instanceof AminoAcidFamily) {
            leafSubstructure = new OakAminoAcid(leafIdentifier, (AminoAcidFamily) family);
        } else if (family instanceof NucleotideFamily) {
            leafSubstructure = new OakNucleotide(leafIdentifier, (NucleotideFamily) family);
        } else {
            leafSubstructure = new OakLigand(leafIdentifier, (LigandFamily) family);
        }
        atoms.forEach(leafSubstructure::addAtom);
        return leafSubstructure;
    }

    public static void connectAminoAcid(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms) {
            // connect backbone atoms first
            connectBackboneAtoms(aminoAcid, atoms);
            // TODO maybe order by relative occurrence to speedup
            switch (aminoAcid.getFamily()) {
                case ALANINE: {
                    aminoAcid.addBondBetween(atoms.get("CA"), atoms.get("CB"));
                    break;
                }
                case ARGININE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "NE", "CZ", "NH1");
                    aminoAcid.addBondBetween(atoms.get("CZ"), atoms.get("NH2"), CovalentBondType.DOUBLE_BOND);
                    break;
                }
                case ASPARAGINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "ND2");
                    aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("OD1"), CovalentBondType.DOUBLE_BOND);
                    break;
                }
                case ASPARTIC_ACID: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "OD2");
                    aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("OD1"), CovalentBondType.DOUBLE_BOND);
                    break;
                }
                case CYSTEINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "SG");
                    break;
                }
                case GLUTAMINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "NE2");
                    aminoAcid.addBondBetween(atoms.get("CD"), atoms.get("OE1"), CovalentBondType.DOUBLE_BOND);
                    break;
                }
                case GLUTAMIC_ACID: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "OE2");
                    aminoAcid.addBondBetween(atoms.get("CD"), atoms.get("OE1"), CovalentBondType.DOUBLE_BOND);
                    break;
                }
                case GLYCINE: {
                    // nothing ...
                    break;
                }
                case HISTIDINE: {
                    connectHistidine(aminoAcid, atoms);
                    break;
                }
                case ISOLEUCINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG1", "CD1");
                    aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
                    break;
                }
                case LEUCINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD1");
                    aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("CD2"));
                    break;
                }
                case LYSINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "CE", "NZ");
                    break;
                }
                case METHIONINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "SD", "CE");
                    break;
                }
                case PHENYLALANINE: {
                    connectPhenylalanine(aminoAcid, atoms);
                    break;
                }
                case PROLINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "N", "CA");
                    break;
                }
                case SERINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "OG");
                    break;
                }
                case THREONINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "OG1");
                    aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
                    break;
                }
                case TRYPTOPHAN: {
                    connectTryptophan(aminoAcid, atoms);
                    break;
                }
                case TYROSINE: {
                    connectPhenylalanine(aminoAcid, atoms);
                    aminoAcid.addBondBetween(atoms.get("CZ"), atoms.get("OH"));
                    break;
                }
                case VALINE: {
                    connectInOrder(aminoAcid, atoms, "CA", "CB", "CG1");
                    aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
                    break;
                }
                default: {
                    break;
                }
            }
    }

    /**
     * Connects the atoms in the given order.
     *
     * @param substructure The residue to connect in.
     * @param atoms The atoms to take from.
     * @param names The names that should be connected.
     */
    private static void connectInOrder(OakLeafSubstructure<?> substructure, Map<String, OakAtom> atoms, String... names) {
        if (names.length < 2) {
            throw new IllegalArgumentException("Two or more atom names are required in order to connect them.");
        }
        for (int i = 1; i < names.length; i++) {
            substructure.addBondBetween(atoms.get(names[i - 1]), atoms.get(names[i]));
        }
    }

    private static void connectPhenylalanine(OakLeafSubstructure<?> substructure, Map<String, OakAtom> atoms) {
        substructure.addBondBetween(atoms.get("CA"), atoms.get("CB"));
        substructure.addBondBetween(atoms.get("CB"), atoms.get("CG"));
        substructure.addBondBetween(atoms.get("CG"), atoms.get("CD2"));
        substructure.addBondBetween(atoms.get("CD2"), atoms.get("CE2"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CE2"), atoms.get("CZ"));
        substructure.addBondBetween(atoms.get("CZ"), atoms.get("CE1"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CE1"), atoms.get("CD1"));
        substructure.addBondBetween(atoms.get("CD1"), atoms.get("CG"), CovalentBondType.DOUBLE_BOND);

    }

    private static void connectTryptophan(OakLeafSubstructure<?> substructure, Map<String, OakAtom> atoms) {
        substructure.addBondBetween(atoms.get("CA"), atoms.get("CB"));
        substructure.addBondBetween(atoms.get("CB"), atoms.get("CG"));
        substructure.addBondBetween(atoms.get("CG"), atoms.get("CD2"));
        substructure.addBondBetween(atoms.get("CD2"), atoms.get("CE3"));
        substructure.addBondBetween(atoms.get("CE3"), atoms.get("CZ3"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CZ3"), atoms.get("CH2"));
        substructure.addBondBetween(atoms.get("CH2"), atoms.get("CZ2"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CZ2"), atoms.get("CE2"));
        substructure.addBondBetween(atoms.get("CE2"), atoms.get("CD2"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CE2"), atoms.get("NE1"));
        substructure.addBondBetween(atoms.get("NE1"), atoms.get("CD1"));
        substructure.addBondBetween(atoms.get("CD1"), atoms.get("CG"), CovalentBondType.DOUBLE_BOND);
    }

    private static void connectHistidine(OakLeafSubstructure<?> substructure, Map<String, OakAtom> atoms) {
        substructure.addBondBetween(atoms.get("CA"), atoms.get("CB"));
        substructure.addBondBetween(atoms.get("CB"), atoms.get("CG"));
        substructure.addBondBetween(atoms.get("CG"), atoms.get("CD2"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CD2"), atoms.get("NE2"));
        substructure.addBondBetween(atoms.get("NE2"), atoms.get("CE1"));
        substructure.addBondBetween(atoms.get("CE1"), atoms.get("ND1"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("ND1"), atoms.get("CG"));
    }

    /**
     * Connects the backbone atoms N-to-CA-to-C-to-O.
     *
     * @param aminoAcid The aminoAcid to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectBackboneAtoms(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms) {
        aminoAcid.addBondBetween(atoms.get("N"), atoms.get("CA"));
        aminoAcid.addBondBetween(atoms.get("CA"), atoms.get("C"));
        aminoAcid.addBondBetween(atoms.get("C"), atoms.get("O"), CovalentBondType.DOUBLE_BOND);
    }

    /**
     * Connects the N terminal Hydrogens N-to-H and N-to-H2.
     *
     * @param aminoAcid The aminoAcid to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectNTerminalAtoms(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms, StructureParserOptions options) {
        if (!options.isOmittingHydrogen()) {
            aminoAcid.addBondBetween(atoms.get("N"), atoms.get("H"));
            aminoAcid.addBondBetween(atoms.get("N"), atoms.get("H2"));
        }
    }

    /**
     * Connects the C terminal AtomFilter C-to-OXT-to-HXT.
     *
     * @param aminoAcid The aminoAcid to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectCTerminaAtoms(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms, StructureParserOptions options) {
        aminoAcid.addBondBetween(atoms.get("C"), atoms.get("OXT"));
        if (!options.isOmittingHydrogen()) {
            aminoAcid.addBondBetween(atoms.get("OXT"), atoms.get("HXT"));
        }
    }

}



