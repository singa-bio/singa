package de.bioforscher.singa.chemistry.algorithms.superimposition;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.core.parameters.DoubleParameter;
import de.bioforscher.singa.core.parameters.ParameterSampler;
import de.bioforscher.singa.core.parameters.ParameterValue;
import de.bioforscher.singa.core.parameters.UniqueParameterList;
import de.bioforscher.singa.core.utility.Range;
import de.bioforscher.singa.mathematics.algorithms.clustering.AffinityPropagation;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author fk
 */
public class AffinityAlignment {

    private final List<StructuralMotif> input;
    private final RepresentationScheme representationScheme;
    private final boolean idealSuperimposition;
    private final Predicate<Atom> atomFilter;
    private Range<Double> selfSimilarityRange = new Range<>(0.0, 0.8);
    private LabeledSymmetricMatrix<StructuralMotif> distanceMatrix;

    public AffinityAlignment(List<StructuralMotif> input, RepresentationScheme representationScheme, boolean idealSuperimposition, Predicate<Atom> atomFilter) {
        this.input = input;
        this.representationScheme = representationScheme;
        this.idealSuperimposition = idealSuperimposition;
        this.atomFilter = atomFilter;

        // calculate initial alignments
        calculateInitialAlignments();
        computeClustering();
    }

    private void computeClustering() {
        DoubleParameter parameter = new DoubleParameter("self", 0.0, 0.8);
        UniqueParameterList<Double> sample = ParameterSampler.sample(parameter, 11);

        for (ParameterValue<Double> doubleParameterValue : sample.getValues()) {
            AffinityPropagation<StructuralMotif> affinityPropagation = AffinityPropagation.<StructuralMotif>create()
                    .dataPoints(this.input)
                    .matrix(this.distanceMatrix)
                    .isDistance(true)
                    .selfSimilarity(doubleParameterValue.getValue())
                    .run();
            affinityPropagation.getClusters();
            System.out.println(affinityPropagation.getClusters().size());

        }
    }

    private void calculateInitialAlignments() {
        double[][] temporaryDistanceMatrix = new double[input.size()][input.size()];
        for (int i = 0; i < this.input.size() - 1; i++) {
            for (int j = i + 1; j < this.input.size(); j++) {

                StructuralMotif reference = this.input.get(i);
                StructuralMotif candidate = this.input.get(j);

                // calculate superimposition
                SubstructureSuperimposition superimposition;
                if (this.representationScheme == null) {
                    superimposition = this.idealSuperimposition ?
                            SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                    reference,
                                    candidate, this.atomFilter) :
                            SubstructureSuperimposer.calculateSubstructureSuperimposition(
                                    reference.getOrderedLeafSubstructures(),
                                    candidate.getOrderedLeafSubstructures(), this.atomFilter);
                } else {
                    superimposition = this.idealSuperimposition ?
                            SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(
                                    reference,
                                    candidate, this.representationScheme) :
                            SubstructureSuperimposer.calculateSubstructureSuperimposition(
                                    reference.getOrderedLeafSubstructures(),
                                    candidate.getOrderedLeafSubstructures(), this.representationScheme);
                }

                temporaryDistanceMatrix[i][j] = superimposition.getRmsd();
                temporaryDistanceMatrix[j][i] = superimposition.getRmsd();
            }
        }
        distanceMatrix = new LabeledSymmetricMatrix<>(temporaryDistanceMatrix);
        distanceMatrix.setRowLabels(this.input);
    }
}
