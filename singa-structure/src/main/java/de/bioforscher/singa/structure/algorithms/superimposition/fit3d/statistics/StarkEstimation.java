package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.statistics;

import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.Fit3DMatch;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<AminoAcidFamily, Double> AMINOACID_SCORES;

    static {
        AMINOACID_SCORES = new HashMap<>();
        AMINOACID_SCORES.put(AminoAcidFamily.ALANINE, 8.19);
        AMINOACID_SCORES.put(AminoAcidFamily.ARGININE, 4.62);
        AMINOACID_SCORES.put(AminoAcidFamily.ASPARAGINE, 4.66);
        AMINOACID_SCORES.put(AminoAcidFamily.ASPARTIC_ACID, 5.79);
        AMINOACID_SCORES.put(AminoAcidFamily.CYSTEINE, 1.64);
        AMINOACID_SCORES.put(AminoAcidFamily.GLUTAMINE, 3.71);
        AMINOACID_SCORES.put(AminoAcidFamily.GLUTAMIC_ACID, 5.99);
        AMINOACID_SCORES.put(AminoAcidFamily.GLYCINE, 7.96);
        AMINOACID_SCORES.put(AminoAcidFamily.HISTIDINE, 2.33);
        AMINOACID_SCORES.put(AminoAcidFamily.ISOLEUCINE, 5.42);
        AMINOACID_SCORES.put(AminoAcidFamily.LEUCINE, 8.39);
        AMINOACID_SCORES.put(AminoAcidFamily.LYSINE, 6.04);
        AMINOACID_SCORES.put(AminoAcidFamily.METHIONINE, 2.03);
        AMINOACID_SCORES.put(AminoAcidFamily.PHENYLALANINE, 3.98);
        AMINOACID_SCORES.put(AminoAcidFamily.PROLINE, 4.59);
        AMINOACID_SCORES.put(AminoAcidFamily.SERINE, 6.33);
        AMINOACID_SCORES.put(AminoAcidFamily.THREONINE, 6.15);
        AMINOACID_SCORES.put(AminoAcidFamily.TRYPTOPHAN, 1.54);
        AMINOACID_SCORES.put(AminoAcidFamily.TYROSINE, 3.65);
        AMINOACID_SCORES.put(AminoAcidFamily.VALINE, 7.00);
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
                for (LeafSubstructure<?> leafSubstructure : match.getSubstructureSuperimposition().getCandidate()) {

                    if (!(leafSubstructure instanceof AminoAcid)) {
                        logger.info("ignoring non amino acid when calculating p-value with Stark et al. model");
                        continue;
                    }

                    a = a * (AMINOACID_SCORES.get(((AminoAcid) leafSubstructure).getFamily()));

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

                for (LeafSubstructure<?> leafSubstructure : match.getSubstructureSuperimposition().getCandidate()) {

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
