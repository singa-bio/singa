package bio.singa.sequence.parser.ena;

import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.sequence.model.NucleotideSequence;
import bio.singa.sequence.model.ProteinSequence;
import bio.singa.sequence.model.SequenceContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ENAParserServiceTest {

    @Test
    public void shouldParseNucleotideSequence() {
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
                "TGATGACTGAAGCACCGAGCTTTGCTAACCCGACTGCACTGGCTGAGCTGAGCATTCAGGTTGTGAAGAAGGCTGAGAATAACTGA", gene.getSequenceAsString());

        // compare translation
        assertEquals("MRTEYCGQLRLSHVGQQVTLCGWVNRRRDLGSLIFIDMRDREGIVQVFFDPDRADALKLASELRNEFCIQVTGTVRARDEKNINRDM" +
                "ATGEIEVLASSLTIINRADVLPLDSNHVNTEEARLKYRYLDLRRPEMAQRLKTRAKITSLVRRFMDDHGFLDIETPMLTKATPEGARDYLVPSRVHKGKF" +
                "YALPQSPQLFKQLLMMSGFDRYYQIVKCFRDEDLRADRQPEFTQIDVETSFMTAPQVREVMEALVRHLWLEVKGVDLGDFPVMTFAEAERRYGSDKPDLR" +
                "NPMELTDVADLLKSVEFAVFAGPANDPKGRVAALRVPGGASLTRKQIDEYGNFVKIYGAKGLAYIKVNERAKGLEGINSPVAKFLNAEIIEDILDRTAAQ" +
                "DGDMIFFGADNKKIVADAMGALRLKVGKDLGLTDESKWAPLWVIDFPMFEDDGEGGLTAMHHPFTSPKDMTAAELKAAPENAVANAYDMVINGYEVGGGS" +
                "VRIHNGDMQQTVFGILGINEEEQREKFGFLLDALKYGTPPHAGLAFGLDRLTMLLTGTDNIRDVIAFPKTTAAACLMTEAPSFANPTALAELSIQVVKKA" +
                "ENN", translation.getSequenceAsString());
    }


}