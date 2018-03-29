package de.bioforscher.singa.features.identifiers;

import de.bioforscher.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * This identifier is used by the NCBI to uniquely identify any organism on the <a
 * href="https://www.ncbi.nlm.nih.gov/taxonomy">NCBI Taxonomy Database</a>.
 * <p>
 * The identifier can only contain numbers.
 *
 * @author cl
 */
public class NCBITaxonomyIdentifier extends AbstractIdentifier<NCBITaxonomyIdentifier> {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[\\d]+");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public NCBITaxonomyIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    @Override
    public NCBITaxonomyIdentifier getFeatureContent() {
        return this;
    }
}
