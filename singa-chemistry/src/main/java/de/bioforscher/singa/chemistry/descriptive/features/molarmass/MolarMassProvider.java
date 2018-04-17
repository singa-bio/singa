package de.bioforscher.singa.chemistry.descriptive.features.molarmass;

import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtDatabase;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;

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
        // use ChEBI
        MolarMass molarMass = ChEBIDatabase.fetchMolarMass(featureable);
        if (molarMass != null) {
            return molarMass;
        }
        // use UniProt
        molarMass = UniProtDatabase.fetchMolarMass(featureable);
        if (molarMass != null) {
            return molarMass;
        }
        return null;
    }
}
