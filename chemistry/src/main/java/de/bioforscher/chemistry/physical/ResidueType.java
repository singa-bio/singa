package de.bioforscher.chemistry.physical;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.physical.AtomName.*;

/**
 * Created by Christoph on 22.09.2016.
 */
public enum ResidueType {

    ALANINE("Alanine", "A", "ALA", ALANINE_ATOM_NAMES),
    CYSTEINE("Cysteine", "C", "CYS", CYSTEINE_ATOM_NAMES),
    GLYCINE("Glycine", "G", "GLY", GLYCINE_ATOM_NAMES),
    VALINE("Valine", "V", "VAL", VALINE_ATOM_NAMES),
    PROLINE("Proline", "P", "PRO", PROLINE_ATOM_NAMES);

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
        Set<String> actualNames = atoms.stream()
                                       .map(Atom::getName)
                                       .collect(Collectors.toSet());
        Set<String> expectedNames = residueType.getAllowedAtoms().stream()
                                               .map(AtomName::getName)
                                               .collect(Collectors.toSet());
        return expectedNames.containsAll(actualNames);
    }

    public static Optional<ResidueType> getResidueTypeByThreeLetterCode(String threeLetterCode) {
        return Arrays.stream(values()).filter(type -> threeLetterCode.trim().toUpperCase().equals(type
                .getThreeLetterCode())).findFirst();

    }
}
