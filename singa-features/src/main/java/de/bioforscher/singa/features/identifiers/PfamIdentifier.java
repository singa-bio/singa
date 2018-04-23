package de.bioforscher.singa.features.identifiers;

import de.bioforscher.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * The identifier used by the <a href="http://pfam.xfam.org/">Pfam database</a>.
 * The identifier starts with "PF" and is followed by a 5-digit number.
 *
 * @author fk
 */
public class PfamIdentifier extends AbstractIdentifier<PfamIdentifier> {

    /**
     * The {@link Pattern} to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("PF[0-9]{5}");

    public PfamIdentifier(String identifier) {
        super(identifier, PATTERN);
    }

    @Override
    public PfamIdentifier getFeatureContent() {
        return this;
    }
}
