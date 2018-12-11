package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.features.FeatureRegistry;
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
        FeatureRegistry.addProviderForFeature(Smiles.class, SmilesProvider.class);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
