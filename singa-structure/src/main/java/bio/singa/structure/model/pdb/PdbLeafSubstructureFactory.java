package bio.singa.structure.model.pdb;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;

import java.util.Map;
import java.util.Set;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.*;
import static bio.singa.structure.model.families.StructuralFamilies.Nucleotides.*;

/**
 * The residue factory is used to create residues from a set of AtomFilter with their AtomNames. This also connects the
 * atoms in the residues, where possible. No distance criterion is used but the knowledge of the residues and the
 * usually connected atoms. Different options can be set.
 */
public class PdbLeafSubstructureFactory {

    private PdbLeafSubstructureFactory() {

    }

    public static PdbLeafSubstructure createLeafSubstructure(PdbLeafIdentifier leafIdentifier, StructuralFamily family) {
        if (StructuralFamilies.AminoAcids.isAminoAcid(family)) {
            return new PdbAminoAcid(leafIdentifier, family);
        } else if (StructuralFamilies.Nucleotides.isNucleotide(family)) {
            return new PdbNucleotide(leafIdentifier, family);
        }
        return new PdbLigand(leafIdentifier, family);
    }

    public static PdbLeafSubstructure createLeafSubstructure(PdbLeafIdentifier leafIdentifier, StructuralFamily family, Set<PdbAtom> atoms) {
        PdbLeafSubstructure leafSubstructure = createLeafSubstructure(leafIdentifier, family);
        atoms.forEach(leafSubstructure::addAtom);
        return leafSubstructure;
    }

    public static void connectNucleotide(PdbNucleotide nucleotide, Map<String, PdbAtom> atoms) {
        connectRibose(nucleotide, atoms);
        connectPhosphateGroup(nucleotide, atoms);
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("O5'"));
        if (ADENOSINE.equals(nucleotide.getFamily())) {
            nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
            connectPurine(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N6"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
        } else if (CYTIDINE.equals(nucleotide.getFamily())) {
            nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
            connectPyrimidin(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C4"), atoms.get("N4"));
            nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
        } else if (DESOXYADENOSINE.equals(nucleotide.getFamily())) {
            connectPurine(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N6"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
        } else if (DESOXYCYTIDINE.equals(nucleotide.getFamily())) {
            connectPyrimidin(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C4"), atoms.get("N4"));
            nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
        } else if (DESOXYGUANOSINE.equals(nucleotide.getFamily())) {
            connectPurine(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N1"));
            nucleotide.addBondBetween(atoms.get("C6"), atoms.get("O6"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N2"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
        } else if (DESOXYTHYMIDINE.equals(nucleotide.getFamily())) {
            connectPyrimidin(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
            nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C7"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
        } else if (DESOXYURIDINE.equals(nucleotide.getFamily())) {
            connectPyrimidin(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
            nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
        } else if (GUANOSINE.equals(nucleotide.getFamily())) {
            nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
            connectPurine(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N1"));
            nucleotide.addBondBetween(atoms.get("C6"), atoms.get("O6"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N2"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N9"));
        } else if (THYMIDINE.equals(nucleotide.getFamily())) {
            nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
            connectPyrimidin(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
            nucleotide.addBondBetween(atoms.get("C5"), atoms.get("C7"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
        } else if (URIDINE.equals(nucleotide.getFamily())) {
            nucleotide.addBondBetween(atoms.get("C2'"), atoms.get("O2'"));
            connectPyrimidin(nucleotide, atoms);
            nucleotide.addBondBetween(atoms.get("C4"), atoms.get("O4"), CovalentBondType.DOUBLE_BOND);
            nucleotide.addBondBetween(atoms.get("N3"), atoms.get("C4"));
            nucleotide.addBondBetween(atoms.get("C1'"), atoms.get("N1"));
        }
    }

    private static void connectRibose(PdbNucleotide nucleotide, Map<String, PdbAtom> atoms) {
        connectInOrder(nucleotide, atoms, "C1'", "C2'", "C3'", "C4'", "O4'", "C1'");
        nucleotide.addBondBetween(atoms.get("C3'"), atoms.get("O3'"));
        nucleotide.addBondBetween(atoms.get("C4'"), atoms.get("C5'"));
        nucleotide.addBondBetween(atoms.get("C5'"), atoms.get("O5'"));
    }

    private static void connectPhosphateGroup(PdbNucleotide nucleotide, Map<String, PdbAtom> atoms) {
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP1"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP2"));
        nucleotide.addBondBetween(atoms.get("P"), atoms.get("OP3"));
    }

    private static void connectPyrimidin(PdbNucleotide nucleotide, Map<String, PdbAtom> atoms) {
        nucleotide.addBondBetween(atoms.get("N1"), atoms.get("C2"));
        nucleotide.addBondBetween(atoms.get("C2"), atoms.get("N3"));
        nucleotide.addBondBetween(atoms.get("C4"), atoms.get("C5"));
        nucleotide.addBondBetween(atoms.get("C2"), atoms.get("O2"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("C5"), atoms.get("O6"), CovalentBondType.DOUBLE_BOND);
        nucleotide.addBondBetween(atoms.get("C6"), atoms.get("N1"));
    }

    private static void connectPurine(PdbNucleotide nucleotide, Map<String, PdbAtom> atoms) {
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

    public static void connectAminoAcid(PdbAminoAcid aminoAcid, Map<String, PdbAtom> atoms) {
        // connect backbone atoms first
        connectBackboneAtoms(aminoAcid, atoms);
        if (ALANINE.equals(aminoAcid.getFamily())) {
            aminoAcid.addBondBetween(atoms.get("CA"), atoms.get("CB"));
        } else if (ARGININE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "NE", "CZ", "NH1");
            aminoAcid.addBondBetween(atoms.get("CZ"), atoms.get("NH2"), CovalentBondType.DOUBLE_BOND);
        } else if (ASPARAGINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "ND2");
            aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("OD1"), CovalentBondType.DOUBLE_BOND);
        } else if (ASPARTIC_ACID.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "OD2");
            aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("OD1"), CovalentBondType.DOUBLE_BOND);
        } else if (CYSTEINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "SG");
        } else if (GLUTAMINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "NE2");
            aminoAcid.addBondBetween(atoms.get("CD"), atoms.get("OE1"), CovalentBondType.DOUBLE_BOND);
        } else if (GLUTAMIC_ACID.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "OE2");
            aminoAcid.addBondBetween(atoms.get("CD"), atoms.get("OE1"), CovalentBondType.DOUBLE_BOND);
        } else if (HISTIDINE.equals(aminoAcid.getFamily())) {
            connectHistidine(aminoAcid, atoms);
        } else if (ISOLEUCINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG1", "CD1");
            aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
        } else if (LEUCINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD1");
            aminoAcid.addBondBetween(atoms.get("CG"), atoms.get("CD2"));
        } else if (LYSINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "CE", "NZ");
        } else if (METHIONINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "SD", "CE");
        } else if (PHENYLALANINE.equals(aminoAcid.getFamily())) {
            connectPhenylalanine(aminoAcid, atoms);
        } else if (PROLINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG", "CD", "N", "CA");
        } else if (SERINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "OG");
        } else if (THREONINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "OG1");
            aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
        } else if (TRYPTOPHAN.equals(aminoAcid.getFamily())) {
            connectTryptophan(aminoAcid, atoms);
        } else if (TYROSINE.equals(aminoAcid.getFamily())) {
            connectPhenylalanine(aminoAcid, atoms);
            aminoAcid.addBondBetween(atoms.get("CZ"), atoms.get("OH"));
        } else if (VALINE.equals(aminoAcid.getFamily())) {
            connectInOrder(aminoAcid, atoms, "CA", "CB", "CG1");
            aminoAcid.addBondBetween(atoms.get("CB"), atoms.get("CG2"));
        }
    }

    /**
     * Connects the atoms in the given order.
     *
     * @param substructure The residue to connect in.
     * @param atoms The atoms to take from.
     * @param names The names that should be connected.
     */
    private static void connectInOrder(PdbLeafSubstructure substructure, Map<String, PdbAtom> atoms, String... names) {
        if (names.length < 2) {
            throw new IllegalArgumentException("Two or more atom names are required in order to connect them.");
        }
        for (int i = 1; i < names.length; i++) {
            substructure.addBondBetween(atoms.get(names[i - 1]), atoms.get(names[i]));
        }
    }

    private static void connectPhenylalanine(PdbLeafSubstructure substructure, Map<String, PdbAtom> atoms) {
        substructure.addBondBetween(atoms.get("CA"), atoms.get("CB"));
        substructure.addBondBetween(atoms.get("CB"), atoms.get("CG"));
        substructure.addBondBetween(atoms.get("CG"), atoms.get("CD2"));
        substructure.addBondBetween(atoms.get("CD2"), atoms.get("CE2"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CE2"), atoms.get("CZ"));
        substructure.addBondBetween(atoms.get("CZ"), atoms.get("CE1"), CovalentBondType.DOUBLE_BOND);
        substructure.addBondBetween(atoms.get("CE1"), atoms.get("CD1"));
        substructure.addBondBetween(atoms.get("CD1"), atoms.get("CG"), CovalentBondType.DOUBLE_BOND);

    }

    private static void connectTryptophan(PdbLeafSubstructure substructure, Map<String, PdbAtom> atoms) {
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

    private static void connectHistidine(PdbLeafSubstructure substructure, Map<String, PdbAtom> atoms) {
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
    private static void connectBackboneAtoms(PdbAminoAcid aminoAcid, Map<String, PdbAtom> atoms) {
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
    private static void connectNTerminalAtoms(PdbAminoAcid aminoAcid, Map<String, PdbAtom> atoms, StructureParserOptions options) {
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
    private static void connectCTerminalAtoms(PdbAminoAcid aminoAcid, Map<String, PdbAtom> atoms, StructureParserOptions options) {
        aminoAcid.addBondBetween(atoms.get("C"), atoms.get("OXT"));
        if (!options.isOmittingHydrogen()) {
            aminoAcid.addBondBetween(atoms.get("OXT"), atoms.get("HXT"));
        }
    }

}



