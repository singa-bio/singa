package de.bioforscher.singa.chemistry.parser.chebi;

import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.parser.rest.AbstractHTMLParser;

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
    private Map<String, String> queryMap;

    public ChEBIImageService(String chEBIIdentifier) {
        this(new ChEBIIdentifier(chEBIIdentifier), defaultImageWidth);
    }

    private ChEBIImageService(ChEBIIdentifier chEBIIdentifier, int imageWidth) {
        setResource("http://www.ebi.ac.uk/chebi/displayImage.do?");
        this.queryMap = new HashMap<>();
        this.queryMap.put("defaultImage", "true");
        this.queryMap.put("imageIndex", "0");
        this.queryMap.put("chebiId", String.valueOf(chEBIIdentifier.getConsecutiveNumber()));
        this.queryMap.put("dimensions", String.valueOf(imageWidth));
    }

    public void saveImageToFile(String filePath) {
        File file = new File(filePath);
        try {
            Files.copy(this.getFetchResult(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("The image could not be written to \""+ filePath +"\"", e);
        }
    }

    @Override
    public InputStream parse() {
        this.fetchWithQuery(this.queryMap);
        return this.getFetchResult();
    }

}
