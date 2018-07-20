package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.FeatureOrigin;

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
public class InChIKey extends AbstractIdentifier<InChIKey> {

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

    public InChIKey(String identifier, FeatureOrigin origin) throws IllegalArgumentException {
        super(identifier, PATTERN, origin);
    }

    @Override
    public InChIKey getFeatureContent() {
        return this;
    }
}
