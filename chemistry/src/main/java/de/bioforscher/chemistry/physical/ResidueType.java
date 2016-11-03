package de.bioforscher.chemistry.physical;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.physical.AtomName.*;

/**
 * Created by Christoph on 22.09.2016.
 */
public enum ResidueType {

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
    VALINE("Valine", "V", "Val", VALINE_ATOM_NAMES);

    private String name;
    private String oneLetterCode;
    private String threeLetterCode;
    private EnumSet<AtomName> allowedAtoms;

    ResidueType(String name, String oneLetterCode, String threeLetterCode, EnumSet<AtomName> allowedAtoms) {
        this.name = name;
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
        this.allowedAtoms = allowedAtoms;
    }

    public String getName() {
        return this.name;
    }

    public String getOneLetterCode() {
        return this.oneLetterCode;
    }

    public String getThreeLetterCode() {
        return this.threeLetterCode;
    }

    public EnumSet<AtomName> getAllowedAtoms() {
        return this.allowedAtoms;
    }

    /**
     * Returns true if the set of Atoms contains only Atom names, that can occur in the  given residue type.
     * @param atoms The atoms to be checked.
     * @param residueType The expected type of residue.
     * @return true if the set of Atoms contains only Atom names, that can occur in the  given residue type.
     */
    public boolean containsExpectedAtoms(List<Atom> atoms, ResidueType residueType) {
        final Set<String> actualNames = atoms.stream()
                                       .map(Atom::getName)
                                       .collect(Collectors.toSet());
        final Set<String> expectedNames = residueType.getAllowedAtoms().stream()
                                               .map(AtomName::getName)
                                               .collect(Collectors.toSet());
        return expectedNames.containsAll(actualNames);
    }

    public static Optional<ResidueType> getResidueTypeByThreeLetterCode(String threeLetterCode) {
        return Arrays.stream(values())
                .filter(type -> threeLetterCode.equalsIgnoreCase(type.getThreeLetterCode()))
                .findFirst();

    }
}
