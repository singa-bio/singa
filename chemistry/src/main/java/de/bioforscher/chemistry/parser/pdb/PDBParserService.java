package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.parser.pdb.tokens.StructureCollector;
import de.bioforscher.chemistry.physical.model.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class PDBParserService {

    private static final Logger logger = LoggerFactory.getLogger(PDBParserService.class);

    public static final String PDB_FETCH_URL = "https://files.rcsb.org/download/%s.pdb";

    public static Structure parseProteinById(String pdbId, String chainId) throws IOException {
        logger.info("parsing chain(s) {} of structure {}", chainId, pdbId);
        return parsePDBFile(new URL(String.format(PDB_FETCH_URL, pdbId)).openStream(), chainId);
    }

    public static Structure parseProteinById(String pdbId) throws IOException {
        logger.info("parsing structure {}", pdbId);
        return parsePDBFile(new URL(String.format(PDB_FETCH_URL, pdbId)).openStream());
    }

    public static Structure parsePDBFile(String filepath) throws IOException {
        return parsePDBFile(new File(filepath));
    }

    public static Structure parsePDBFile(File pdbFile) throws IOException {
        logger.info("parsing structure from file {}", pdbFile.getPath());
        try (InputStream inputStream = Files.newInputStream(pdbFile.toPath())) {
            return parsePDBFile(inputStream);
        }
    }

    public static Structure parsePDBFile(InputStream inputStream) throws IOException {
        return parsePDBFile(inputStream, ".*");
    }

    public static Structure parsePDBFile(InputStream inputStream, String chainId) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return StructureCollector.collectStructure(bufferedReader.lines().collect(Collectors.toList()), chainId);
            }
        }
    }
}
