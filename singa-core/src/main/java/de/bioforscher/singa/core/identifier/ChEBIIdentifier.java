package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This identifier is used by the <a href="https://www.ebi.ac.uk/chebi/">ChEBI Database</a> to identify small molecules.
 * <p>
 * The identifier consists of a unique number preceded by the "CHEBI:" suffix.
 *
 * @author cl
 * @see <a href="https://www.ebi.ac.uk/chebi/faqForward.do#5">ChEBI identifier</a>
 */
public class ChEBIIdentifier extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("CHEBI:([\\d]+)");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public ChEBIIdentifier(String identifier) throws IllegalArgumentException {
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
     * Searches a valid PubChem identifier in a collection of identifiers and returns it.
     *
     * @param identifiers A collection of identifiers.
     * @return The first PubChem identifier or an empty optional if no identifier could be found.
     */
    public static Optional<Identifier> find(Collection<Identifier> identifiers) {
        for (Identifier identifier : identifiers) {
            if (ChEBIIdentifier.check(identifier)) {
                return Optional.of(identifier);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the pattern used to validate the identifier.
     *
     * @return The pattern used to validate the identifier.
     */
    public static Pattern getPattern() {
        return PATTERN;
    }

    /**
     * Returns the consecutive number without the "CHEBI:" part.
     *
     * @return The consecutive number without the "CHEBI:" part.
     */
    public int getConsecutiveNumber() {
        Matcher matcherCHEBI = PATTERN.matcher(getIdentifier());
        if (matcherCHEBI.matches()) {
            return Integer.parseInt(matcherCHEBI.group(1));
        }  else {
            // should no be possible
            throw new IllegalStateException("This identifier has been created with an unexpected pattern.");
        }
    }

}
