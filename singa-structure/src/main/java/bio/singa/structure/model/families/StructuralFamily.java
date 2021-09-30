package bio.singa.structure.model.families;

/**
 * A {@link StructuralFamily} defines a PDB-conform label that can at least be expressed in one-letter and/or
 * three-letter code.
 *
 * @author fk
 */
public class StructuralFamily implements Comparable<StructuralFamily> {

    private final String oneLetterCode;
    private final String threeLetterCode;

    public StructuralFamily(String oneLetterCode, String threeLetterCode) {
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    public String getOneLetterCode() {
        return oneLetterCode;
    }

    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    @Override
    public int compareTo(StructuralFamily o) {
        return String.CASE_INSENSITIVE_ORDER.compare(getThreeLetterCode(), o.getThreeLetterCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StructuralFamily that = (StructuralFamily) o;

        return threeLetterCode != null ? threeLetterCode.equals(that.threeLetterCode) : that.threeLetterCode == null;
    }

    @Override
    public int hashCode() {
        return threeLetterCode != null ? threeLetterCode.hashCode() : 0;
    }
}
