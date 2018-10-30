package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.annotations.taxonomy.Organism;
import bio.singa.chemistry.entities.Protein;
import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class UniProtParserTest {

    @Test
    void shouldParseSampleAccession() {
        Protein entity = UniProtParserService.parse("P12345");
        // primary name
        assertEquals("Aspartate aminotransferase, mitochondrial", entity.getName());
        // exemplary additional names
        assertTrue(entity.getAdditionalNames().contains("Glutamate oxaloacetate transaminase 2"));
        assertTrue(entity.getAdditionalNames().contains("Plasma membrane-associated fatty acid-binding protein"));
        // sequence
        String sequence = entity.getAllAminoAcidSequences().iterator().next();
        assertEquals("MALLHSARVLSGVASAFHPGLAAAASARASSWWAHVEMGPPDPILGVTEAYKRDTNSKKMNLGVGAYRDDNGKPYVLPSVRKAEAQIAAKGLDKEYL" +
                "PIGGLAEFCRASAELALGENSEVVKSGRFVTVQTISGTGALRIGASFLQRFFKFSRDVFLPKPSWGNHTPIFRDAGMQLQSYRYYDPKTCGFDFTGALEDIS" +
                "KIPEQSVLLLHACAHNPTGVDPRPEQWKEIATVVKKRNLFAFFDMAYQGFASGDGDKDAWAVRHFIEQGINVCLCQSYAKNMGLYGERVGAFTVICKDADEA" +
                "KRVESQLKILIRPMYSNPPIHGARIASTILTSPDLRKQWLQEVKGMADRIIGMRTQLVSNLKKEGSTHSWQHITDQIGMFCFTGLKPEQVERLTKEFSIYMT" +
                "KDGRISVAGVTSGNVGYLAHAIHQVTK", sequence);
        // organism
        Organism organism = entity.getAllOrganisms().iterator().next();

        // organism - name
        assertEquals("Oryctolagus cuniculus", organism.getScientificName());
        // organism - id
        assertEquals("9986", organism.getIdentifier().toString());
        // annotation
        String functionNote = entity.getContentOfAnnotation(String.class, "function", AnnotationType.NOTE);
        assertEquals("Catalyzes the irreversible transamination of the L-tryptophan metabolite L-kynurenine to" +
                " form kynurenic acid (KA). Plays a key role in amino acid metabolism. Important for metabolite exchange" +
                " between mitochondria and cytosol. Facilitates cellular uptake of long-chain free fatty acids" +
                " (By similarity).", functionNote);

        Protein aars = UniProtParserService.parse("P21889");

        final List<Identifier> additionalIdentifiers = aars.getAdditionalIdentifiers();
        final Optional<ENAAccessionNumber> firstIdentifier = IdentifierPatternRegistry.find(ENAAccessionNumber.class, additionalIdentifiers);

        assertEquals("CAA37932.1", firstIdentifier.get().toString());

    }

}