package bio.singa.structure.model.oak;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.geometry.bodies.Sphere;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.*;

import java.util.*;

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
        return Vectors.dihedralAngle(a.getPosition(), b.getPosition(), c.getPosition(), d.getPosition());
    }

    /**
     * Renumbers the {@link LeafSubstructure}s in a given {@link OakStructure} according to a renumbering map.
     * <b>Warning:</b>The method copies only the parts of the structure which are covered by the renumbering map.
     * Non-consecutive parts of the structure (ligands, etc.) are not affected and copied to the new structure.
     *
     * @param structure The {@link OakStructure} to be renumbered.
     * @param renumberingMap The renumbering map, containing as key original {@link LeafIdentifier}s the renumbered
     * serial as values.
     * @return A copy of the structure, renumbered according to the given map.
     */
    public static OakStructure renumberStructure(OakStructure structure, Map<LeafIdentifier, Integer> renumberingMap) {
        OakStructure renumberedStructure = new OakStructure();
        renumberedStructure.setPdbIdentifier(structure.getPdbIdentifier());
        for (Model model : structure.getAllModels()) {
            OakModel renumberedModel = new OakModel(model.getModelIdentifier());
            renumberedStructure.addModel(renumberedModel);
            // consecutive parts
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = new OakChain(chain.getChainIdentifier());
                renumberedModel.addChain(renumberedChain);
                for (LeafSubstructure leafSubstructure : oakChain.getConsecutivePart()) {
                    LeafIdentifier originalIdentifier = leafSubstructure.getIdentifier();
                    if (!renumberingMap.containsKey(originalIdentifier)) {
                        continue;
                    }
                    LeafIdentifier renumberedIdentifier = new LeafIdentifier(
                            originalIdentifier.getPdbIdentifier(),
                            originalIdentifier.getModelIdentifier(),
                            originalIdentifier.getChainIdentifier(),
                            renumberingMap.get(originalIdentifier),
                            originalIdentifier.getInsertionCode());
                    OakLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(renumberedIdentifier, leafSubstructure.getFamily());
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure, true);
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        OakAtom renumberedAtom = new OakAtom(
                                atom.getAtomIdentifier(),
                                atom.getElement(),
                                atom.getAtomName(),
                                atom.getPosition());
                        renumberedLeafSubstructure.addAtom(renumberedAtom);
                    }
                }
            }
            // nonconsecutive parts are copied without renumbering
            for (Chain chain : model.getAllChains()) {
                OakChain oakChain = (OakChain) chain;
                OakChain renumberedChain = (OakChain) renumberedModel.getChain(chain.getChainIdentifier()).orElseThrow(NoSuchElementException::new);
                for (LeafSubstructure leafSubstructure : oakChain.getNonConsecutivePart()) {
                    OakLeafSubstructure renumberedLeafSubstructure = createLeafSubstructure(leafSubstructure.getIdentifier(), leafSubstructure.getFamily());
                    renumberedLeafSubstructure.setAnnotatedAsHetAtom(true);
                    renumberedChain.addLeafSubstructure(renumberedLeafSubstructure);
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        OakAtom renumberedAtom = new OakAtom(
                                atom.getAtomIdentifier(),
                                atom.getElement(),
                                atom.getAtomName(),
                                atom.getPosition());
                        renumberedLeafSubstructure.addAtom(renumberedAtom);
                    }
                }
            }
        }
        return renumberedStructure;
    }
}
