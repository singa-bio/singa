package bio.singa.chemistry.features.logp;

import bio.singa.chemistry.features.databases.pubchem.PubChemDatabase;
import bio.singa.chemistry.features.databases.pubchem.PubChemParserService;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;

/**
 * @author cl
 */
public class LogPProvider extends FeatureProvider<LogP> {

    private final PubChemDatabase pubChemDatabase = PubChemDatabase.getInstance();

    public LogPProvider() {
        setProvidedFeature(LogP.class);
        addRequirement(PubChemIdentifier.class);
    }

    @Override
    public LogP provide(Featureable featureable) {
        PubChemIdentifier pubChemIdentifier = featureable.getFeature(PubChemIdentifier.class);
        return PubChemParserService.parse(pubChemIdentifier).getFeature(LogP.class);
    }

}
