package bio.singa.structure.features;

import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;
import bio.singa.structure.parser.chebi.ChEBIDatabase;

public class ChEBIToInChIKey extends FeatureProvider<InChIKey> {

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
