package bio.singa.structure.parser.pdb.structures.iterators.converters;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class URLToPDBLinesConverter implements ContentConverter<URL, List<String>> {

    private static final URLToPDBLinesConverter instance = new URLToPDBLinesConverter();

    private URLToPDBLinesConverter() {

    }

    public static URLToPDBLinesConverter get() {
        return instance;
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
