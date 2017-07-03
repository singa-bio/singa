package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

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
    public static final Pattern PATTERN = Pattern.compile("^([1-6])$|^([1-6])\\.(\\d{1,2})$|^([1-6])\\.(\\d{1,2})\\.(\\d{1,2})$|^([1-6])\\.(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,3})$");

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
     * Returns true, if the identifier is valid.
     *
     * @param identifier The identifier.
     * @return True, if the identifier is valid.
     */
    public static boolean check(Identifier identifier) {
        return PATTERN.matcher(identifier.toString()).matches();
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
