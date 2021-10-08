package bio.singa.structure.io.cif;

import bio.singa.structure.io.general.converters.ContentConverter;

import java.net.MalformedURLException;
import java.net.URL;

public class MmcifIdentifierToUrlConverter implements ContentConverter<String, URL> {

    String BCIF_FETCH_URL = "https://models.rcsb.org/%s.bcif";

    private static final MmcifIdentifierToUrlConverter instance = new MmcifIdentifierToUrlConverter();

    public static MmcifIdentifierToUrlConverter get() {
        return instance;
    }

    private MmcifIdentifierToUrlConverter() {

    }

    @Override
    public URL convert(String content) {
        try {
            return new URL(String.format(BCIF_FETCH_URL, content));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The content " + content + " resulted in a malformed URL.");
        }
    }
}
