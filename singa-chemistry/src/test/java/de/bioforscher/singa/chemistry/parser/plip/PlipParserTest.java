package de.bioforscher.singa.chemistry.parser.plip;

import org.junit.Test;

import java.io.InputStream;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

/**
 * @author cl
 */
public class PlipParserTest {

    @Test
    public void shouldParsePlipLigandInteractions() {
        InputStream inputStream = getResourceAsStream("plip/2reg.xml");
        PlipParser.parse("2reg",inputStream);
    }

    @Test
    public void shouldParseInterChainInteractions() {
        InputStream inputStream = getResourceAsStream("plip/1c0a.xml");
        PlipParser.parse("1c0a",inputStream);
    }

}