package bio.singa.structure.parser.pdb.structures.iterators.converters;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class UrlToPdbLinesConverter implements ContentConverter<URL, List<String>> {

    private static UrlToPdbLinesConverter instance = new UrlToPdbLinesConverter();

    public static UrlToPdbLinesConverter get() {
        return instance;
    }

    private UrlToPdbLinesConverter() {

    }

    @Override
    public List<String> convert(URL access) {
        InputStream inputStream;
        try {
            inputStream = access.openStream();
        } catch (IOException e) {
            throw new UncheckedIOException("unable to access " + access, e);
        }
        try {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    return bufferedReader.lines().collect(Collectors.toList());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read " + access, e);
        }
    }

}
