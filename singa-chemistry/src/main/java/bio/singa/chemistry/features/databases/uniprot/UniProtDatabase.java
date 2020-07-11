package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.model.AbstractChemicalEntity;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Featureable;
import bio.singa.features.quantities.MolarMass;

import javax.measure.Quantity;
import java.util.Optional;

/**
 * @author cl
 */
public class UniProtDatabase {

    public static final Evidence evidence = new Evidence(Evidence.SourceType.DATABASE,
            "UniProt Database",
            "UniProt Consortium. \"UniProt: the universal protein knowledgebase.\" Nucleic acids research 46.5 (2018): 2699.");

    public static MolarMass fetchMolarMass(Featureable featureable) {
        // try to get UniProt identifier
        AbstractChemicalEntity entity = (AbstractChemicalEntity) featureable;
        Optional<UniProtIdentifier> identifierOptional = IdentifierPatternRegistry.find(UniProtIdentifier.class, entity.getAllIdentifiers());
        // has uniprot id
        if (!identifierOptional.isPresent()) {
            return null;
        }
        // try to get weight from UniProt Database
        UniProtIdentifier identifier = identifierOptional.get();
        Quantity<MolarMass> molarMass = UniProtParserService.fetchMolarMass(identifier);

        return MolarMass.of(molarMass)
                .evidence(evidence)
                .build();
    }

}
