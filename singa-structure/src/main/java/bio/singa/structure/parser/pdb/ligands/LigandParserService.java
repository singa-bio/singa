package bio.singa.structure.parser.pdb.ligands;

import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.parser.pdb.structures.LocalCIFRepository;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class LigandParserService {

    private static final Logger logger = LoggerFactory.getLogger(LigandParserService.class);
    private static final String CIF_FETCH_URL = "https://files.rcsb.org/ligands/view/%s.cif";

    public static LeafSubstructure<?> parseLeafSubstructureById(String ligandId) throws IOException {
        logger.debug("parsing structure {}", ligandId);
        return parseLeafSubstructure(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream());
    }

    public static LeafSubstructure<?> parseLeafSubstructure(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.parseLeafSubstructure(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

    public static LeafSkeleton parseLeafSkeleton(String ligandId) {
        logger.debug("parsing structure {} using the supplied atoms", ligandId);
        try {
            return parseLeafSkeleton(new URL(String.format(CIF_FETCH_URL, ligandId)).openStream());
        } catch (IOException e) {
            logger.warn("Could not parse cif file for ligand {}. If this exception" +
                    " occurred during structure parsing, you may ignore it with setting DISREGARD_CONNECTIONS", ligandId);
        }
        return new LeafSkeleton(ligandId);
    }

    public static LeafSkeleton parseLeafSkeleton(String ligandId, LocalCIFRepository localCifRepository) {
        logger.debug("parsing structure {} using the supplied atoms", ligandId);
        try {
            return parseLeafSkeleton(Files.newInputStream(localCifRepository.getPathForLigandIdentifier(ligandId)));
        } catch (IOException e) {
            logger.warn("Could not parse cif file for ligand {}. If this exception" +
                    " occurred during structure parsing, you may ignore it with setting DISREGARD_CONNECTIONS", ligandId, e);
        }
        return new LeafSkeleton(ligandId);
    }

    public static LeafSkeleton parseLeafSkeleton(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return CifFileParser.parseLeafSkeleton(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

}
