package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.features.databases.pubchem.PubChemDatabase;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;

/**
 * @author cl
 */
public class PubChemToChEBI extends FeatureProvider<ChEBIIdentifier> {

    public PubChemToChEBI() {
        setProvidedFeature(ChEBIIdentifier.class);
        addRequirement(PubChemIdentifier.class);
    }

    @Override
    public <FeatureableType extends Featureable> ChEBIIdentifier provide(FeatureableType featureable) {
        PubChemIdentifier pubChemIdentifier = featureable.getFeature(PubChemIdentifier.class);
        return PubChemDatabase.fetchChEBIIdentifier(pubChemIdentifier);
    }


}
