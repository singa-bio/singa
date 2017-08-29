package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

/**
 * @author cl
 */
public class PlipParserTest {

    private static final Logger logger = LoggerFactory.getLogger(PlipParserTest.class);
    private static int structureCount = 0;

    @Test
    public void shouldParseInteractionsOf1c0a() throws IOException {
        InputStream inputStream = getResourceAsStream("plip/1c0a.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        interactionContainer.getInteractions().forEach(System.out::println);
    }

    @Test
    public void shouldParseLigandInteractionsOf1c0a() throws IOException {
        InputStream inputStream = getResourceAsStream("plip/1c0a_ligand.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        interactionContainer.getInteractions().forEach(System.out::println);
    }

    @Test
    public void shouldParseInteractionsOf3aou() {
        InputStream inputStream = getResourceAsStream("plip/3aou.xml");
        InteractionContainer interactionContainer = PlipParser.parse("3aou", inputStream);
        interactionContainer.getInteractions().forEach(System.out::println);
    }

    @Test
    public void shouldParseInteractionsOf1k1i() {
        InputStream inputStream = getResourceAsStream("plip/1k1i.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1k1i", inputStream);

        Structure structure = StructureParser.online()
                .pdbIdentifier("1k1i")
                .parse();

        interactionContainer.validateWithStructure(structure);
    }




}