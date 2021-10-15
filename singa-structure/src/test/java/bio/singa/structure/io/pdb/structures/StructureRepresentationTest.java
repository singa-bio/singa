package bio.singa.structure.io.pdb.structures;


import bio.singa.core.utility.Resources;
import bio.singa.structure.io.pdb.PdbStructureParser;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureRepresentation;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.pdb.tokens.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class StructureRepresentationTest {

    private static int currentLine = 0;
    private static String currentExpected;
    private static String currentActual;

    private static void assertPDBLinesEqual(List<String> expectedLines, List<String> actualLines) {
//        assertEquals(expectedLines.size(), actualLines.size());
        for (int i = 0; i < expectedLines.size(); i++) {
            currentActual = actualLines.get(i);
            currentExpected = expectedLines.get(i);
            currentLine = i;
            assertPDBLineEquals(expectedLines.get(i), actualLines.get(i));
        }
    }

    private static void assertPDBLineEquals(String expected, String actual) {
        if (expected.length() < 6 || actual.length() < 6) {
            assertLineEquals(expected.trim(), actual.trim());
            return;
        }
        String substring = expected.substring(0, 6);
        assertLineEquals(substring, actual.substring(0, 6));
        switch (substring) {
            case "HEADER":
                assertLineEquals(HeaderToken.ID_CODE.extract(expected), (HeaderToken.ID_CODE.extract(actual)));
                break;
            case "TITLE ":
                assertLineEquals(PdbStructureParser.trimEnd(TitleToken.TEXT.extract(expected)), (TitleToken.TEXT.extract(actual)));
                break;
            case "LINK  ":
                assertLineEquals(LinkToken.FIRST_ATOM_NAME.extract(expected), (LinkToken.FIRST_ATOM_NAME.extract(actual)));
                assertLineEquals(LinkToken.FIRST_ATOM_RESIDUE_NAME.extract(expected), (LinkToken.FIRST_ATOM_RESIDUE_NAME.extract(actual)));
                assertLineEquals(LinkToken.FIRST_ATOM_CHAIN_IDENTIFIER.extract(expected), (LinkToken.FIRST_ATOM_CHAIN_IDENTIFIER.extract(actual)));
                assertLineEquals(LinkToken.FIRST_ATOM_RESIDUE_SERIAL.extract(expected), (LinkToken.FIRST_ATOM_RESIDUE_SERIAL.extract(actual)));
                assertLineEquals(LinkToken.FIRST_ATOM_RESIDUE_INSERTION.extract(expected), (LinkToken.FIRST_ATOM_RESIDUE_INSERTION.extract(actual)));
                assertLineEquals(LinkToken.SECOND_ATOM_NAME.extract(expected), (LinkToken.SECOND_ATOM_NAME.extract(actual)));
                assertLineEquals(LinkToken.SECOND_ATOM_RESIDUE_NAME.extract(expected), (LinkToken.SECOND_ATOM_RESIDUE_NAME.extract(actual)));
                assertLineEquals(LinkToken.SECOND_ATOM_CHAIN_IDENTIFIER.extract(expected), (LinkToken.SECOND_ATOM_CHAIN_IDENTIFIER.extract(actual)));
                assertLineEquals(LinkToken.SECOND_ATOM_RESIDUE_SERIAL.extract(expected), (LinkToken.SECOND_ATOM_RESIDUE_SERIAL.extract(actual)));
                assertLineEquals(LinkToken.SECOND_ATOM_RESIDUE_INSERTION.extract(expected), (LinkToken.SECOND_ATOM_RESIDUE_INSERTION.extract(actual)));
                break;
            case "ATOM  ":
            case "HETATM":
                assertLineEquals(AtomToken.ATOM_SERIAL.extract(expected), (AtomToken.ATOM_SERIAL.extract(actual)));
                assertLineEquals(AtomToken.ATOM_NAME.extract(expected), (AtomToken.ATOM_NAME.extract(actual)));
                assertLineEquals(AtomToken.RESIDUE_NAME.extract(expected), (AtomToken.RESIDUE_NAME.extract(actual)));
                assertLineEquals(AtomToken.CHAIN_IDENTIFIER.extract(expected), (AtomToken.CHAIN_IDENTIFIER.extract(actual)));
                assertLineEquals(AtomToken.RESIDUE_INSERTION.extract(expected), (AtomToken.RESIDUE_INSERTION.extract(actual)));
                assertLineEquals(AtomToken.RESIDUE_SERIAL.extract(expected), (AtomToken.RESIDUE_SERIAL.extract(actual)));
                assertLineEquals(AtomToken.X_COORDINATE.extract(expected), (AtomToken.X_COORDINATE.extract(actual)));
                assertLineEquals(AtomToken.Y_COORDINATE.extract(expected), (AtomToken.Y_COORDINATE.extract(actual)));
                assertLineEquals(AtomToken.Z_COORDINATE.extract(expected), (AtomToken.Z_COORDINATE.extract(actual)));
                assertLineEquals(AtomToken.ELEMENT_SYMBOL.extract(expected), (AtomToken.ELEMENT_SYMBOL.extract(actual)));
                assertLineEquals(AtomToken.ELEMENT_CHARGE.extract(expected), (AtomToken.ELEMENT_CHARGE.extract(actual)));
                assertLineEquals(AtomToken.TEMPERATURE_FACTOR.extract(expected), (AtomToken.TEMPERATURE_FACTOR.extract(actual)));
                break;
            case "TER   ":
                assertLineEquals(ChainTerminatorToken.ATOM_SERIAL.extract(expected), (ChainTerminatorToken.ATOM_SERIAL.extract(actual)));
                assertLineEquals(ChainTerminatorToken.RESIDUE_NAME.extract(expected), (ChainTerminatorToken.RESIDUE_NAME.extract(actual)));
                assertLineEquals(ChainTerminatorToken.CHAIN_IDENTIFIER.extract(expected), (ChainTerminatorToken.CHAIN_IDENTIFIER.extract(actual)));
                assertLineEquals(ChainTerminatorToken.RESIDUE_SERIAL.extract(expected), (ChainTerminatorToken.RESIDUE_SERIAL.extract(expected)));
        }
    }

    private static void assertLineEquals(String expected, String actual) {
        assertEquals(expected, actual, "Assertion failed in line " + currentLine + ".\nExpected line : " + currentExpected + "\nActual line   : " + currentActual);
    }

    @Test
    void shouldRepresentSingleChain() throws IOException {
        String fileLocation = Resources.getResourceAsFileLocation("1brr_single_chain.pdb");
        List<String> expectedLines = Files.readAllLines(Paths.get(fileLocation));
        Structure structure = StructureParser.local()
                .fileLocation(fileLocation)
                .parse();
        String pdbRepresentation = StructureRepresentation.composePdbRepresentation(structure);
        List<String> actualLines = Arrays.asList(pdbRepresentation.split(System.lineSeparator()));
        assertPDBLinesEqual(expectedLines, actualLines);
    }

    @Test
    void shouldRepresentMultipleChains() throws IOException {
        String fileLocation = Resources.getResourceAsFileLocation("1brr_multi_chain.pdb");
        List<String> expectedLines = Files.readAllLines(Paths.get(fileLocation));
        Structure structure = StructureParser.local()
//        for (int i = 0; i < expectedLines.size(); i++) {
//            String actual = actualLines.get(i).trim();
//            String expected = expectedLines.get(i);
//            if (!expected.equals(actual)) {
//                System.out.println("actual  : "+ actual);
//                System.out.println("expected: "+ expected);
//            }
//        }

                .fileLocation(fileLocation)
                .parse();
        String pdbRepresentation = StructureRepresentation.composePdbRepresentation(structure);
        List<String> actualLines = Arrays.asList(pdbRepresentation.split(System.lineSeparator()));

        assertPDBLinesEqual(expectedLines, actualLines);
    }

    @Test
    void shouldRepresentMultipleModels() throws IOException {
        String fileLocation = Resources.getResourceAsFileLocation("5ie8_multi_model.pdb");
        List<String> expectedLines = Files.readAllLines(Paths.get(fileLocation));
        Structure structure = StructureParser.local()
                .fileLocation(fileLocation)
                .parse();
        String pdbRepresentation = StructureRepresentation.composePdbRepresentation(structure);
        List<String> actualLines = Arrays.asList(pdbRepresentation.split(System.lineSeparator()));
        assertPDBLinesEqual(expectedLines, actualLines);
    }


    @Test
    void shouldParseLinks() throws IOException {
        String fileLocation = Resources.getResourceAsFileLocation("1c0a_links_test.pdb");
        List<String> expectedLines = Files.readAllLines(Paths.get(fileLocation));
        Structure structure = StructureParser.local()
                .fileLocation(fileLocation)
                .everything()
                .parse();
        String pdbRepresentation = StructureRepresentation.composePdbRepresentation(structure);
        List<String> actualLines = Arrays.asList(pdbRepresentation.split(System.lineSeparator()));
        assertPDBLinesEqual(expectedLines, actualLines);
    }
}