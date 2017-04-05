package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;

import java.util.regex.Pattern;

public class ECNumber extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile("^([1-6])$|^([1-6])\\.(\\d{1,2})$|^([1-6])\\.(\\d{1,2})\\.(\\d{1,2})$|^([1-6])\\.(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,3})$");

    public ECNumber(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

}
