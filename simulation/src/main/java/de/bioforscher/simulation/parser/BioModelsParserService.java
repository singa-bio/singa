package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author cl
 */
public class BioModelsParserService {

    public static final String BIOMODELS_FETCH_URL = "http://www.ebi.ac.uk/biomodels-main/download?mid=%s";

    public static SBMLParser parseModelById(String modelIdentifier) {
        try {
            return parseModelFromStream(new URL(String.format(BIOMODELS_FETCH_URL, modelIdentifier)).openStream());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not find model " + modelIdentifier, e);
        }
    }

    public static SBMLParser parseModelFromFile(String filePath) {
        try {
            return parseModelFromStream(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not find file " + filePath, e);
        }
    }

    public  static SBMLParser parseModelFromStream(InputStream inputStream) throws IOException {
        SBMLParser parser = new SBMLParser(inputStream);
        parser.parse();
        return parser;
    }

}
