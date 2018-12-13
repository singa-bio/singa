package bio.singa.features.identifiers;


import bio.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * This identifier is used to identify functional classes of enzymes. <p> Every enzyme code consists of the letters "EC"
 * followed by four numbers separated by periods. Those numbers represent a progressively finer classification of the
 * enzyme.
 *
 * @author cl
 * @see <a href="http://www.chem.qmul.ac.uk/iubmb/enzyme/">Enzyme Nomenclature</a>
 */
public class ECNumber extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("^([1-7])$|^([1-7])\\.([\\d-]{1,2})$|^([1-7])\\.([\\d-]{1,2})\\.([\\d-]{1,2})-$|^([1-7])\\.([\\d-]{1,2})\\.([\\d-]{1,2})\\.([n\\d-]{1,3})$");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public ECNumber(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    /**
     * Returns the pattern used to validate the identifier.
     *
     * @return The pattern used to validate the identifier.
     */
    public static Pattern getPattern() {
        return PATTERN;
    }

}
