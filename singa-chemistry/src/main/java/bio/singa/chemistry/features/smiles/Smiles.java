package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.features.FeatureProviderRegistry;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;

/**
 * @author cl
 */
public class Smiles extends AbstractFeature<String> {

    public static final String SYMBOL = "SMILES";

    public Smiles(String smilesString, Evidence evidence) {
        super(smilesString, evidence);
    }

    public static void register() {
        FeatureProviderRegistry.addProviderForFeature(Smiles.class, SmilesProvider.class);
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }
}
