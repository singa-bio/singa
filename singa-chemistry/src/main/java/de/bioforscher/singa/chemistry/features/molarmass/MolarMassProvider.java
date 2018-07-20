package de.bioforscher.singa.chemistry.features.molarmass;

import de.bioforscher.singa.chemistry.features.databases.chebi.ChEBIDatabase;
import de.bioforscher.singa.chemistry.features.databases.uniprot.UniProtDatabase;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
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
        addRequirement(ChEBIIdentifier.class);
        addFallbackRequirement(100, UniProtIdentifier.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarMass provide(FeatureableType featureable) {
        // mass is parsed from databases
        switch (getPreferredStrategyIndex()) {
            case 0: {
                MolarMass molarMass = ChEBIDatabase.fetchMolarMass(featureable);
                if (molarMass != null) {
                    return molarMass;
                }
            }
            case 100: {
                MolarMass molarMass = UniProtDatabase.fetchMolarMass(featureable);
                if (molarMass != null) {
                    return molarMass;
                }
            }
        }
        return null;
    }
}
