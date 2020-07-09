package bio.singa.structure.parser.pdb.structures.iterators.converters;

import bio.singa.structure.parser.pdb.structures.StructureParserException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author cl
 */
public class PathToObjectConverter implements ContentConverter<Path, Object> {

    private static PathToObjectConverter instance = new PathToObjectConverter();

    private PathToObjectConverter() {

    }

    public static PathToObjectConverter get() {
        return instance;
    }

    @Override
    public Object convert(Path content) {
        String fileName = content.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".mmtf.gz")) {
            try {
                return Files.readAllBytes(content);
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read mmtf " + content, e);
            }
        }

        if (fileName.endsWith(".ent.gz")) {
            try {
                return fetchLines(readPacked(content));
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read packed pdb" + content, e);
            }
        }
        if (fileName.endsWith(".pdb")) {
            try {
                return fetchLines(Files.newInputStream(content));
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read pdb" + content, e);
            }
        }
        throw new StructureParserException("Unable to determine file type of "+content);
    }

}
