package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.annotations.taxonomy.Organism;
import bio.singa.structure.features.variants.SequenceVariant;
import bio.singa.structure.features.variants.SequenceVariants;
import bio.singa.chemistry.model.Protein;
import bio.singa.core.utility.Range;
import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.features.identifiers.GoTerm;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.model.Evidence;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.identifiers.PDBIdentifier;
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
    private static Protein sirt;

    @BeforeAll
    static void initialize() {
        aminotransferase = UniProtParserService.parse("P12345");
        aars = UniProtParserService.parse("P21889");
        transthyretin = UniProtParserService.parse("P02766");
        aarsByName = UniProtParserService.parse("SYD_ECOLI");
        sirt = UniProtParserService.parse("Q96EB6");
    }

    @Test
    @DisplayName("parse uniprot - primary name")
    void parseName() {
        assertEquals("Aspartate aminotransferase, mitochondrial", aminotransferase.getNames().iterator().next());
    }

    @Test
    @DisplayName("parse uniprot - additional names")
    void parseAdditionalNames() {
        assertTrue(aminotransferase.getNames().contains("Glutamate oxaloacetate transaminase 2"));
        assertTrue(aminotransferase.getNames().contains("Plasma membrane-associated fatty acid-binding protein"));
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
        assertEquals("Catalyzes the irreversible transamination of the L-tryptophan metabolite L-kynurenine to " +
                "form kynurenic acid (KA). As a member of the malate-aspartate shuttle, it has a key role in the " +
                "intracellular NAD(H) redox balance. Is important for metabolite exchange between mitochondria and " +
                "cytosol, and for amino acid metabolism. Facilitates cellular uptake of long-chain free fatty acids.",
                functionNote);
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
    void shouldParseByEntryName() {
        UniProtIdentifier uniProtIdentifier = aarsByName.getFeature(UniProtIdentifier.class);
        assertEquals("P21889", uniProtIdentifier.getContent());
    }

    @Test
    void parsePrimaryGeneName() {
        assertEquals("GOT2", aminotransferase.getPrimaryGeneName());
        assertEquals("aspS", aars.getPrimaryGeneName());
        assertEquals("TTR", transthyretin.getPrimaryGeneName());
    }

    @Test
    void parseSubCellularLocation() {
        List<Annotation> annotations = aars.getAnnotationsOfType(AnnotationType.GO_TERM);
        assertEquals("GO:0005829", ((GoTerm) annotations.get(0).getContent()).getContent());
        assertEquals("GO:0004815", ((GoTerm) annotations.get(1).getContent()).getContent());
        assertEquals("GO:0005524", ((GoTerm) annotations.get(2).getContent()).getContent());
        assertEquals("GO:0003676", ((GoTerm) annotations.get(3).getContent()).getContent());
        assertEquals("GO:0006422", ((GoTerm) annotations.get(4).getContent()).getContent());
    }

    @Test
    void parsePdbIdentifier() {
        List<Annotation> annotations = aars.getAnnotationsOfType(AnnotationType.PDB_IDENTIFIER);
        assertEquals("1C0A", ((PDBIdentifier) annotations.get(0).getContent()).getContent());
        assertEquals("1EQR", ((PDBIdentifier) annotations.get(1).getContent()).getContent());
        assertEquals("1IL2", ((PDBIdentifier) annotations.get(2).getContent()).getContent());
    }

    @Test
    void parseUniProtRange() {
        List<Annotation> annotations = sirt.getAnnotationsOfType(AnnotationType.PDB_RANGE);
        // simple format: A/B=241-516
        assertEquals("4i5i-1-A-241", ((Range<?>) annotations.get(16).getContent()).getLowerBound().toString());
        assertEquals("4i5i-1-A-516", ((Range<?>) annotations.get(16).getContent()).getUpperBound().toString());
        // concatenated format A=234-510, B=641-663
        assertEquals("4kxq-1-A-234", ((Range<?>) annotations.get(18).getContent()).getLowerBound().toString());
        assertEquals("4kxq-1-A-510", ((Range<?>) annotations.get(18).getContent()).getUpperBound().toString());
    }
}