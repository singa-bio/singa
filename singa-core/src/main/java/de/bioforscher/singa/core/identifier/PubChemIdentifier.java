package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph on 19.04.2016.
 */
public class PubChemIdentifier extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile("CID ([\\d]+)");

    public PubChemIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
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
