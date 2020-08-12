package bio.singa.structure.parser.pdb.structures.iterators.converters;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class PathToPDBLinesConverter implements ContentConverter<Path, List<String>> {

    private static PathToPDBLinesConverter instance = new PathToPDBLinesConverter();

    private PathToPDBLinesConverter() {

    }

    public static PathToPDBLinesConverter get() {
        return instance;
    }

    @Override
    public List<String> convert(Path content) {
        if (content.toString().endsWith(".ent.gz")) {
            try {
                return fetchLines(readPacked(content));
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read " + content, e);
            }
        }
        try {
            return fetchLines(Files.newInputStream(content));
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read " + content, e);
        }
    }

}
