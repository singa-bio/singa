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
            int identifier = Integer.parseInt(matcherCHEBI.group(1));
            return identifier;
        } else {
            return 0;
        }
    }

}
