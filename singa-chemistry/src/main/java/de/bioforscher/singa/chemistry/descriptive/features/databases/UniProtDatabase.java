package de.bioforscher.singa.chemistry.descriptive.features.databases;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.chemistry.parser.uniprot.UniProtParserService;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;
import de.bioforscher.singa.units.quantities.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.Optional;

/**
 * @author cl
 */
public class UniProtDatabase extends DatabaseDescriptor {

    private static final Logger logger = LoggerFactory.getLogger(UniProtDatabase.class);

    /**
     * The instance.
     */
    private static final UniProtDatabase instance = new UniProtDatabase();

    private UniProtDatabase () {
        setSourceName("UniProt Database");
        setSourcePublication("Degtyarenko, Kirill, et al. \"ChEBI: a database and ontology for chemical entities of " +
                "biological interest.\" Nucleic acids research 36.suppl 1 (2008): D344-D350.");
    }

    public static UniProtDatabase getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> Quantity<MolarMass> fetchMolarMass(Featureable featureable) {
        // try to get UniProt identifier
        ChemicalEntity<?> entity = (ChemicalEntity) featureable;
        Optional<Identifier> identifier = UniProtIdentifier.find(entity.getAllIdentifiers());
        // try to get weight from UniProt Database
        return identifier
                .map(identifier1 -> UniProtParserService.parse(identifier1.toString())
                        .getMolarMass())
                .orElse(null);
    }

}
