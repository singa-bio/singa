package de.bioforscher.singa.chemistry.descriptive.features.smiles;

import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIDatabase;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

/**
 * @author cl
 */
public class SmilesProvider extends FeatureProvider<Smiles> {

    private final ChEBIDatabase chEBIDatabase = ChEBIDatabase.getInstance();

    public SmilesProvider() {
        setProvidedFeature(Smiles.class);
        addRequirement(ChEBIIdentifier.class);
    }

    @Override
    public <FeatureableType extends Featureable> Smiles provide(FeatureableType featureable) {
        return ChEBIDatabase.fetchSmiles(featureable);
    }

}
