package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.features.FeatureProviderRegistry;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

/**
 * @author cl
 */
public class Smiles extends QualitativeFeature<String> {

    public Smiles(String smilesString, Evidence evidence) {
        super(smilesString, evidence);
    }

    public static void register() {
        FeatureProviderRegistry.addProviderForFeature(Smiles.class, SmilesProvider.class);
    }

}
