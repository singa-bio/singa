package de.bioforscher.chemistry.parser;

import de.bioforscher.chemistry.parser.pdb.AtomToken;
import org.junit.Test;

import java.util.stream.Stream;

/**
 * Created by Christoph on 23.06.2016.
 */
public class StructureParserTest {

    @Test
    public void shouldParseAtomsCorrectly() {
        String line = "ATOM      1  N   SER A 778      -6.558 -12.277   2.471  1.00  2.40           N  ";
        Stream.of(AtomToken.values()).forEach(atomToken -> System.out.println(atomToken.extract(line)));
    }

}
