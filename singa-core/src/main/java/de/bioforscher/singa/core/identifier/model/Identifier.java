package de.bioforscher.singa.core.identifier.model;

import java.util.regex.Pattern;

public interface Identifier {

    String getIdentifier();

    Pattern getPattern();

}
