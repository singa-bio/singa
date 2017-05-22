package de.bioforscher.singa.chemistry.descriptive.features.databases;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;
import de.bioforscher.singa.units.features.molarmass.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.ChebiWebServiceFault_Exception;

import javax.measure.Quantity;
import java.util.Optional;

import static de.bioforscher.singa.units.UnitProvider.GRAM_PER_MOLE;

/**
 * @author cl
 */
public class ChEBIDatabase extends DatabaseDescriptor {

    private static final Logger logger = LoggerFactory.getLogger(ChEBIDatabase.class);

    /**
     * The instance.
     */
    private static final ChEBIDatabase instance = new ChEBIDatabase();

    private ChEBIDatabase () {
        setSourceName("ChEBI Database");
        setSourcePublication("Degtyarenko, Kirill, et al. \"ChEBI: a database and ontology for chemical entities of " +
                "biological interest.\" Nucleic acids research 36.suppl 1 (2008): D344-D350.");
    }

    public static ChEBIDatabase getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> Quantity<MolarMass> fetchMolarMass(Featureable featureable) {
        // try to get Chebi identifier
        Species species = (Species) featureable;
        Optional<Identifier> identifier = ChEBIIdentifier.find(species.getAllIdentifiers());
        // try to get weight from ChEBI Database
        if (identifier.isPresent()) {
            ChebiWebServiceClient client = new ChebiWebServiceClient();
            try {
                // fetch and parse weight
                double weight = parseMolarMass(client.getCompleteEntity(identifier.get().toString()).getMass());
                if (weight != Double.NaN) {
                    return Quantities.getQuantity(weight, GRAM_PER_MOLE);
                }
            } catch (ChebiWebServiceFault_Exception e) {
                logger.warn("Can not reach Chemical Entities of Biological Interest (ChEBI) Database. Identifier {} can not be fetched.", identifier);
            }
        }
        return null;
    }

    private static double parseMolarMass(String massAsString) {
        if (massAsString != null) {
            return Double.valueOf(massAsString);
        }
        return Double.NaN;
    }

}
