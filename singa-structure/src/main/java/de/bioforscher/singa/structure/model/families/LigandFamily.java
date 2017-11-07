package de.bioforscher.singa.structure.model.families;

/**
 * @author cl
 */
public class LigandFamily implements StructuralFamily<LigandFamily> {

    private String oneLetterCode;
    private String threeLetterCode;

    public LigandFamily(String threeLetterCode) {
        this("?", threeLetterCode);
    }

    public LigandFamily(String oneLetterCode, String threeLetterCode) {
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    @Override
    public String getOneLetterCode() {
        return oneLetterCode;
    }

    @Override
    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    @Override
    public int compareTo(LigandFamily o) {
        return String.CASE_INSENSITIVE_ORDER.compare(getThreeLetterCode(), o.getThreeLetterCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LigandFamily that = (LigandFamily) o;

        return threeLetterCode != null ? threeLetterCode.equals(that.threeLetterCode) : that.threeLetterCode == null;
    }

    @Override
    public int hashCode() {
        return threeLetterCode != null ? threeLetterCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LigandFamily{" +
                "oneLetterCode='" + oneLetterCode + '\'' +
                ", threeLetterCode='" + threeLetterCode + '\'' +
                '}';
    }
}
