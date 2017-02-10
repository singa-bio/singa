package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.parser.pdb.structures.PDBWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;

/**
 * Represents an instance of the Fit3D algorithm. This can either be a one target one alignment ({@link Fit3DAlignment})
 * or an alignment batch ({@link Fit3DAlignmentBatch}).
 *
 * @author fk
 */
public interface Fit3D {

    Logger logger = LoggerFactory.getLogger(Fit3D.class);

    /**
     * Returns the matches that were found by this Fit3D search.
     *
     * @return The matches found in the target structure(s).
     */
    TreeMap<Double, SubstructureSuperimposition> getMatches();

    /**
     * Returns the fraction of residues that were aligned.
     *
     * @return The fractions of matches that were aligned.
     */
    double getFraction();

    /**
     * Writes the matches that were found by this Fit3D search to the specified directory. All matches are aligned to
     * the query motif.
     *
     * @param outputDirectory The directory where the matches should be written.
     */
    default void writeMatches(Path outputDirectory) {
        getMatches().values().forEach(substructureSuperimposition -> {
            try {
                PDBWriterService.writeLeafSubstructures(substructureSuperimposition.getMappedFullCandidate(),
                        outputDirectory.resolve(substructureSuperimposition.getStringRepresentation() + ".pdb"));
            } catch (IOException e) {
                logger.error("could not write match {}", substructureSuperimposition.getStringRepresentation(), e);
            }
        });
    }

    /**
     * Returns a string that represents the alignment. This is only available for the {@link Fit3DSiteAlignment}.
     *
     * @return The alignment in string representation.
     */
    default String getAlignmentString() {
        throw new UnsupportedOperationException("unique alignment string can only be obtained with Fit3DSite algorithm");
    }
}
