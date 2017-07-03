package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.regex.Pattern;

/**
 * The IUPAC International Chemical Identifier is a textual identifier for chemical substances, designed to provide a
 * standard way to encode molecular information and to facilitate the search for such information in databases and on
 * the web.
 * <p>
 * The InChIKey is a short, fixed-length character signature based on a hash code of the InChI string.
 *
 * @author cl
 * @see <a href="http://www.inchi-trust.org/technical-faq/#2.1">Enzyme Nomenclature</a>
 */
public class InChIKey extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[A-Z]{14}-[A-Z]{10}-[A-Z]");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public InChIKey(String identifier) throws IllegalArgumentException {
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
