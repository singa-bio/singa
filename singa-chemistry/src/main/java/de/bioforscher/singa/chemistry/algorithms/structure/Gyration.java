package de.bioforscher.singa.chemistry.algorithms.structure;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntity;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors3D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of the gyration radius measurement for the compactness of molecules. The gyration uses atoms and
 * can be applied on any {@link StructuralEntity}.
 * Calculation is performed according to:
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

    public static Gyration of(StructuralEntity<?, ?> structuralEntity) {
        // collect all atoms based on type
        List<Atom> allAtoms = new ArrayList<>();
        if (structuralEntity instanceof Atom) {
            allAtoms.add((Atom) structuralEntity);
        } else if (structuralEntity instanceof LeafSubstructure) {
            allAtoms.addAll(((LeafSubstructure<?, ?>) structuralEntity).getAllAtoms());
        } else if (structuralEntity instanceof BranchSubstructure) {
            allAtoms.addAll(((BranchSubstructure<?, ?>) structuralEntity).getAllAtoms());
        }
        return new Gyration(allAtoms);
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public Vector3D getCentroid() {
        return centroid;
    }

    public double getRadius() {
        return this.radius;
    }

    private void calculateRadius() {
        // determine geometric centroid
        List<Vector3D> positions = this.atoms.stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        this.centroid = Vectors3D.getCentroid(positions);
        double sumOfSquaredDistances = 0.0;
        double sumOfMolecularMass = 0.0;
        for (int i = 0; i < this.atoms.size(); i++) {
            Atom atom = this.atoms.get(i);
            sumOfSquaredDistances += atom.getElement().getAtomicMass().getValue().doubleValue()
                    * VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC.calculateDistance(atom.getPosition(), centroid);
            sumOfMolecularMass += atom.getElement().getAtomicMass().getValue().doubleValue();
        }
        this.radius = Math.sqrt(sumOfSquaredDistances / sumOfMolecularMass);
    }
}
