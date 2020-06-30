package bio.singa.structure.parser.pdb.structures.iterators.converters;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cl
 */
public class FileLocationToPathConverter implements ContentConverter<String, Path> {

    private static final FileLocationToPathConverter instance = new FileLocationToPathConverter();

    public static FileLocationToPathConverter get() {
        return instance;
    }

    private FileLocationToPathConverter() {

    }

    @Override
    public Path convert(String content) {
        return Paths.get(content);
    }

}
