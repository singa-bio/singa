package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.Evidence;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class PDBLigandIdentifier extends AbstractIdentifier<PDBLigandIdentifier> {

    public static final Pattern PATTERN = Pattern.compile("([A-Z0-9]{3})");

    public PDBLigandIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public PDBLigandIdentifier(String identifier, Evidence featureOrigin) throws IllegalArgumentException {
        super(identifier, PATTERN, featureOrigin);
    }

    @Override
    public PDBLigandIdentifier getFeatureContent() {
        return this;
    }

}
