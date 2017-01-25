package de.bioforscher.chemistry.parser.pdb.ligands;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by leberech on 19/12/16.
 */
public class LigandParserService {

    private static final Logger logger = LoggerFactory.getLogger(LigandParserService.class);
    private static final String CIF_FETCH_URL = "https://files.rcsb.org/ligands/view/%s.cif";

    public static LeafSubstructure<?, ?> parseLeafSubstructureById(String ligandId) throws IOException {
        logger.info("parsing structure {}", ligandId);
        return parseLeafSubstructureFromCifFile(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream());
    }

    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCifFile(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.parseLeafSubstructureFromCif(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

    public static LeafSubstructure<?,?> parseLeafSubstructureFromCifFile(String ligandId, Map<String, Atom> atoms, LeafIdentifier leafIdentifier) throws IOException {
        logger.info("parsing structure {} using the supplied atoms", ligandId);
        return parseLeafSubstructureFromCifFile(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream(), atoms, leafIdentifier);
    }

    public static LeafSubstructure<?,?> parseLeafSubstructureFromCifFile(InputStream inputStream, Map<String, Atom> atoms, LeafIdentifier leafIdentifier) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.parseLeafSubstructureFromCif(bufferedReader.lines().collect(Collectors.toList()), atoms, leafIdentifier);
            }
        }
    }

    public static String parseLigandTypeById(String ligandId) throws IOException {
        logger.info("getting information for {}", ligandId);
        return parseLigandTypeFromCifFile(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream());
    }

    public static String parseLigandTypeFromCifFile(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.getLeafType(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

}
