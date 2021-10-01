package bio.singa.structure.algorithms.gyration;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.mathematics.vectors.Vectors3D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.AtomContainer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

/**
 * An implementation of the gyration radius measurement for the compactness of molecules. The gyration uses atoms and
 * can be applied on any {@link AtomContainer}. Calculation is performed according to:
 * <pre>
 *     Stockwell, GR, Thornton, JM (2006). Conformational diversity of ligands bound to proteins.
 *     J. Mol. Biol., 356, 4:928-44.
 * </pre>
 *
 * @author fk
 */
public class Gyration {

    /**
     * All atoms.
     */
    private final Collection<? extends Atom> atoms;

    /**
     * The gyration radius.
     */
    private double radius;

    /**
     * The centroid or centre of gyration.
     */
    private Vector3D centroid;

    /**
     * Calculates the gyration with the given list of atoms.
     * @param atoms The atoms.
     */
    private Gyration(Collection<? extends Atom> atoms) {
        this.atoms = atoms;
        calculateRadius();
    }

    /**
     * Calculates the gyration for the atoms in the container.
     * @param atomContainer The atom container.
     * @return The Gyration.
     */
    public static Gyration of(AtomContainer atomContainer) {
        return new Gyration(atomContainer.getAllAtoms());
    }

    /**
     * Returns the atoms the gyration has been calculated from.
     * @return The atoms the gyration has been calculated from.
     */
    public Collection<? extends Atom> getAtoms() {
        return atoms;
    }

    /**
     * Returns the centroid of this gyration.
     * @return The centroid of this gyration.
     */
    public Vector3D getCentroid() {
        return centroid;
    }

    /**
     * Returns the radius of this gyration.
     * @return The radius of this gyration.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Calculates the radius of gyration.
     */
    private void calculateRadius() {
        // determine geometric centroid
        List<Vector3D> positions = atoms.stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        centroid = Vectors3D.get3DCentroid(positions);
        double sumOfSquaredDistances = 0.0;
        double sumOfMolecularMass = 0.0;
        for (Atom atom : atoms) {
            sumOfSquaredDistances += atom.getElement().getAtomicMass().getValue().doubleValue()
                    * SQUARED_EUCLIDEAN_METRIC.calculateDistance(atom.getPosition(), centroid);
            sumOfMolecularMass += atom.getElement().getAtomicMass().getValue().doubleValue();
        }
        radius = Math.sqrt(sumOfSquaredDistances / sumOfMolecularMass);
    }
}
