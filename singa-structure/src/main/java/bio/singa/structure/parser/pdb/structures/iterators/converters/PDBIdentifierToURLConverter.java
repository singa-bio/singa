package bio.singa.structure.parser.pdb.structures.iterators.converters;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author cl
 */
public class PDBIdentifierToURLConverter implements ContentConverter<String, URL> {

    private static final PDBIdentifierToURLConverter instance = new PDBIdentifierToURLConverter();

    public static PDBIdentifierToURLConverter get() {
        return instance;
    }

    private PDBIdentifierToURLConverter() {

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
