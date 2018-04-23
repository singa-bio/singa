package de.bioforscher.singa.features.identifiers;

import de.bioforscher.singa.features.identifiers.model.AbstractIdentifier;
import de.bioforscher.singa.features.model.FeatureOrigin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This identifier is used by the <a href="https://pubchem.ncbi.nlm.nih.gov/">PubChem Database</a> to identify small
 * molecules.
 * <p>
 * The identifier usually only contains numbers. Sometimes the number is preceded by "CID:". To make this identifier
 * more "identifiable" we require the suffix "CID:".
 *
 * @author cl
 */
public class PubChemIdentifier extends AbstractIdentifier<PubChemIdentifier> {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("CID:([\\d]+)");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public PubChemIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public PubChemIdentifier(String identifier, FeatureOrigin origin) throws IllegalArgumentException {
        super(identifier, PATTERN, origin);
    }

    /**
     * Returns the consecutive number without the "CID:" part.
     *
     * @return The consecutive number without the "CID:" part.
     */
    public int getConsecutiveNumber() {
        Matcher matcherCHEBI = PATTERN.matcher(getIdentifier());
        if (matcherCHEBI.matches()) {
            return Integer.parseInt(matcherCHEBI.group(1));
        } else {
            // should no be possible
            throw new IllegalStateException("This identifier has been created with an unexpected pattern.");
        }
    }

    @Override
    public PubChemIdentifier getFeatureContent() {
        return this;
    }
}
