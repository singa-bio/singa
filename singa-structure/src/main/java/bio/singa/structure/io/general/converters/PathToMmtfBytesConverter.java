package bio.singa.structure.io.general.converters;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author cl
 */
public class PathToMmtfBytesConverter implements ContentConverter<Path, byte[]> {

    private static PathToMmtfBytesConverter instance = new PathToMmtfBytesConverter();

    private PathToMmtfBytesConverter() {

    }

    public static PathToMmtfBytesConverter get() {
        return instance;
    }

    @Override
    public byte[] convert(Path content) {
        try {
            return Files.readAllBytes(content);
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read " + content, e);
        }
    }

}
