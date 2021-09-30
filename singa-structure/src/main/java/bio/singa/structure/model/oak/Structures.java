package bio.singa.structure.model.oak;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.geometry.bodies.Sphere;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.mathematics.vectors.Vectors3D;
import bio.singa.structure.model.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.structure.model.oak.LeafSubstructureFactory.createLeafSubstructure;

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
    public static LabeledSymmetricMatrix<LeafSubstructure> calculateDistanceMatrix(LeafSubstructureContainer leafSubstructureContainer) {
        return VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(leafSubstructureContainer.getAllLeafSubstructures(), LeafSubstructure::getPosition);
    }

    /**
     * Returns the squared distance matrix of the given {@link LeafSubstructureContainer}.
     *
     * @param leafSubstructureContainer A {@link LeafSubstructureContainer}.
     * @return The squared distance matrix.
     */
    public static LabeledSymmetricMatrix<LeafSubstructure> calculateSquaredDistanceMatrix(LeafSubstructureContainer leafSubstructureContainer) {
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
        LabeledSymmetricMatrix<LeafSubstructure> queryDistanceMatrix = calculateSquaredDistanceMatrix(leafSubstructureContainer);
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
                .noneMatch(StructuralEntityFilter.AtomFilter.isAlphaCarbon().negate()
                        .and(StructuralEntityFilter.AtomFilter.isHydrogen().negate()));
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
                .noneMatch(StructuralEntityFilter.AtomFilter.isBackbone().negate()
                        .and(StructuralEntityFilter.AtomFilter.isHydrogen().negate())
                        .and(StructuralEntityFilter.AtomFilter.isBetaCarbon().negate()));
    }

    public static List<Sphere> convertToSpheres(AtomContainer atomContainer) {
        List<Sphere> spheres = new ArrayList<>();
        for (Atom atom : atomContainer.getAllAtoms()) {
            spheres.add(new Sphere(atom.getPosition(), atom.getElement().getVanDerWaalsRadius().getValue().doubleValue()));
        }
        return spheres;
    }

    /**
     * Calculated the torsion angle between the given {@link Atom}s. The order of the given atoms is important.
     *
     * @param a {@link Atom} 1.
     * @param b {@link Atom} 2.
     * @param c {@link Atom} 3.
     * @param d {@link Atom} 4.
     * @return The torsion angle in degrees.
     */
    public static double calculateTorsionAngle(Atom a, Atom b, Atom c, Atom d) {
        return Vectors3D.dihedralAngle(a.getPosition(), b.getPosition(), c.getPosition(), d.getPosition());
    }

    public static OakStructure toStructure(Collection<LeafSubstructure> leafSubstructures, String pdbIdentifier, String title) {
        OakStructure renumberedStructure = new OakStructure();
        renumberedStructure.setPdbIdentifier(pdbIdentifier);
        renumberedStructure.setTitle(title);
        List<LeafSubstructure> sortedOriginal = leafSubstructures.stream()
                .sorted(Comparator.comparing(LeafSubstructure::getIdentifier))
                .collect(Collectors.toList());
        // collect distinct models
        List<Integer> modelIdentifiers = sortedOriginal.stream()
                .map(leafSubstructure -> leafSubstructure.getIdentifier().getModelIdentifier())
                .distinct()
                .collect(Collectors.toList());
        for (Integer modelIdentifier : modelIdentifiers) {
            OakModel newModel = new OakModel(modelIdentifier);
            renumberedStructure.addModel(newModel);
            for (LeafSubstructure leafSubstructure : sortedOriginal) {
                String chainIdentifier = leafSubstructure.getIdentifier().getChainIdentifier();
                // get chain
                Optional<Chain> optionalChain = newModel.getChain(chainIdentifier);
                OakChain newChain;
                if (!optionalChain.isPresent()) {
                    newChain = new OakChain(chainIdentifier);
                    newModel.addChain(newChain);
                } else {
                    newChain = ((OakChain) optionalChain.get());
                }
                // consecutive parts
                OakLeafSubstructure newLeafSubstructure = leafSubstructure.getCopy();
                newLeafSubstructure.setAnnotatedAsHetAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                newChain.addLeafSubstructure(newLeafSubstructure);
            }
        }
        return renumberedStructure;
    }

    /**
     * Returns the closest distance between two {@link AtomContainer}s or NaN if computation fails.
     *
     * @param atomContainer1 First {@link AtomContainer}.
     * @param atomContainer2 Second {@link AtomContainer}.
     * @return The closest distance.
     */
    public static double getClosestDistance(AtomContainer atomContainer1, AtomContainer atomContainer2) {
        List<Vector3D> positions1 = atomContainer1.getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        List<Vector3D> positions2 = atomContainer2.getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        return getClosestDistance(positions1, positions2);
    }

    public static double getClosestDistance(Collection<? extends AtomContainer> containers1, Collection<? extends AtomContainer> containers2) {
        List<Vector3D> positions1 = containers1.stream().flatMap(atomContainer -> atomContainer.getAllAtoms().stream())
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        List<Vector3D> positions2 = containers2.stream().flatMap(atomContainer -> atomContainer.getAllAtoms().stream())
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        return getClosestDistance(positions1, positions2);
    }

    public static double getClosestDistance(List<Vector3D> positions1, List<Vector3D> positions2) {
        Matrix distancesPairwise = VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(positions1, positions2);
        List<Pair<Integer>> minimalDistances = Matrices.getPositionsOfMinimalElement(distancesPairwise);
        if (minimalDistances.isEmpty()) {
            return Double.NaN;
        }
        Pair<Integer> firstPair = minimalDistances.get(0);
        return distancesPairwise.getElement(firstPair.getFirst(), firstPair.getSecond());
    }

    public static void assignBFactors(Structure structure, Map<UniqueAtomIdentifier, Double> factors) {
        OakStructure oakStructure = (OakStructure) structure;
        for (Map.Entry<UniqueAtomIdentifier, Double> entry : factors.entrySet()) {
            UniqueAtomIdentifier identifer = entry.getKey();
            Double value = entry.getValue();
            oakStructure.getAtom(identifer.getAtomSerial()).ifPresent(atom -> atom.setBFactor(value));
        }
    }

    public static Map<Vector3D, UniqueAtomIdentifier> mapAtomsByCoordinate(Structure structure, Set<Vector3D> coordinates, double eps) {
        OakStructure oakStructure = (OakStructure) structure;
        HashMap<Vector3D, UniqueAtomIdentifier> map = new HashMap<>();
        for (Vector3D coordinate : coordinates) {
            Optional<Map.Entry<UniqueAtomIdentifier, Atom>> optionalAtom = oakStructure.getAtomByCoordinate(coordinate, eps);
            if (optionalAtom.isPresent()) {
                Map.Entry<UniqueAtomIdentifier, Atom> entry = optionalAtom.get();
                map.put(coordinate, entry.getKey());
            }
        }
        return map;
    }

}
