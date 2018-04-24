package de.bioforscher.singa.chemistry.descriptive.features.identifiers;

import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemDatabase;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.features.identifiers.PubChemIdentifier;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

public class InChIKeyProvider extends FeatureProvider<InChIKey> {

    public InChIKeyProvider() {
        setProvidedFeature(InChIKey.class);
        addRequirement(ChEBIIdentifier.class);
        addFallbackRequirement(100, PubChemIdentifier.class);
    }

    @Override
    public <FeatureableType extends Featureable> InChIKey provide(FeatureableType featureable) {
        switch (getPreferredStrategyIndex()) {
            case 0: {
                ChEBIIdentifier chEBIIdentifier = featureable.getFeature(ChEBIIdentifier.class);
                return ChEBIDatabase.fetchInchiKey(chEBIIdentifier);
            }
            case 100: {
                PubChemIdentifier pubChemIdentifier = featureable.getFeature(PubChemIdentifier.class);
                return PubChemDatabase.fetchInchiKey(pubChemIdentifier);
            }
        }
        return null;
    }



}
