package bio.singa.structure.features;

import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;
import bio.singa.features.quantities.MolarMass;
import bio.singa.structure.parser.chebi.ChEBIDatabase;
import bio.singa.structure.parser.uniprot.UniProtDatabase;

/**
 * @author cl
 */
public class MolarMassProvider extends FeatureProvider<MolarMass> {

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
