package de.bioforscher.chemistry.parser.pdb.ligands;

import de.bioforscher.chemistry.parser.pdb.structures.tokens.LeafSkeleton;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
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
        return parseLeafSubstructure(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream());
    }

    public static LeafSubstructure<?, ?> parseLeafSubstructure(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.parseLeafSubstructure(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

    public static LeafSkeleton parseLeafSkeleton(String ligandId) {
        logger.info("parsing structure {} using the supplied atoms", ligandId);
        try {
            return parseLeafSkeleton(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse cif file for ligand "+ligandId+".",e);
        }
    }

    public static LeafSkeleton parseLeafSkeleton(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.parseLeafSkeleton(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

}
