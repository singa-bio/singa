package bio.singa.structure.io.general.converters;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * @author cl
 */
public interface ContentConverter<ContentType, ConversionType> {

    ConversionType convert(ContentType content);

    /**
     * Extracts the content form a packed file to an input stream.
     *
     * @param path The path to a file to be parsed.
     * @return The files a stream.
     * @throws IOException if the file could not be unpacked or found.
     */
    default InputStream readPacked(Path path) throws IOException {
        return new GZIPInputStream(new FileInputStream(path.toFile()));
    }

    /**
     * Returns a list of lines from the input steam.
     *
     * @param inputStream The input stream.
     * @return The lines of the file.
     * @throws IOException if the file input stream was corrupted.
     */
    default List<String> fetchLines(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return bufferedReader.lines().collect(Collectors.toList());
            }
        }
    }

}
