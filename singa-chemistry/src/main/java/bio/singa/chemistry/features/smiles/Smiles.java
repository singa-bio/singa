package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.features.FeatureRegistry;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;

/**
 * @author cl
 */
public class Smiles extends AbstractFeature<String> {

    public static final String SYMBOL = "SMILES";

    public Smiles(String smilesString, FeatureOrigin featureOrigin) {
        super(smilesString, featureOrigin);
    }

    public static void register() {
        FeatureRegistry.addProviderForFeature(Smiles.class, SmilesProvider.class);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
