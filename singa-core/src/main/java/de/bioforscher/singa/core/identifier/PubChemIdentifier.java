package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class PubChemIdentifier extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile("CID:([\\d]+)");

    public PubChemIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public static boolean check(Identifier identifier) {
        return PATTERN.matcher(identifier.toString()).matches();
    }

    public static Optional<Identifier> find(Collection<Identifier> identifiers) {
        for (Identifier identifier : identifiers) {
            if (PubChemIdentifier.check(identifier)) {
                return Optional.of(identifier);
            }
        }
        return Optional.empty();
    }

    public static Pattern getPattern() {
        return PATTERN;
    }

    public int getConsecutiveNumber() {
        Matcher matcherCHEBI = PATTERN.matcher(getIdentifier());
        if (matcherCHEBI.matches()) {
            return Integer.parseInt(matcherCHEBI.group(1));
        } else {
            return 0;
        }
    }
}
