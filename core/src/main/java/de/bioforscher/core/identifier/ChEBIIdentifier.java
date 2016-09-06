package de.bioforscher.core.identifier;

import de.bioforscher.core.identifier.model.AbstractIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChEBIIdentifier extends AbstractIdentifier {

    // https://www.ebi.ac.uk/chebi/faqForward.do#5

    public static final Pattern PATTERN = Pattern.compile("CHEBI:([\\d]+)");

    public ChEBIIdentifier(String identifier) throws IllegalArgumentException {
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
