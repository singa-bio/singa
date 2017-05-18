package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

public class UniProtIdentifier extends AbstractIdentifier {

    // http://www.uniprot.org/help/accession_numbers

    public static final Pattern PATTERN = Pattern.compile("[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");

    public UniProtIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public static boolean check(Identifier identifier) {
        return PATTERN.matcher(identifier.toString()).matches();
    }

    public static Optional<Identifier> find(Collection<Identifier> identifiers) {
        for (Identifier identifier : identifiers) {
            if (UniProtIdentifier.check(identifier)) {
                return Optional.of(identifier);
            }
        }
        return Optional.empty();
    }

    public static Pattern getPattern() {
        return PATTERN;
    }

}
