package bio.singa.structure.io.general.converters;

import java.io.File;
import java.nio.file.Path;

/**
 * @author cl
 */
public class FileToPathConverter implements ContentConverter<File, Path> {

    private static FileToPathConverter instance = new FileToPathConverter();

    private FileToPathConverter() {

    }

    public static FileToPathConverter get() {
        return instance;
    }

    @Override
    public Path convert(File content) {
        return content.toPath();
    }
}
