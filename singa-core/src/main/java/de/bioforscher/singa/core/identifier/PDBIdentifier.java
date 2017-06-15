package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class PDBIdentifier extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile("[0-9][A-Za-z0-9]{3}");

    public PDBIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    public static String extractFirst(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
