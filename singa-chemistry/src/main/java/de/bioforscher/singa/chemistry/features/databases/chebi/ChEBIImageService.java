package de.bioforscher.singa.chemistry.features.databases.chebi;

import de.bioforscher.singa.core.parser.AbstractHTMLParser;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ChEBIImageService extends AbstractHTMLParser<InputStream> {

    private static final int defaultImageWidth = 75;
    private final Map<String, String> queryMap;

    public ChEBIImageService(String chEBIIdentifier) {
        this(new ChEBIIdentifier(chEBIIdentifier), defaultImageWidth);
    }

    private ChEBIImageService(ChEBIIdentifier chEBIIdentifier, int imageWidth) {
        setResource("http://www.ebi.ac.uk/chebi/displayImage.do?");
        queryMap = new HashMap<>();
        queryMap.put("defaultImage", "true");
        queryMap.put("imageIndex", "0");
        queryMap.put("chebiId", String.valueOf(chEBIIdentifier.getConsecutiveNumber()));
        queryMap.put("dimensions", String.valueOf(imageWidth));
    }

    public void saveImageToFile(String filePath) {
        File file = new File(filePath);
        try {
            Files.copy(getFetchResult(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("The image could not be written to \"" + filePath + "\"", e);
        }
    }

    @Override
    public InputStream parse() {
        fetchWithQuery(queryMap);
        return getFetchResult();
    }

}
