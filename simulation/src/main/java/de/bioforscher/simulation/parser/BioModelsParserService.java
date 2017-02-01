package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Christoph on 07/11/2016.
 */
public class BioModelsParserService {

    public static final String BIOMODELS_FETCH_URL = "http://www.ebi.ac.uk/biomodels-main/download?mid=%s";

    public static HashMap<String, ChemicalEntity> parseModelById(String modelIdentifier) throws IOException {
        return parseModelFromStream(new URL(String.format(BIOMODELS_FETCH_URL, modelIdentifier)).openStream());
    }

    public static HashMap<String, ChemicalEntity> parseModelFromFile(String filePath) {
        try {
            return parseModelFromStream(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not find file " + filePath, e);
        }
    }

    public  static HashMap<String, ChemicalEntity> parseModelFromStream(InputStream inputStream) throws IOException {
        SBMLSpeciesParserService parser = new SBMLSpeciesParserService(inputStream);
        parser.parse();
        return parser.getChemicalEntities();
    }

}
