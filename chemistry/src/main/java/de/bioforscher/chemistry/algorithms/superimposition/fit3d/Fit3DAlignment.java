package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.chemistry.physical.model.StructureUtilities;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.matrices.MatrixUtilities;
import de.bioforscher.mathematics.vectors.RegularVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An implementation of the Fit3D algorithm for substructure search.
 *
 * @author fk
 */
public class Fit3DAlignment {

    public static final double DEFAULT_DISTANCE_TOLERANCE = 1.0;

    private static final Logger logger = LoggerFactory.getLogger(Fit3DAlignment.class);

    private final List<LeafSubstructure<?, ?>> queryMotif;
    private final BranchSubstructure<?> target;
    private double queryExtent;
    private LabeledSymmetricMatrix<LeafSubstructure<?, ?>> distanceMatrix;

    public Fit3DAlignment(List<LeafSubstructure<?, ?>> queryMotif, BranchSubstructure<?> target) {
        this.queryMotif = queryMotif;
        this.target = target;

        if (queryMotif.size() > target.getAtomContainingSubstructures().size()) {
            throw new Fit3DException("search target must contain at least as many atom-containing substructures " +
                    "as the query");
        }

        // reduce target structures to the types that are actually occurring in the query motif or defined exchanges
        reduceTargetStructure();

        // calculate motif extent
        calculateMotifExtent();

        // TODO switch to all squared distances
        // calculate distance matrix
        this.distanceMatrix = this.target.getDistanceMatrix();
        logger.debug("the target structure distance matrix is\n{}", this.distanceMatrix.getStringRepresentation());

        composeEnvironments();
    }

    /**
     * Determines the maximal spatial extent of the query motif, measured on the centroid of all atoms.
     */
    private void calculateMotifExtent() {
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> queryDistanceMatrix =
                StructureUtilities.calculateDistanceMatrix(this.queryMotif);
        // position of maximal element is always symmetric, hence we consider the first
        Pair<Integer> positionOfMaximalElement = MatrixUtilities.getPositionsOfMaximalElement(queryDistanceMatrix)
                .stream()
                .findFirst()
                .orElseThrow(() -> new Fit3DException("could not determine extent of the query motif"));
        this.queryExtent = queryDistanceMatrix.getElement(positionOfMaximalElement.getFirst(),
                positionOfMaximalElement.getSecond());

        logger.debug("the query motif extent is {}", this.queryExtent);
    }


    /**
     * Reduces the target structure only to the {@link StructuralFamily} types that are contained in the query motif or
     * its defined exchanges.
     */
    private void reduceTargetStructure() {
        Set<StructuralFamily> containingTypes = getContainingTypes();
        List<Integer> toBeRemoved = this.target.getAtomContainingSubstructures().stream()
                .filter(leafSubstructure -> !containingTypes.contains(leafSubstructure.getFamily()))
                .map(LeafSubstructure::getIdentifier)
                .collect(Collectors.toList());
        toBeRemoved.forEach(this.target::removeSubstructure);
    }

    /**
     * Returns all containing types of the query motif.
     *
     * @return a set that contains all {@link StructuralFamily} elements of the query motif
     */
    private Set<StructuralFamily> getContainingTypes() {
        // add types
        Set<StructuralFamily> types = this.queryMotif.stream()
                .map(LeafSubstructure::getFamily)
                .collect(Collectors.toSet());
        // add exchangeable types
        for (LeafSubstructure<?, ?> substructure : this.queryMotif) {
            types.addAll(substructure.getExchangeableTypes());
        }
        return types;
    }

    private void composeEnvironments() {
        List<List<LeafSubstructure<?, ?>>> environments = new ArrayList<>();
        // iterate over reduced target structure
        Iterator<LeafSubstructure<?, ?>> structureIterator = this.target.getAtomContainingSubstructures().iterator();
        while (structureIterator.hasNext()) {
            // collect environments within the bounds if the motif extent
            LeafSubstructure<?, ?> currentSubstructure = structureIterator.next();
            RegularVector distanceToOthers = this.distanceMatrix.getColumnByLabel(currentSubstructure);
            List<LeafSubstructure<?, ?>> environment = new ArrayList<>();
            for (int i = 0; i < distanceToOthers.getElements().length; i++) {
                double currentDistance = distanceToOthers.getElement(i);
                if (currentDistance <= this.queryExtent + DEFAULT_DISTANCE_TOLERANCE) {
                    environment.add(this.distanceMatrix.getColumnLabel(i));
                }
            }
            System.out.println(currentSubstructure);
            System.out.println(environment);
            System.out.println();
            // TODO continue here
//            if (canContainSolution(environment)) {
//                environments.add(environment);
//            }
        }
    }
}