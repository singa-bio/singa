package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.features.databases.chebi.ChEBIDatabase;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;

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
