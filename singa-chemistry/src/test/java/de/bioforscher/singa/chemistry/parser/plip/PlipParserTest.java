package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class PlipParserTest {

    private static final Logger logger = LoggerFactory.getLogger(PlipParserTest.class);
    private static int structureCount = 0;

    @Test
    public void shouldParseInterChainInteractions() {
        InputStream inputStream = getResourceAsStream("plip/1c0a.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        Structure structure = StructureParser.online()
                .pdbIdentifier("1c0a")
                .chainIdentifier("A")
                .parse();
        interactionContainer.validateWithStructure(structure);
        List<Interaction> ligandInteractions = interactionContainer.getLigandInteractions();
        assertEquals(30, ligandInteractions.size());
    }

    @Test
    public void shouldParseLigandInteractions() {
        InputStream inputStream = getResourceAsStream("plip/1c0a_ligand.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        assertEquals(93, interactionContainer.getInteractions().size());
    }


    @Test
    public void shouldParseInteractionsWithInsertionCodes() {
        InputStream inputStream = getResourceAsStream("plip/1k1i.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1k1i", inputStream);
        Structure structure = StructureParser.online()
                .pdbIdentifier("1k1i")
                .chainIdentifier("A")
                .parse();
        interactionContainer.validateWithStructure(structure);
    }




}