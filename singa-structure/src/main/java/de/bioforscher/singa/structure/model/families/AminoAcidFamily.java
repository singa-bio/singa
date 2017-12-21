package de.bioforscher.singa.structure.model.families;

import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.oak.AtomName;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.structure.model.oak.AtomName.*;

/**
 * A {@link AminoAcidFamily} contains the general information of a type of amino acid.
 *
 * @author cl
 */
public enum AminoAcidFamily implements StructuralFamily<AminoAcidFamily> {

    ALANINE("Alanine", "A", "Ala", ALANINE_ATOM_NAMES),
    ARGININE("Arginine", "R", "Arg", ARGININE_ATOM_NAMES),
    ASPARAGINE("Asparagine", "N", "Asn", ASPARAGINE_ATOM_NAMES),
    ASPARTIC_ACID("Aspartic acid", "D", "Asp", ASPARTIC_ACID_ATOM_NAMES),
    CYSTEINE("Cysteine", "C", "Cys", CYSTEINE_ATOM_NAMES),
    GLUTAMINE("Glutamine", "Q", "Gln", GLUTAMINE_ATOM_NAMES),
    GLUTAMIC_ACID("Glutamic acid", "E", "Glu", GLUTAMIC_ACID_ATOM_NAMES),
    GLYCINE("Glycine", "G", "Gly", GLYCINE_ATOM_NAMES),
    HISTIDINE("Histidine", "H", "His", HISTIDINE_ATOM_NAMES),
    ISOLEUCINE("Isoleucine", "I", "Ile", ISOLEUCINE_ATOM_NAMES),
    LEUCINE("Leucine", "L", "Leu", LEUCINE_ATOM_NAMES),
    LYSINE("Lysine", "K", "Lys", LYSINE_ATOM_NAMES),
    METHIONINE("Methionine", "M", "Met", METHIONINE_ATOM_NAMES),
    PHENYLALANINE("Phenylalanine", "F", "Phe", PHENYLALANINE_ATOM_NAMES),
    PROLINE("Proline", "P", "Pro", PROLINE_ATOM_NAMES),
    SERINE("Serine", "S", "Ser", SERINE_ATOM_NAMES),
    THREONINE("Threonine", "T", "Thr", THREONINE_ATOM_NAMES),
    TRYPTOPHAN("Tryptophan", "W", "Trp", TRYPTOPHAN_ATOM_NAMES),
    TYROSINE("Tyrosine", "Y", "Tyr", TYROSINE_ATOM_NAMES),
    VALINE("Valine", "V", "Val", VALINE_ATOM_NAMES),
    UNKNOWN("Unknown", "X", "Unk", UNKNOWN_ATOM_NAMES);

    private static final String RESIDUE_PROTOTYPES_BASE_DIR = "de/bioforscher/singa/structure/leaves/prototypes/";
    private final String name;
    private final String oneLetterCode;
    private final String threeLetterCode;
    private final EnumSet<AtomName> allowedAtoms;

    AminoAcidFamily(String name, String oneLetterCode, String threeLetterCode, EnumSet<AtomName> allowedAtoms) {
        this.name = name;
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
        this.allowedAtoms = allowedAtoms;
    }

    public static Optional<AminoAcidFamily> getAminoAcidTypeByThreeLetterCode(String threeLetterCode) {
        return Arrays.stream(values())
                .filter(type -> threeLetterCode.equalsIgnoreCase(type.getThreeLetterCode()))
                .findAny();
    }

    public static Optional<AminoAcidFamily> getAminoAcidTypeByOneLetterCode(String oneLetterCode) {
        return Arrays.stream(values())
                .filter(type -> oneLetterCode.equalsIgnoreCase(type.getOneLetterCode()))
                .findAny();
    }

    public String getName() {
        return name;
    }

    @Override
    public String getOneLetterCode() {
        return oneLetterCode;
    }

    @Override
    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    public EnumSet<AtomName> getAllowedAtoms() {
        return allowedAtoms;
    }

    /**
     * Returns true if the set of AtomFilter contains only Atom names, that can occur in the given residue type.
     *
     * @param atoms The atoms to be checked.
     * @param aminoAcidFamily The expected type of residue.
     * @return True, if the set of AtomFilter contains only Atom names, that can occur in the given residue type.
     */
    public boolean containsExpectedAtoms(List<Atom> atoms, AminoAcidFamily aminoAcidFamily) {
        final Set<String> actualNames = atoms.stream()
                .map(Atom::getAtomName)
                .collect(Collectors.toSet());
        final Set<String> expectedNames = aminoAcidFamily.getAllowedAtoms().stream()
                .map(AtomName::getName)
                .collect(Collectors.toSet());
        return expectedNames.containsAll(actualNames);
    }

    /**
     * Returns a prototype of the {@link AminoAcid} that are deposited in the project resources.
     *
     * @return A {@link AminoAcid} prototype.
     */
    public AminoAcid getPrototype() {
        // potentially replace with (AminoAcid) LigandParserService.parseLeafSubstructureById(getThreeLetterCode());
        return StructureParser.local()
                .inputStream(Resources.getResourceAsStream(RESIDUE_PROTOTYPES_BASE_DIR + getName().replaceAll(" ", "_").toLowerCase() + ".pdb"))
                .parse()
                .getAllAminoAcids()
                .get(0);
    }
}
