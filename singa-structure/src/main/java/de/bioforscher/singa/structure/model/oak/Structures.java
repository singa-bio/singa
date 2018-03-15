package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.Fit3DException;
import de.bioforscher.singa.structure.model.interfaces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.bioforscher.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;

/**
 * Methods to use for structures...
 */
public class Structures {

    /**
     * prevent instantiation
     */
    private Structures() {

    }

    /**
     * Returns the distance matrix of the given {@link LeafSubstructureContainer}.
     *
     * @param leafSubstructureContainer A {@link LeafSubstructureContainer}.
     * @return The distance matrix.
     */
    public static LabeledSymmetricMatrix<LeafSubstructure<?>> calculateDistanceMatrix(LeafSubstructureContainer leafSubstructureContainer) {
        return VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(leafSubstructureContainer.getAllLeafSubstructures(), LeafSubstructure::getPosition);
    }

    /**
     * Returns the squared distance matrix of the given {@link LeafSubstructureContainer}.
     *
     * @param leafSubstructureContainer A {@link LeafSubstructureContainer}.
     * @return The squared distance matrix.
     */
    public static LabeledSymmetricMatrix<LeafSubstructure<?>> calculateSquaredDistanceMatrix(LeafSubstructureContainer leafSubstructureContainer) {
        return VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(leafSubstructureContainer.getAllLeafSubstructures(), LeafSubstructure::getPosition);
    }

    /**
     * Returns the maximal extent of the given {@link LeafSubstructureContainer}.
     *
     * @param leafSubstructureContainer A {@link LeafSubstructureContainer}.
     * @return The maximal extent.
     */
    public static double calculateExtent(LeafSubstructureContainer leafSubstructureContainer) {
        return Math.sqrt(calculateSquaredExtent(leafSubstructureContainer));
    }

    /**
     * Returns the maximal squared extent of the given {@link LeafSubstructureContainer}.
     *
     * @param leafSubstructureContainer A {@link LeafSubstructureContainer}.
     * @return The maximal squared extent.
     */
    public static double calculateSquaredExtent(LeafSubstructureContainer leafSubstructureContainer) {
        LabeledSymmetricMatrix<LeafSubstructure<?>> queryDistanceMatrix = calculateSquaredDistanceMatrix(leafSubstructureContainer);
        // position of maximal element is always symmetric, hence we consider the first
        Pair<Integer> positionOfMaximalElement = Matrices.getPositionsOfMaximalElement(queryDistanceMatrix).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("could not determine the maximal squared extent of " +
                        leafSubstructureContainer));
        return queryDistanceMatrix.getElement(positionOfMaximalElement.getFirst(),
                positionOfMaximalElement.getSecond());
    }

    /**
     * Returns the distance matrix of the given {@link Atom}s. <p>
     *
     * @param atomContainer A atom container with the atoms.
     * @return The squared distance matrix.
     */
    public static LabeledSymmetricMatrix<Atom> calculateAtomDistanceMatrix(AtomContainer atomContainer) {
        return VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(atomContainer.getAllAtoms(), Atom::getPosition);
    }

    /**
     * Returns the distance matrix of the given {@link Atom}s. <p>
     *
     * @param atoms A atom container with the atoms.
     * @return The squared distance matrix.
     */
    public static LabeledSymmetricMatrix<Atom> calculateAtomDistanceMatrix(List<Atom> atoms) {
        return VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(atoms, Atom::getPosition);
    }

    /**
     * Returns true iff the given {@link Structure} consists only of alpha carbon atoms (<b>this may include hydrogen
     * atoms</b>).
     *
     * @param structure The {@link Structure} to check.
     * @return True iff structure contains only alpha carbon atoms.
     */
    public static boolean isAlphaCarbonStructure(Structure structure) {
        return structure.getAllAminoAcids().stream()
                .map(LeafSubstructure::getAllAtoms)
                .flatMap(Collection::stream)
                .noneMatch(AtomFilter.isAlphaCarbon().negate()
                        .and(AtomFilter.isHydrogen().negate()));
    }

    /**
     * Returns true iff the given {@link Structure} consists only of backbone atoms (<b>this may include beta carbon and
     * hydrogen atoms</b>).
     *
     * @param structure The {@link Structure} to check.
     * @return True iff structure contains only backbone and hydrogen atoms.
     */
    public static boolean isBackboneStructure(Structure structure) {
        return structure.getAllAminoAcids().stream()
                .map(LeafSubstructure::getAllAtoms)
                .flatMap(Collection::stream)
                .noneMatch(AtomFilter.isBackbone().negate()
                        .and(AtomFilter.isHydrogen().negate())
                        .and(AtomFilter.isBetaCarbon().negate()));
    }

    public static List<Sphere> convertToSpheres(AtomContainer atomContainer) {
        List<Sphere> spheres = new ArrayList<>();
        for (Atom atom : atomContainer.getAllAtoms()) {
            spheres.add(new Sphere(atom.getPosition(), atom.getElement().getVanDerWaalsRadius().getValue().doubleValue()));
        }
        return spheres;
    }
}
