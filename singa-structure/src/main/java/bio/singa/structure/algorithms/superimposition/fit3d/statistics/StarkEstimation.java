package bio.singa.structure.algorithms.superimposition.fit3d.statistics;

import bio.singa.structure.algorithms.superimposition.fit3d.Fit3DMatch;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.*;

/**
 * @author fk
 */
public class StarkEstimation implements StatisticalModel {

    private static final Logger logger = LoggerFactory.getLogger(StarkEstimation.class);

    private static final double A0 = 2.678E9;
    private static final double A2 = 0.1277E-6;

    private static final double A3 = 1.79E-3;

    private static final double C2 = 0.196;
    private static final double C3 = 0.094;

    private static final Map<StructuralFamily, Double> AMINO_ACID_SCORES;

    static {
        AMINO_ACID_SCORES = new HashMap<>();
        AMINO_ACID_SCORES.put(ALANINE, 8.19);
        AMINO_ACID_SCORES.put(ARGININE, 4.62);
        AMINO_ACID_SCORES.put(ASPARAGINE, 4.66);
        AMINO_ACID_SCORES.put(ASPARTIC_ACID, 5.79);
        AMINO_ACID_SCORES.put(CYSTEINE, 1.64);
        AMINO_ACID_SCORES.put(GLUTAMINE, 3.71);
        AMINO_ACID_SCORES.put(GLUTAMIC_ACID, 5.99);
        AMINO_ACID_SCORES.put(GLYCINE, 7.96);
        AMINO_ACID_SCORES.put(HISTIDINE, 2.33);
        AMINO_ACID_SCORES.put(ISOLEUCINE, 5.42);
        AMINO_ACID_SCORES.put(LEUCINE, 8.39);
        AMINO_ACID_SCORES.put(LYSINE, 6.04);
        AMINO_ACID_SCORES.put(METHIONINE, 2.03);
        AMINO_ACID_SCORES.put(PHENYLALANINE, 3.98);
        AMINO_ACID_SCORES.put(PROLINE, 4.59);
        AMINO_ACID_SCORES.put(SERINE, 6.33);
        AMINO_ACID_SCORES.put(THREONINE, 6.15);
        AMINO_ACID_SCORES.put(TRYPTOPHAN, 1.54);
        AMINO_ACID_SCORES.put(TYROSINE, 3.65);
        AMINO_ACID_SCORES.put(VALINE, 7.00);
    }


    @Override
    public void calculatePvalues(List<Fit3DMatch> matches) {

        for (Fit3DMatch match : matches) {

            // number of residues of candidate motif
            int n = match.getSubstructureSuperimposition().getCandidate().size();
            double a;
            double b;
            // correction factor 1
            double c = 1.0;
            // correction factor 2
            double d = 0.0;

            // for more than two residues
            if (n > 2) {

                a = A0 * Math.pow(A3, n);

                b = 2.93 * n - 5.88;

                // multiply abundances and calculate correction factors
                for (LeafSubstructure leafSubstructure : match.getSubstructureSuperimposition().getCandidate()) {

                    if (!(leafSubstructure instanceof AminoAcid)) {
                        logger.info("ignoring non amino acid when calculating p-value with Stark et al. model");
                        continue;
                    }

                    a = a * AMINO_ACID_SCORES.get(leafSubstructure.getFamily());

                    // count number of atoms used for alignment
                    int numberOfAtoms = match.getSubstructureSuperimposition().getMappedCandidate().stream()
                            .map(LeafSubstructure::getAllAtoms)
                            .mapToInt(Collection::size)
                            .sum();

                    if (numberOfAtoms == 2) {
                        c = c * C2;
                        d = d + 2;
                    } else if (numberOfAtoms > 2) {
                        c = c * C3;
                        d = d + 3;
                    }
                }
            } else {

                a = A0 * A2;
                b = 0.97;

                for (LeafSubstructure leafSubstructure : match.getSubstructureSuperimposition().getCandidate()) {

                    if (!(leafSubstructure instanceof AminoAcid)) {
                        logger.info("ignoring non amino acid when calculating p-value with Stark et al. model");
                        continue;
                    }

                    // count number of atoms used for alignment
                    int numberOfAtoms = match.getSubstructureSuperimposition().getMappedCandidate().stream()
                            .map(LeafSubstructure::getAllAtoms)
                            .mapToInt(Collection::size)
                            .sum();

                    if (numberOfAtoms == 2) {
                        c = c * C2;
                        d = d + 2;
                    } else if (numberOfAtoms > 2) {
                        c = c * C3;
                        d = d + 3;
                    }
                }
            }

            // calculate p-value with or without dependence atom correction
            if ((a * c * Math.pow(match.getRmsd(), (b + d))) < (a * Math.pow(match.getRmsd(), b))) {
                match.setPvalue(1 - Math.exp(-(a * c * Math.pow(match.getRmsd(), (b + d)))));
            } else {
                match.setPvalue(1 - Math.exp(-((a * Math.pow(match.getRmsd(), b)))));
            }
        }
    }
}
