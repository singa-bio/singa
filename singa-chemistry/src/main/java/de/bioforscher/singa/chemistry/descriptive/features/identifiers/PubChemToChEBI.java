package de.bioforscher.singa.chemistry.descriptive.features.identifiers;

import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemDatabase;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.PubChemIdentifier;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

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
