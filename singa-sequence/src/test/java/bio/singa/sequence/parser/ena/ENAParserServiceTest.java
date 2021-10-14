package bio.singa.sequence.parser.ena;

import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.sequence.model.NucleotideSequence;
import bio.singa.sequence.model.ProteinSequence;
import bio.singa.sequence.model.SequenceContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ENAParserServiceTest {

    @Test
    void shouldParseNucleotideSequence() {
        // parse sequence
        SequenceContainer sequenceContainer = ENAParserService.parseGeneTranslationPair(new ENAAccessionNumber("CAA37856.1"));
        NucleotideSequence gene = sequenceContainer.getGene();
        ProteinSequence translation = sequenceContainer.getTranslation();

        // compare gene
        assertEquals("ATGCGTACAGAATATTGTGGACAGCTCCGTTTGTCCCACGTGGGGCAGCAGGTGACTCTGTGTGGTTGGGTCAACCGTCGTCGTGAT" +
                "CTTGGTAGCCTGATCTTCATCGATATGCGCGACCGCGAAGGTATCGTGCAGGTATTTTTCGATCCGGATCGTGCGGACGCGTTAAAGCTGGCCTCTGAAC" +
                "TGCGTAATGAGTTCTGCATTCAGGTCACGGGCACCGTACGTGCGCGTGACGAAAAAAATATTAACCGCGATATGGCGACCGGCGAAATCGAAGTGCTGGC" +
                "GTCCTCGCTGACTATCATCAACCGCGCAGATGTTCTGCCGCTTGACTCTAACCACGTCAACACCGAAGAAGCGCGTCTGAAATACCGCTACCTCGACCTG" +
                "CGTCGTCCGGAAATGGCTCAGCGCCTGAAAACCCGCGCTAAAATCACCAGCCTGGTGCGCCGTTTTATGGATGACCACGGCTTCCTCGACATCGAAACTC" +
                "CGATGCTGACCAAAGCCACGCCGGAAGGCGCGCGTGACTACCTGGTGCCTTCTCGTGTGCACAAAGGTAAATTCTACGCACTGCCGCAATCCCCGCAGTT" +
                "GTTCAAACAGCTGCTGATGATGTCCGGTTTTGACCGTTACTATCAGATCGTTAAATGCTTCCGTGACGAAGACCTGCGTGCTGACCGTCAGCCTGAATTT" +
                "ACTCAGATCGATGTGGAAACTTCTTTCATGACCGCGCCGCAAGTGCGTGAAGTGATGGAAGCGCTGGTGCGTCATCTGTGGCTGGAAGTGAAGGGTGTGG" +
                "ATCTGGGCGATTTCCCGGTAATGACCTTTGCGGAAGCAGAACGCCGTTATGGTTCTGATAAACCGGATCTGCGTAACCCGATGGAACTGACTGACGTTGC" +
                "TGATCTGCTGAAATCTGTTGAGTTTGCTGTATTTGCAGGTCCGGCGAACGATCCGAAAGGTCGCGTAGCGGCTCTGCGCGTTCCGGGCGGCGCATCGCTG" +
                "ACCCGTAAGCAGATCGACGAATACGGTAACTTCGTTAAAATCTACGGCGCGAAAGGTCTGGCTTACATCAAAGTTAACGAACGCGCGAAAGGTCTGGAAG" +
                "GTATCAACAGCCCGGTAGCGAAGTTCCTTAATGCAGAAATCATCGAAGACATCCTGGATCGTACTGCCGCGCAAGATGGCGATATGATTTTCTTCGGTGC" +
                "CGACAACAAGAAAATTGTTGCCGACGCGATGGGTGCACTGCGCCTGAAAGTGGGTAAAGACCTTGGTCTGACCGACGAAAGCAAATGGGCACCGCTGTGG" +
                "GTTATCGACTTCCCGATGTTTGAAGACGACGGTGAAGGCGGCCTGACGGCAATGCACCATCCGTTCACCTCACCGAAAGATATGACGGCTGCAGAACTGA" +
                "AAGCTGCACCGGAAAATGCGGTGGCGAACGCTTACGATATGGTCATCAATGGTTACGAAGTGGGCGGTGGTTCAGTACGTATCCATAATGGTGATATGCA" +
                "GCAGACGGTGTTTGGTATTCTGGGTATCAACGAAGAGGAACAGCGCGAGAAATTCGGCTTCCTGCTCGACGCTCTGAAATACGGTACTCCGCCGCACGCA" +
                "GGTCTGGCATTCGGTCTTGACCGTCTGACCATGCTGCTGACCGGCACCGACAATATCCGTGACGTTATCGCCTTCCCGAAAACCACGGCGGCAGCGTGTC" +
                "TGATGACTGAAGCACCGAGCTTTGCTAACCCGACTGCACTGGCTGAGCTGAGCATTCAGGTTGTGAAGAAGGCTGAGAATAACTGA", gene.getSequence());

        // compare translation
        assertEquals("MRTEYCGQLRLSHVGQQVTLCGWVNRRRDLGSLIFIDMRDREGIVQVFFDPDRADALKLASELRNEFCIQVTGTVRARDEKNINRDM" +
                "ATGEIEVLASSLTIINRADVLPLDSNHVNTEEARLKYRYLDLRRPEMAQRLKTRAKITSLVRRFMDDHGFLDIETPMLTKATPEGARDYLVPSRVHKGKF" +
                "YALPQSPQLFKQLLMMSGFDRYYQIVKCFRDEDLRADRQPEFTQIDVETSFMTAPQVREVMEALVRHLWLEVKGVDLGDFPVMTFAEAERRYGSDKPDLR" +
                "NPMELTDVADLLKSVEFAVFAGPANDPKGRVAALRVPGGASLTRKQIDEYGNFVKIYGAKGLAYIKVNERAKGLEGINSPVAKFLNAEIIEDILDRTAAQ" +
                "DGDMIFFGADNKKIVADAMGALRLKVGKDLGLTDESKWAPLWVIDFPMFEDDGEGGLTAMHHPFTSPKDMTAAELKAAPENAVANAYDMVINGYEVGGGS" +
                "VRIHNGDMQQTVFGILGINEEEQREKFGFLLDALKYGTPPHAGLAFGLDRLTMLLTGTDNIRDVIAFPKTTAAACLMTEAPSFANPTALAELSIQVVKKA" +
                "ENN", translation.getSequence());

        // ensure correct UniProt mapping
        assertEquals("P21889", translation.getFeature(UniProtIdentifier.class).getContent());
    }


    @Test
    void shouldParseTranslationSequenceWithoutUniProtMapping() {
        SequenceContainer sequenceContainer = ENAParserService.parseGeneTranslationPair(new ENAAccessionNumber("AAC74452.1"));
        assertFalse(sequenceContainer.getTranslation().hasFeature(UniProtIdentifier.class));
    }
}