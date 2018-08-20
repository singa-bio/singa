package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.Featureable;
import bio.singa.structure.features.molarmass.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author cl
 */
public class UniProtDatabase {

    public static final FeatureOrigin origin = new FeatureOrigin(FeatureOrigin.OriginType.DATABASE,
            "UniProt Database",
            "UniProt Consortium. \"UniProt: the universal protein knowledgebase.\" Nucleic acids research 46.5 (2018): 2699.");

    private static final Logger logger = LoggerFactory.getLogger(UniProtDatabase.class);

    /**
     * The instance.
     */
    private static final UniProtDatabase instance = new UniProtDatabase();

    public static UniProtDatabase getInstance() {
        return instance;
    }

    public static MolarMass fetchMolarMass(Featureable featureable) {
        // try to get UniProt identifier
        ChemicalEntity entity = (ChemicalEntity) featureable;
        Optional<UniProtIdentifier> identifierOptional = IdentifierPatternRegistry.find(UniProtIdentifier.class, entity.getAllIdentifiers());
        // try to get weight from UniProt Database
        return identifierOptional.map(identifier -> new MolarMass(UniProtParserService.fetchMolarMass(identifier), origin)).orElse(null);
    }

}
