package bio.singa.structure.parser.pdb.structures.iterators.converters;

import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author cl
 */
public class IdentifierToMmtfBytesConverter implements ContentConverter<String, byte[]> {

    private static IdentifierToMmtfBytesConverter instance = new IdentifierToMmtfBytesConverter();

    public static IdentifierToMmtfBytesConverter get() {
        return instance;
    }

    private IdentifierToMmtfBytesConverter() {

    }

    @Override
    public byte[] convert(String content) {
        try {
            return ReaderUtils.getByteArrayFromUrl(content, true, false);
        } catch (IOException e) {
            throw new UncheckedIOException("unable to access " + content, e);
        }
    }
}
