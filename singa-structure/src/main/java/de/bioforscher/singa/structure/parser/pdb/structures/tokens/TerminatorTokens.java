package de.bioforscher.singa.structure.parser.pdb.structures.tokens;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class TerminatorTokens {

    public static final Pattern MODEL_TERMINATOR = Pattern.compile("^ENDMDL.*");
    public static final Pattern CHAIN_TERMINATOR = Pattern.compile("^TER {3}.*");

}
