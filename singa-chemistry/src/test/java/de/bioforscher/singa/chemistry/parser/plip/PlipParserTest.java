package de.bioforscher.singa.chemistry.parser.plip;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsFilepath;
import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

/**
 * @author cl
 */
public class PlipParserTest {

    private static final Logger logger = LoggerFactory.getLogger(PlipParserTest.class);
    private static int structureCount = 0;

    @Test
    public void shouldParseInterChainInteractions() throws IOException {
        // InputStream inputStream = getResourceAsStream("plip/1c0a.xml");
        // PlipParser.parse("1c0a",inputStream);
        String nrPDB_chains_blast_10e80 = getResourceAsFilepath("nrPDB_chains_BLAST_10e80");

        Files.readAllLines(Paths.get(nrPDB_chains_blast_10e80)).forEach(line -> {
            structureCount++;
            logger.info("Processing structure {}/39592.", structureCount);
            String[] split = line.split("\t");
            try {
                PlipParser.parse(split[0], split[1]);
            } catch (UncheckedIOException e) {
                logger.warn("Unable to parse {}. ", split[0], e);
            }
        });


    }

    @Test
    public void shouldParseInterChainInteractionsWithIllReferences() {
        InputStream inputStream = getResourceAsStream("plip/3aou.xml");
        PlipParser.parse("3aou", inputStream);
    }


}