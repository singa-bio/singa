package de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
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
public class UniProtDatabase {

    private static final Logger logger = LoggerFactory.getLogger(UniProtDatabase.class);

    public static final FeatureOrigin origin = new FeatureOrigin(FeatureOrigin.OriginType.DATABASE,
            "UniProt Database",
            "Degtyarenko, Kirill, et al. \"ChEBI: a database and ontology for chemical entities of " +
                    "biological interest.\" Nucleic acids research 36.suppl 1 (2008): D344-D350.");

    /**
     * The instance.
     */
    private static final UniProtDatabase instance = new UniProtDatabase();

    public static UniProtDatabase getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> MolarMass fetchMolarMass(Featureable featureable) {
        // try to get UniProt identifier
        ChemicalEntity<?> entity = (ChemicalEntity) featureable;
        Optional<Identifier> identifier = UniProtIdentifier.find(entity.getAllIdentifiers());
        // try to get weight from UniProt Database
        if (identifier.isPresent()) {
            return new MolarMass(UniProtParserService.fetchMolarMass(identifier.get()), origin);
        }
        return null;
    }

}
