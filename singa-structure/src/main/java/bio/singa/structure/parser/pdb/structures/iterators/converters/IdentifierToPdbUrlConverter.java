package bio.singa.structure.parser.pdb.structures.iterators.converters;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author cl
 */
public class IdentifierToPdbUrlConverter implements ContentConverter<String, URL> {

    private static IdentifierToPdbUrlConverter instance = new IdentifierToPdbUrlConverter();

    public static IdentifierToPdbUrlConverter get() {
        return instance;
    }

    private IdentifierToPdbUrlConverter() {

    }

    @Override
    public URL convert(String content) {
        try {
            return new URL(String.format(PDB_FETCH_URL, content));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The content " + content + " resulted in a malformed URL.");
        }
    }

}
