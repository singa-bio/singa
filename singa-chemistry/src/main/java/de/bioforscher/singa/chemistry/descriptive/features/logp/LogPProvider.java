package de.bioforscher.singa.chemistry.descriptive.features.logp;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem.PubChemParserService;
import de.bioforscher.singa.core.identifier.PubChemIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;
import de.bioforscher.singa.units.features.model.FeatureProvider;
import de.bioforscher.singa.units.features.model.Featureable;

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
    public <FeatureableType extends Featureable> LogP provide(FeatureableType featureable) {
        // try to get Chebi identifier
        ChemicalEntity<?> species = (ChemicalEntity) featureable;
        Optional<Identifier> identifier = PubChemIdentifier.find(species.getAllIdentifiers());
        // try to get weight from ChEBI Database
        if (identifier.isPresent()) {
            // fetch and parse logP
            Species logPSpecies = PubChemParserService.parse(identifier.get().toString());
            return logPSpecies.getFeature(LogP.class);
        }
        return null;
    }


}
