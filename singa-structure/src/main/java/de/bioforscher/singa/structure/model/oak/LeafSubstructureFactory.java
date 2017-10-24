package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.families.LigandFamily;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.families.StructuralFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions;

import java.util.Map;

/**
 * The residue factory is used to create residues from a set of AtomFilter with their AtomNames. This also connects the atoms
 * in the residues, where possible. No distance criterion is used but the knowledge of the residues and the usually
 * connected atoms. Different options can be set.
 */
public class LeafSubstructureFactory {

    private LeafSubstructureFactory() {
    }

    public static OakLeafSubstructure<?> createLeafSubstructure(LeafIdentifier leafIdentifier, StructuralFamily family) {
        if (family instanceof AminoAcidFamily) {
            return new OakAminoAcid(leafIdentifier, (AminoAcidFamily) family);
        } else if (family instanceof NucleotideFamily) {
            return new OakNucleotide(leafIdentifier, (NucleotideFamily) family);
        }
        return new OakLigand(leafIdentifier, (LigandFamily) family);
    }

    public static OakNucleotide createNucleotideFromAtoms(LeafIdentifier leafIdentifier, NucleotideFamily nucleotideFamily, Map<String, OakAtom> atoms, StructureParserOptions options) {
        // create new Nucleotide
        OakNucleotide nucleotide = new OakNucleotide(leafIdentifier, nucleotideFamily);
        if (options.isOmittingHydrogen()) {
            // without hydrogens
            atoms.values().stream()
                    .filter(atom -> atom.getElement().getProtonNumber() != 1)
                    .forEach(nucleotide::addAtom);
        } else {
            atoms.values().forEach(nucleotide::addAtom);
        }

        if (options.isCreatingEdges()) {
            connectRibose(nucleotide, atoms);
            connectPhosphateGroup(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("P"), atoms.get("O5'"));
            switch (nucleotideFamily) {
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
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                    break;
                }
                case DESOXYGUANOSINE: {
                    connectPurine(nucleotide, atoms);
                    nucleotide.addBondBetween(atoms.get("C6"), atoms.get("O6"));
                    nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N2"));
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
                    break;
                }
                case DESOXYTHYMIDINE: {
                    connectPyrimidin(nucleotide, atoms);
                    nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"));
                    nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C7"));
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                    break;
                }
                case DESOXYURIDINE: {
                    connectPyrimidin(nucleotide, atoms);
                    nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"));
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                    break;
                }
                case GUANOSINE: {
                    nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                    connectPurine(nucleotide, atoms);
                    nucleotide.addBondBetween(atoms.get("C6"), atoms.get("O6"));
                    nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N2"));
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
                    break;
                }
                case THYMIDINE: {
                    nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                    connectPyrimidin(nucleotide, atoms);
                    nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"));
                    nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C7"));
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                    break;
                }
                case URIDINE: {
                    nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
                    connectPyrimidin(nucleotide, atoms);
                    nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"));
                    nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
                    break;
                }
            }
        }

        return nucleotide;
    }

    private static void connectRibose(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        connectInOrder(nucleotide, atoms, "C1'", "C2'", "C3'", "C4'", "O4'", "C1'");
        nucleotide.addBondBetween(atoms.get("C3'"), atoms.get("O3'"));
        nucleotide.addBondBetween(atoms.get("C4'"), atoms.get("C5'"));
        nucleotide.addBondBetween(atoms.get("C5'"), atoms.get("O5'"));
    }

    private static void connectPhosphateGroup(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP1"));
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP2"));
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP3"));
    }

    private static void connectPyrimidin(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        connectInOrder(nucleotide, atoms, "N1", "C2", "N3", "C4", "C5", "C6", "N1");
        nucleotide.addBondBetween(atoms.get("C2"), atoms.get("O2"));
    }

    private static void connectPurine(OakNucleotide nucleotide, Map<String, OakAtom> atoms) {
        connectInOrder(nucleotide, atoms, "N1", "C2", "N3", "C4", "N9", "C8", "N7", "C5", "C6", "N1");
        nucleotide.addBondBetween(atoms.get("C4"), atoms.get("C5"));
    }

    public static OakAminoAcid createAminoAcidFromAtoms(LeafIdentifier leafIdentifier, AminoAcidFamily aminoAcidFamily, Map<String, OakAtom> atoms, StructureParserOptions options) {
        // create new AminoAcid
        OakAminoAcid aminoAcid = new OakAminoAcid(leafIdentifier, aminoAcidFamily);
        // and add atoms
        if (options.isOmittingHydrogen()) {
            // without hydrogens
            atoms.values().stream()
                    .filter(atom -> atom.getElement().getProtonNumber() != 1)
                    .forEach(aminoAcid::addAtom);
        } else {
            // all
            atoms.values().forEach(aminoAcid::addAtom);
        }

        if (options.isCreatingEdges()) {
            // connect backbone atoms first
            connectBackboneAtoms(aminoAcid, atoms);

            // TODO maybe order by relative occurrence to speedup
            switch (aminoAcidFamily) {
                case ALANINE: {
                    aminoAcid.addBondBetween(atoms.get("CA"), atoms.get("CB"));
                    break;
                }
                case ARGININE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD", "NE", "CZ", "NH1");
                    aminoAcid.addBondBetween(atoms.get("CZ"), atoms.get("NH2"));
                    break;
                }
                case ASPARAGINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "OD1");
                    aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("ND2"));
                    break;
                }
                case ASPARTIC_ACID: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "OD1");
                    aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("OD2"));
                    break;
                }
                case CYSTEINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "SG");
                    break;
                }
                case GLUTAMINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD", "OE1");
                    aminoAcid.addBondBetween(atoms.get("CD"), atoms.get("NE2"));
                    break;
                }
                case GLUTAMIC_ACID: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD", "OE1");
                    aminoAcid.addBondBetween(atoms.get("CD"), atoms.get("OE2"));
                    break;
                }
                case GLYCINE: {
                    // nothing ...
                    break;
                }
                case HISTIDINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD2", "NE2", "CE1", "ND1", "CG");
                    break;
                }
                case ISOLEUCINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG1", "CD1");
                    aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
                    break;
                }
                case LEUCINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD1");
                    aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("CD2"));
                    break;
                }
                case LYSINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD", "CE", "NZ");
                    break;
                }
                case METHIONINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "SD", "CE");
                    break;
                }
                case PHENYLALANINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD2", "CE2", "CZ", "CE1", "CD1", "CG");
                    break;
                }
                case PROLINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD", "N", "CA");
                    break;
                }
                case SERINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "OG");
                    break;
                }
                case THREONINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "OG1");
                    aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
                    break;
                }
                case TRYPTOPHAN: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD", "CD1", "CE2", "CZ2", "CH2", "CZ2", "CE3", "CD2", "CG");
                    aminoAcid.addBondBetween(atoms.get("CD2"), atoms.get("CE2"));
                    break;
                }
                case TYROSINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG", "CD1", "CE1", "CZ", "CE2", "CD2", "CG");
                    aminoAcid.addBondBetween(atoms.get("CZ"), atoms.get("OH"));
                    break;
                }
                case VALINE: {
                    connectInOrder(aminoAcid, atoms,
                            "CA", "CB", "CG1");
                    aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
                    break;
                }
                default: {
                    break;
                }
            }
        }

        return aminoAcid;
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

    /**
     * Connects the backbone atoms N-to-CA-to-C-to-O.
     * @param aminoAcid The aminoAcid to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectBackboneAtoms(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms) {
        aminoAcid.addBondBetween(atoms.get("N"), atoms.get("CA"));
        aminoAcid.addBondBetween(atoms.get("CA"), atoms.get("C"));
        aminoAcid.addBondBetween(atoms.get("C"), atoms.get("O"));
    }

    /**
     * Connects the N terminal Hydrogens N-to-H and N-to-H2.
     * @param aminoAcid The aminoAcid to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectNTerminalAtoms(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms, StructureParserOptions options) {
        if (options.isConnectingHydrogens()) {
            aminoAcid.addBondBetween(atoms.get("N"), atoms.get("H"));
            aminoAcid.addBondBetween(atoms.get("N"), atoms.get("H2"));
        }
    }

    /**
     * Connects the C terminal AtomFilter C-to-OXT-to-HXT.
     * @param aminoAcid The aminoAcid to connect in.
     * @param atoms The atoms to take from.
     */
    private static void connectCTerminaAtoms(OakAminoAcid aminoAcid, Map<String, OakAtom> atoms, StructureParserOptions options) {
        aminoAcid.addBondBetween(atoms.get("C"), atoms.get("OXT"));
        if (options.isConnectingHydrogens()) {
            aminoAcid.addBondBetween(atoms.get("OXT"), atoms.get("HXT"));
        }
    }

}



