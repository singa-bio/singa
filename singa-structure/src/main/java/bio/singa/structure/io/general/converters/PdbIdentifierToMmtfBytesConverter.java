package bio.singa.structure.io.general.converters;

import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author cl
 */
public class PdbIdentifierToMmtfBytesConverter implements ContentConverter<String, byte[]> {

    private static PdbIdentifierToMmtfBytesConverter instance = new PdbIdentifierToMmtfBytesConverter();

    public static PdbIdentifierToMmtfBytesConverter get() {
        return instance;
    }

    private PdbIdentifierToMmtfBytesConverter() {

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
