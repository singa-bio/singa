package de.bioforscher.singa.chemistry.features.logp;

import de.bioforscher.singa.chemistry.features.databases.pubchem.PubChemDatabase;
import de.bioforscher.singa.chemistry.features.databases.pubchem.PubChemParserService;
import de.bioforscher.singa.features.identifiers.PubChemIdentifier;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

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
