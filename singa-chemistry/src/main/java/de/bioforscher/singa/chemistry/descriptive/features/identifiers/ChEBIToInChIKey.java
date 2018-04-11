package de.bioforscher.singa.chemistry.descriptive.features.identifiers;

import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIDatabase;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

public class ChEBIToInChIKey extends FeatureProvider<InChIKey> {

    private final ChEBIDatabase chEBIDatabase = ChEBIDatabase.getInstance();

    public ChEBIToInChIKey() {
        setProvidedFeature(InChIKey.class);
        addRequirement(ChEBIIdentifier.class);
    }

    @Override
    public <FeatureableType extends Featureable> InChIKey provide(FeatureableType featureable) {
        ChEBIIdentifier chEBIIdentifier = featureable.getFeature(ChEBIIdentifier.class);
        return ChEBIDatabase.fetchInchiKey(chEBIIdentifier);
    }

}
