package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntity;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A class to write {@link StructuralEntity} objects to PDB format.
 *
 * @author fk
 */
public class StructureWriter {

    private static final Logger logger = LoggerFactory.getLogger(StructureWriter.class);

    /**
     * Prevent instantiation.
     */
    private StructureWriter() {

    }

    /**
     * Writes a given list of {@link LeafSubstructure}s in PDB format.
     *
     * @param leafSubstructures The list of {@link LeafSubstructure}s to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeLeafSubstructures(List<LeafSubstructure<?, ?>> leafSubstructures, Path outputPath) throws IOException {
        logger.info("Writing {} leaf substructures to {}.", leafSubstructures.size(), outputPath);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, StructureRepresentation.composePdbRepresentaiton(leafSubstructures).getBytes());
    }

    /**
     * Writes a {@link Structure} in PDB format.
     *
     * @param structure The structure to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeStructure(Structure structure, Path outputPath) throws IOException {
        logger.info("Writing structure {} to {}.",  structure, outputPath);
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, StructureRepresentation.composePdbRepresentaiton(structure).getBytes());
    }

}
