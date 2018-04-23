package de.bioforscher.singa.structure.model.families;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author cl
 */
public enum NucleotideFamily implements StructuralFamily<NucleotideFamily> {

    ADENOSINE("A", "A"),
    DESOXYADENOSINE("A", "dA"),
    GUANOSINE("G", "G"),
    DESOXYGUANOSINE("G", "dG"),
    THYMIDINE("T", "T"),
    DESOXYTHYMIDINE("T", "dT"),
    URIDINE("U", "U"),
    DESOXYURIDINE("U", "dU"),
    CYTIDINE("C", "C"),
    DESOXYCYTIDINE("C", "dC"),
    UNKNOWN("X", "UNK");

    private final String oneLetterCode;
    private final String threeLetterCode;

    NucleotideFamily(String oneLetterCode, String threeLetterCode) {
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    public static Optional<NucleotideFamily> getNucleotideByThreeLetterCode(String threeLetterCode) {
        return Arrays.stream(values())
                .filter(type -> threeLetterCode.trim().equalsIgnoreCase(type.getThreeLetterCode()))
                .findAny();
    }

    public static Optional<NucleotideFamily> getNucleotide(char nucleotide) {
        switch (nucleotide) {
            case 'A': case 'a': return Optional.of(ADENOSINE);
            case 'C': case 'c': return Optional.of(CYTIDINE);
            case 'G': case 'g': return Optional.of(GUANOSINE);
            case 'T': case 't': return Optional.of(THYMIDINE);
            case 'U': case 'u': return Optional.of(DESOXYURIDINE);
            default: return Optional.empty();
        }
    }

    @Override
    public String getOneLetterCode() {
        return oneLetterCode;
    }

    @Override
    public String getThreeLetterCode() {
        return threeLetterCode;
    }

}
