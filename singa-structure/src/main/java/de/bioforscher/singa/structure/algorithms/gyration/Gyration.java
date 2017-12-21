package de.bioforscher.singa.structure.algorithms.gyration;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors3D;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.AtomContainer;

import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

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

    private final List<Atom> atoms;
    private double radius;
    private Vector3D centroid;

    private Gyration(List<Atom> atoms) {
        this.atoms = atoms;
        calculateRadius();
    }

    public static Gyration of(AtomContainer structuralEntity) {
        return new Gyration(structuralEntity.getAllAtoms());
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public Vector3D getCentroid() {
        return centroid;
    }

    public double getRadius() {
        return radius;
    }

    private void calculateRadius() {
        // determine geometric centroid
        List<Vector3D> positions = atoms.stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        centroid = Vectors3D.getCentroid(positions);
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
