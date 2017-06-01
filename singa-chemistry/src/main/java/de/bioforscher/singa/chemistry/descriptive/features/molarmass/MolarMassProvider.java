package de.bioforscher.singa.chemistry.descriptive.features.molarmass;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtDatabase;
import de.bioforscher.singa.units.features.model.FeatureProvider;
import de.bioforscher.singa.units.features.model.Featureable;

/**
 * @author cl
 */
public class MolarMassProvider extends FeatureProvider<MolarMass> {

    private final ChEBIDatabase chEBIDatabase = ChEBIDatabase.getInstance();
    private final UniProtDatabase uniProtDatabase = UniProtDatabase.getInstance();

    public MolarMassProvider() {
        setProvidedFeature(MolarMass.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarMass provide(FeatureableType featureable) {
        // mass is parsed from databases
        if (featureable.getClass().equals(Species.class)) {
            // use ChEBI
            return ChEBIDatabase.fetchMolarMass(featureable);
        } else if (featureable.getClass().equals(Enzyme.class) || featureable.getClass().equals(Protein.class)) {
            // use UniProt
            return UniProtDatabase.fetchMolarMass(featureable);
        } else {
            return null;
        }
    }
}
