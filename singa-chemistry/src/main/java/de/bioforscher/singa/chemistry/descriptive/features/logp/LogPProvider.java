package de.bioforscher.singa.chemistry.descriptive.features.logp;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemParserService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.unichem.UniChemParser;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.features.identifiers.PubChemIdentifier;
import de.bioforscher.singa.features.identifiers.model.Identifier;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public class LogPProvider extends FeatureProvider<LogP> {

    private final PubChemDatabase pubChemDatabase = PubChemDatabase.getInstance();

    public LogPProvider() {
        setProvidedFeature(LogP.class);
    }

    @Override
    public LogP provide(Featureable featureable) {
        // try to get Pubchem
        ChemicalEntity species = (ChemicalEntity) featureable;
        Optional<Identifier> pubChemIdentifier = PubChemIdentifier.find(species.getAllIdentifiers());
        if (!pubChemIdentifier.isPresent()) {
            // try to find via inChiKey
            Optional<Identifier> inChiKey = InChIKey.find(species.getAllIdentifiers());
            if (inChiKey.isPresent()) {
                final List<Identifier> identifiers = UniChemParser.parse((InChIKey) inChiKey.get());
                pubChemIdentifier = PubChemIdentifier.find(identifiers);
            }
        }
        // fetch and parse logP
        if (pubChemIdentifier.isPresent()) {
            Species logPSpecies = PubChemParserService.parse(pubChemIdentifier.get().toString());
            return logPSpecies.getFeature(LogP.class);
        }
        return null;
    }


}
