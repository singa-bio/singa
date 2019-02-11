package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.annotations.taxonomy.Organism;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.features.variants.SequenceVariant;
import bio.singa.chemistry.features.variants.SequenceVariants;
import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Evidence;
import bio.singa.structure.model.families.AminoAcidFamily;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class UniProtParserTest {

    private static Protein aminotransferase;
    private static Protein aars;
    private static Protein transthyretin;
    private static Protein aarsByName;

    @BeforeAll
    static void initialize() {
        aminotransferase = UniProtParserService.parse("P12345");
        aars = UniProtParserService.parse("P21889");
        transthyretin = UniProtParserService.parse("P02766");
        aarsByName = UniProtParserService.parse("SYD_ECOLI");
    }

    @Test
    @DisplayName("parse uniprot - primary name")
    void parseName() {
        assertEquals("Aspartate aminotransferase, mitochondrial", aminotransferase.getName());
    }

    @Test
    @DisplayName("parse uniprot - additional names")
    void parseAdditionalNames() {
        assertTrue(aminotransferase.getAdditionalNames().contains("Glutamate oxaloacetate transaminase 2"));
        assertTrue(aminotransferase.getAdditionalNames().contains("Plasma membrane-associated fatty acid-binding protein"));
    }

    @Test
    @DisplayName("parse uniprot - sequences")
    void parseSequence() {
        String sequence = aminotransferase.getAllAminoAcidSequences().iterator().next();
        assertEquals("MALLHSARVLSGVASAFHPGLAAAASARASSWWAHVEMGPPDPILGVTEAYKRDTNSKKMNLGVGAYRDDNGKPYVLPSVRKAEAQIAAKGLDKEYL" +
                "PIGGLAEFCRASAELALGENSEVVKSGRFVTVQTISGTGALRIGASFLQRFFKFSRDVFLPKPSWGNHTPIFRDAGMQLQSYRYYDPKTCGFDFTGALEDIS" +
                "KIPEQSVLLLHACAHNPTGVDPRPEQWKEIATVVKKRNLFAFFDMAYQGFASGDGDKDAWAVRHFIEQGINVCLCQSYAKNMGLYGERVGAFTVICKDADEA" +
                "KRVESQLKILIRPMYSNPPIHGARIASTILTSPDLRKQWLQEVKGMADRIIGMRTQLVSNLKKEGSTHSWQHITDQIGMFCFTGLKPEQVERLTKEFSIYMT" +
                "KDGRISVAGVTSGNVGYLAHAIHQVTK", sequence);
    }

    @Test
    @DisplayName("parse uniprot - organism")
    void parseOrganism() {
        // organism
        Organism organism = aminotransferase.getAllOrganisms().iterator().next();
        // organism - name
        assertEquals("Oryctolagus cuniculus", organism.getScientificName());
        // organism - id
        assertEquals("9986", organism.getIdentifier().toString());
    }

    @Test
    @DisplayName("parse uniprot - function annotation")
    void parseFunctionNote() {
        // annotation
        String functionNote = aminotransferase.getContentOfAnnotation(String.class, "function", AnnotationType.NOTE);
        assertEquals("Catalyzes the irreversible transamination of the L-tryptophan metabolite L-kynurenine to" +
                " form kynurenic acid (KA). Plays a key role in amino acid metabolism. Important for metabolite exchange" +
                " between mitochondria and cytosol. Facilitates cellular uptake of long-chain free fatty acids" +
                " (By similarity).", functionNote);
    }

    @Test
    @DisplayName("parse uniprot - ena accession")
    void parseENAAccessionNumber() {
        // annotation
        final List<Identifier> additionalIdentifiers = aars.getAllIdentifiers();
        final Optional<ENAAccessionNumber> firstIdentifier = IdentifierPatternRegistry.find(ENAAccessionNumber.class, additionalIdentifiers);
        assertEquals("CAA37932.1", firstIdentifier.get().toString());
    }

    @Test
    @DisplayName("parse uniprot - variants")
    void shouldParseVariants() {
        SequenceVariants sequenceVariants = transthyretin.getFeature(SequenceVariants.class);
        SequenceVariant sequenceVariant = sequenceVariants.getContent().iterator().next();
        assertEquals("VAR_007546", sequenceVariant.getIdentifier());
        assertEquals("Common polymorphism; dbSNP:rs1800458.", sequenceVariant.getDescription());
        Evidence expected = new Evidence(Evidence.SourceType.LITERATURE);
        expected.setIdentifier("Ota2004");
        expected.setDescription("DOI: 10.1038/ng1285");
        assertEquals(expected, sequenceVariant.getEvidences().iterator().next());
        assertEquals(26, sequenceVariant.getLocation());
        assertEquals(AminoAcidFamily.GLYCINE, sequenceVariant.getOriginal());
        assertEquals(AminoAcidFamily.SERINE, sequenceVariant.getVariation());
    }

    @Test
    @DisplayName("parse uniprot - by entry name")
    void shouldParseByEntryName89() {
        UniProtIdentifier uniProtIdentifier = aarsByName.getFeature(UniProtIdentifier.class);
        assertEquals("P21889", uniProtIdentifier.getContent());
    }

}