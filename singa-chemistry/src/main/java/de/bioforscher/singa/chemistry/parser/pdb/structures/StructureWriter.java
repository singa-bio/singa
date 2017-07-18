package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.HeaderToken;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * A class to write {@link StructuralEntity} objects to PDB format.
 * <p>
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
     * Writes a given {@link BranchSubstructure} to PDB format by getting the PDB lines of all {@link
     * LeafSubstructure}s.
     *
     * @param branchSubstructure The {@link BranchSubstructure} to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeBranchSubstructure(BranchSubstructure<?, ?> branchSubstructure, Path outputPath) throws IOException {
        logger.info("writing {} to {}", branchSubstructure, outputPath);
        writeLeafSubstructures(branchSubstructure.getLeafSubstructures(), outputPath);
    }

    /**
     * Writes a given list of {@link LeafSubstructure}s to PDB format.
     *
     * @param leafSubstructures The list of {@link LeafSubstructure}s to be written.
     * @param outputPath The output {@link Path}.
     * @throws IOException If the path cannot be written.
     */
    public static void writeLeafSubstructures(List<LeafSubstructure<?, ?>> leafSubstructures, Path outputPath) throws IOException {
        logger.info("writing {} leaf substructures to {}", leafSubstructures.size(), outputPath);
        Files.createDirectories(outputPath.getParent());
        Optional<String> headerContent = createHeaderContent(leafSubstructures);
        String pdbFileContent = leafSubstructures.stream()
                .map(LeafSubstructure::getPdbLines)
                .flatMap(Collection::stream)
                .collect(Collectors.joining("\n"));
        if (headerContent.isPresent()) {
            pdbFileContent = headerContent.get() + "\n" + pdbFileContent;
        }
        Files.write(outputPath, pdbFileContent.getBytes());
    }

    /**
     * Tries to create PDB file header content of the given {@link LeafSubstructure}s.
     *
     * @param leafSubstructures The {@link LeafSubstructure}s for which header content should be generated.
     * @return The header content.
     */
    private static Optional<String> createHeaderContent(List<LeafSubstructure<?, ?>> leafSubstructures) {
        StringJoiner headerContent = new StringJoiner("\n");
        // PDB-ID
        String pdbIdentifier = leafSubstructures.iterator().next().getPdbIdentifier();
        if (!pdbIdentifier.equals(LeafIdentifier.DEFAULT_PDB_IDENTIFIER)) {
            headerContent.add(HeaderToken.ID_CODE.createHeaderLine(pdbIdentifier));
        }
        if (!headerContent.toString().isEmpty()) {
            return Optional.ofNullable(headerContent.toString());
        }
        return Optional.empty();
    }
}
