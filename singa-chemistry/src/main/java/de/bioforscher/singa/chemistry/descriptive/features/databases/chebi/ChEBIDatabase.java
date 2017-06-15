package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.Featureable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.ChebiWebServiceFault_Exception;

import javax.measure.Quantity;
import java.util.Optional;

import static de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass.GRAM_PER_MOLE;

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
            ChebiWebServiceClient client = new ChebiWebServiceClient();
            try {
                // fetch and parse weight
                final double weight = parseMolarMass(client.getCompleteEntity(identifier.get().toString()).getMass());
                if (weight != Double.NaN) {
                    final Quantity<MolarMass> quantity = Quantities.getQuantity(weight, GRAM_PER_MOLE);
                    return new MolarMass(quantity, origin);
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

    public static <FeaturableType extends Featureable> Smiles fetchSmiles(Featureable featureable) {
        // try to get Chebi identifier
        ChemicalEntity<?> species = (ChemicalEntity) featureable;
        Optional<Identifier> identifier = ChEBIIdentifier.find(species.getAllIdentifiers());
        // try to get weight from ChEBI Database
        if (identifier.isPresent()) {
            ChebiWebServiceClient client = new ChebiWebServiceClient();
            try {
                // fetch and parse smiles string
                final String smilesString = client.getCompleteEntity(identifier.get().toString()).getSmiles();
                return new Smiles(smilesString, origin);
            } catch (ChebiWebServiceFault_Exception e) {
                logger.warn("Can not reach Chemical Entities of Biological Interest (ChEBI) Database. Identifier {} can not be fetched.", identifier);
            }
        }
        return null;
    }

}
