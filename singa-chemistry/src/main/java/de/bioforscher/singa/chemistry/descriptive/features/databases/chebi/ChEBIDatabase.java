package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author cl
 */
public class ChEBIDatabase {

    private static final Logger logger = LoggerFactory.getLogger(ChEBIDatabase.class);

    public static final FeatureOrigin origin = new FeatureOrigin(FeatureOrigin.OriginType.DATABASE,
            "ChEBI Database",
            "Degtyarenko, Kirill, et al. \"ChEBI: a database and ontology for chemical entities of " +
                    "biological interest.\" Nucleic acids research 36.suppl 1 (2008): D344-D350.");

    /**
     * The instance.
     */
    private static final ChEBIDatabase instance = new ChEBIDatabase();

    public static ChEBIDatabase getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> MolarMass fetchMolarMass(Featureable featureable) {
        // try to get Chebi identifier
        ChemicalEntity<?> species = (ChemicalEntity) featureable;
        Optional<Identifier> identifier = ChEBIIdentifier.find(species.getAllIdentifiers());
        // try to get weight from ChEBI Database
        if (identifier.isPresent()) {
            return ChEBIParserService.parse(identifier.get().toString()).getFeature(MolarMass.class);
        }
        return null;
    }

    public static <FeaturableType extends Featureable> Smiles fetchSmiles(Featureable featureable) {
        // try to get Chebi identifier
        ChemicalEntity<?> species = (ChemicalEntity) featureable;
        Optional<Identifier> identifier = ChEBIIdentifier.find(species.getAllIdentifiers());
        // try to get weight from ChEBI Database
        if (identifier.isPresent()) {
            return ChEBIParserService.parse(identifier.get().toString()).getFeature(Smiles.class);
        }
        return null;
    }

}
