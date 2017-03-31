package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class to write {@link de.bioforscher.chemistry.physical.model.StructuralEntity} objects to PDB format.
 *
 * TODO write test
 *
 * @author fk
 */
public class StructureWriter {

    private static final Logger logger = LoggerFactory.getLogger(StructureWriter.class);

    /**
     * prevent instantiation
     */
    private StructureWriter() {

    }

    /**
     * Writes a given {@link BranchSubstructure} to PDB format by getting the PDB lines of all {@link LeafSubstructure}s.
     *
     * @param branchSubstructure The {@link BranchSubstructure} to be written.
     * @param outputPath         The output {@link Path}.
     * @throws IOException
     */
    public static void writeBranchSubstructures(BranchSubstructure<?> branchSubstructure, Path outputPath) throws IOException {
        logger.info("writing {} to {}", branchSubstructure, outputPath);
        writeLeafSubstructures(branchSubstructure.getLeafSubstructures(), outputPath);
    }

    /**
     * Writes a given list of {@link LeafSubstructure}s to PDB format.
     *
     * @param leafSubstructures The list of {@link LeafSubstructure}s to be written.
     * @param outputPath        The output {@link Path}.
     * @throws IOException
     */
    public static void writeLeafSubstructures(List<LeafSubstructure<?, ?>> leafSubstructures, Path outputPath) throws IOException {
        logger.info("writing {} LeafSubstructures to {}", leafSubstructures.size(), outputPath);
        Files.createDirectories(outputPath.getParent());
        String pdbFileContent = leafSubstructures.stream()
                .map(LeafSubstructure::getPdbLines)
                .flatMap(Collection::stream)
                .collect(Collectors.joining("\n"));
        Files.write(outputPath, pdbFileContent.getBytes());
    }
}
