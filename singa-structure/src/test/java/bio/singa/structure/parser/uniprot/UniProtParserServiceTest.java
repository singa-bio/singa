package bio.singa.structure.parser.uniprot;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.quantities.MolarMass;
import bio.singa.structure.model.Protein;
import org.junit.jupiter.api.Test;

import static bio.singa.features.quantities.MolarMass.GRAM_PER_MOLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class UniProtParserServiceTest {

    @Test
    void shouldUseUniProtToFetchMolarMass() {
        Protein testProtein = new Protein.Builder("Q4DA54")
                .additionalIdentifier(new UniProtIdentifier("Q4DA54"))
                .build();
        // get feature
        MolarMass feature = testProtein.getFeature(MolarMass.class);
        // assert attributes and values
        assertEquals("UniProt Database", feature.getPrimaryEvidence().getIdentifier());
        assertEquals(53406.0, feature.getValue().doubleValue());
        assertEquals(GRAM_PER_MOLE, feature.getUnit());
    }

}