package bio.singa.structure.model.families;

import java.util.Objects;

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

    public void setOneLetterCode(String oneLetterCode) {
        this.oneLetterCode = oneLetterCode;
    }

    @Override
    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    public void setThreeLetterCode(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
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
        return Objects.equals(oneLetterCode, that.oneLetterCode) &&
                Objects.equals(threeLetterCode, that.threeLetterCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oneLetterCode, threeLetterCode);
    }

    @Override
    public String toString() {
        return "LigandFamily{" +
                "oneLetterCode='" + oneLetterCode + '\'' +
                ", threeLetterCode='" + threeLetterCode + '\'' +
                '}';
    }
}
