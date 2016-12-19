package de.bioforscher.chemistry.parser.pdb.structures.tokens;

import java.util.regex.Pattern;

/**
 * Created by Christoph on 09/11/2016.
 */
public class TerminatorTokens {

    public static final Pattern MODEL_TERMINATOR = Pattern.compile("^ENDMDL.*");
    public static final Pattern CHAIN_TERMINATOR = Pattern.compile("^TER   .*");

}
