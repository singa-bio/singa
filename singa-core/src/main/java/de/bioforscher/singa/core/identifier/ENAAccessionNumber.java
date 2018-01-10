package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class ENAAccessionNumber extends AbstractIdentifier {

    public enum ExpressionType {
        GENOMIC_DNA, MRNA
    }

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[A-Z]{3}\\d{5}\\.\\d+");

    private ExpressionType expressionType;

    /**
     * Creates a new identifier by validating it with the given pattern.
     *
     * @param identifier The new identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public ENAAccessionNumber(String identifier, ExpressionType expressionType) throws IllegalArgumentException {
        super(identifier, PATTERN);
        this.expressionType = expressionType;
    }

    public ExpressionType getExpressionType() {
        return expressionType;
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
     * Searches the first ENA Accession Number in a collection of identifiers and returns it.
     *
     * @param identifiers A collection of identifiers.
     * @return The first ENA Accession Number identifier or an empty optional if no identifier could be found.
     */
    public static Optional<Identifier> find(Collection<Identifier> identifiers) {
        for (Identifier identifier : identifiers) {
            if (check(identifier)) {
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

}
