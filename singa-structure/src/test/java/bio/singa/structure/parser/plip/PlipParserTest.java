package bio.singa.structure.parser.plip;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.oak.OakStructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static bio.singa.core.utility.Resources.getResourceAsStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class PlipParserTest {

    @Test
    @DisplayName("plip parsing - correct intra chain interaction parsing")
    void shouldParseIntraChainInteractions() {
        InputStream inputStream = getResourceAsStream("plip/1c0a.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .chainIdentifier("A")
                .parse();
        interactionContainer.validateWithStructure(structure);
        List<Interaction> ligandInteractions = interactionContainer.getLigandInteractions();
        assertEquals(30, ligandInteractions.size());
    }

    @Test
    @DisplayName("plip parsing - correct number of interactions")
    void shouldParseLigandInteractions() {
        InputStream inputStream = getResourceAsStream("plip/1c0a_ligand.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        assertEquals(93, interactionContainer.getInteractions().size());
    }

    @Test
    @DisplayName("plip parsing - error correct symmetric interactions")
    void shouldFixSymmetricInteractions() {
        InputStream inputStream = Resources.getResourceAsStream("plip/4bge.xml");
        InteractionContainer.checkAddedInteractions = false;
        InteractionContainer interactionContainer = PlipParser.parse("4bge", inputStream);
        Optional<Interaction> secondHBond = interactionContainer.getAllInteractions()
                .stream()
                .filter(interaction -> interaction instanceof HydrogenBond)
                .filter(interaction -> interaction.getPlipIdentifier() == 2)
                .findAny();
        assertTrue(secondHBond.isPresent());

        inputStream = Resources.getResourceAsStream("plip/4bge.xml");
        InteractionContainer.checkAddedInteractions = true;
        interactionContainer = PlipParser.parse("4bge", inputStream);
        secondHBond = interactionContainer.getAllInteractions()
                .stream()
                .filter(interaction -> interaction instanceof HydrogenBond)
                .filter(interaction -> interaction.getPlipIdentifier() == 2)
                .findAny();
        assertFalse(secondHBond.isPresent());
    }

    @Test
    @DisplayName("plip parsing - correct handling of insertion codes")
    void shouldParseInteractionsWithInsertionCodes() {
        InputStream inputStream = getResourceAsStream("plip/1k1i.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1k1i", inputStream);
        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1k1i")
                .chainIdentifier("A")
                .parse();
        interactionContainer.validateWithStructure(structure);
    }

    @Test
    @DisplayName("plip parsing - correct fingerprint creation")
    void plipCountFingerPrint() {
        InputStream inputStream = getResourceAsStream("plip/1c0a_ligand.xml");
        InteractionContainer interactionContainer = PlipParser.parse("1c0a", inputStream);
        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .chainIdentifier("A")
                .parse();
        interactionContainer.validateWithStructure(structure);
        PlipCountFingerprint.of(interactionContainer);
    }

    @Test
    @DisplayName("plip parsing - correct binding site specific multi atom interactions")
    void shouldParseBindingSiteAtomSpecificDetails() {
        InputStream inputStream = getResourceAsStream("plip/6nhb.xml");
        InteractionContainer interactionContainer = PlipParser.parse("6nhb", inputStream);
        Optional<PiCation> optionalPiCation = interactionContainer.getInteractions().stream()
                .filter(interaction -> interaction instanceof PiCation)
                .map(interaction -> ((PiCation) interaction))
                .filter(interaction -> interaction.getPlipIdentifier() == 1)
                .findAny();
        if (!optionalPiCation.isPresent()) {
            fail("unable to find interaction");
        }
        PiCation piCation = optionalPiCation.get();
        assertEquals(6, piCation.getAtoms1().size());
        assertEquals(3, piCation.getAtoms2().size());
    }

}