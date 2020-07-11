package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.features.databases.pubchem.PubChemDatabase;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;
import bio.singa.chemistry.features.databases.chebi.ChEBIDatabase;

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
