package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.simple.AbstractChemicalEntity;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Featureable;
import bio.singa.structure.features.molarmass.MolarMass;

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
        // try to get weight from UniProt Database
        return identifierOptional.map(identifier -> new MolarMass(UniProtParserService.fetchMolarMass(identifier), evidence)).orElse(null);
    }

}
