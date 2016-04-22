package de.bioforscher.core.identifier;

import de.bioforscher.core.identifier.model.AbstractIdentifier;

import java.util.regex.Pattern;

public class UniProtIdentifier extends AbstractIdentifier {

    // http://www.uniprot.org/help/accession_numbers

    public static final Pattern PATTERN = Pattern.compile("[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");

    public UniProtIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

}
