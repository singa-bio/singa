package bio.singa.structure.io.general.converters;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author cl
 */
public class PdbIdentifierToUrlConverter implements ContentConverter<String, URL> {

    String PDB_FETCH_URL = "https://files.rcsb.org/download/%s.pdb";

    private static final PdbIdentifierToUrlConverter instance = new PdbIdentifierToUrlConverter();

    public static PdbIdentifierToUrlConverter get() {
        return instance;
    }

    private PdbIdentifierToUrlConverter() {

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
