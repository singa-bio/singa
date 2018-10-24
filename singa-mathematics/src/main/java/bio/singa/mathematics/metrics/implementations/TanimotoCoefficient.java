package bio.singa.mathematics.metrics.implementations;

import bio.singa.mathematics.exceptions.IncompatibleDimensionsException;
import bio.singa.mathematics.metrics.model.Metric;
import bio.singa.mathematics.vectors.BitVector;

/**
 * Implementation of the Tanimoto coefficient for similarity of {@link BitVector}s. The definition follows the standards
 * defined by ChEMBL.
 *
 * @param <BitVectorType>
 * @author fk
 * @see <a href="https://www.surechembl.org/knowledgebase/84207-tanimoto-coefficient-and-fingerprint-generation>Definition</a>
 */
public class TanimotoCoefficient<BitVectorType extends BitVector> implements Metric<BitVectorType> {

    @Override
    public double calculateDistance(BitVectorType first, BitVectorType second) {
        if (first.hasSameDimensions(second) && first.getElements().length != 0) {
            int nab = 0;
            int na = 0;
            int nb = 0;
            boolean[] firstElements = first.getElements();
            boolean[] secondElements = second.getElements();
            for (int i = 0; i < firstElements.length; i++) {
                if (firstElements[i] && secondElements[i]) {
                    nab++;
                }
                if (firstElements[i]) {
                    na++;
                }
                if (secondElements[i]) {
                    nb++;
                }
            }

            // similarity is 1.0 if all values are false
            if (na == 0 && nb == 0) {
                return 1.0;
            }

            return (double) nab / (na + nb - nab);
        } else {
            throw new IncompatibleDimensionsException(first, second);
        }

        // below the definition by scikit-learn
        // http://scikit-learn.org/stable/modules/generated/sklearn.neighbors.DistanceMetric.html
        /*
            int nonEqualDimensions = 0;
            boolean[] firstElements = first.getElements();
            boolean[] secondElements = second.getElements();
            for (int i = 0; i < firstElements.length; i++) {
                if (!firstElements[i] == secondElements[i]) {
                    nonEqualDimensions++;
                }
            }
            return 2.0 * nonEqualDimensions / (first.getElements().length + nonEqualDimensions);
        */
    }
}
