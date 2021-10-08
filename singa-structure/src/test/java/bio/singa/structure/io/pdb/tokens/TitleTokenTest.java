package bio.singa.structure.io.pdb.tokens;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class TitleTokenTest {

    @Test
    void shouldComposeTitleLine() {
        String title = "This is a short one line title.";
        List<String> titleLines = TitleToken.assemblePDBLines(title);
        assertEquals(1, titleLines.size());
        assertEquals("TITLE     THIS IS A SHORT ONE LINE TITLE.", titleLines.iterator().next());
    }

    @Test
    void shouldComposeLongTitleLines() {
        String title = "This title is quiet a bit longer and therefore should require at least one more line - if not " +
                "two. Also everything should be written in capital letters, so please do that.";
        List<String> titleLines = TitleToken.assemblePDBLines(title);
        String actual = String.join("\n", titleLines);
        String expected = "TITLE     THIS TITLE IS QUIET A BIT LONGER AND THEREFORE SHOULD REQUIRE AT\n" +
                "TITLE    2 LEAST ONE MORE LINE - IF NOT TWO. ALSO EVERYTHING SHOULD BE WRITTEN\n" +
                "TITLE    3 IN CAPITAL LETTERS, SO PLEASE DO THAT.";
        assertEquals(expected, actual);
    }

}