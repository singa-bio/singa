package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.algorithms.superimposition.scores.PsScore;
import de.bioforscher.singa.chemistry.algorithms.superimposition.scores.XieScore;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
    List<Fit3DMatch> getMatches();

    /**
     * Returns the fraction of residues that were aligned.
     *
     * @return The fractions of matches that were aligned.
     */
    default double getFraction() {
        throw new UnsupportedOperationException("Fraction of aligned residues is only available for the Fit3D site " +
                "alignment algorithm and always 1.0 for Fit3D");
    }

    /**
     * Returns the {@link XieScore} of aligned residues (only available for {@link Fit3DSiteAlignment}.
     *
     * @return The {@link XieScore} of residues that were aligned.
     */
    default XieScore getXieScore() {
        throw new UnsupportedOperationException("Xie score only available for the Fit3D site alignment algorithm.");
    }

    /**
     * Returns the {@link PsScore} of aligned residues (only available for {@link Fit3DSiteAlignment}.
     *
     * @return The {@link PsScore} of residues that were aligned.
     */
    default PsScore getPsScore() {
        throw new UnsupportedOperationException("PS-score only available for the Fit3D site alignment algorithm.");
    }

    /**
     * Writes the matches that were found by this Fit3D search to the specified directory. All matches are aligned to
     * the query motif.
     *
     * @param outputDirectory The directory where the matches should be written.
     */
    default void writeMatches(Path outputDirectory) {
        getMatches().forEach(match -> {
            try {
                StructureWriter.writeLeafSubstructures(match.getSubstructureSuperimposition().getMappedFullCandidate(),
                        outputDirectory.resolve(match.getSubstructureSuperimposition().getStringRepresentation() + ".pdb"));
            } catch (IOException e) {
                logger.error("could not write match {}", match.getSubstructureSuperimposition().getStringRepresentation(), e);
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
