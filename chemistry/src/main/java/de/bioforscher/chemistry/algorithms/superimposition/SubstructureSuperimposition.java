package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.mathematics.algorithms.superimposition.Superimposition;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link Superimposition} for {@link BranchSubstructure}s.
 *
 * @author fk
 */
public class SubstructureSuperimposition implements Superimposition<LeafSubstructure<?,?>> {

    private final double rmsd;
    private final Vector translation;
    private final Matrix rotation;
    private final List<LeafSubstructure<?,?>> mappedCandidate;

    public SubstructureSuperimposition(double rmsd, Vector translation, Matrix rotation, List<LeafSubstructure<?,?>> mappedCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.mappedCandidate = mappedCandidate;
    }

    @Override
    public String toString() {
        return "SubstructureSuperimposition{" +
                "rmsd=" + this.rmsd +
                '}';
    }

    @Override
    public double getRmsd() {
        return this.rmsd;
    }

    @Override
    public Vector getTranslation() {
        return this.translation;
    }

    @Override
    public Matrix getRotation() {
        return this.rotation;
    }

    @Override
    public List<LeafSubstructure<?,?>> getMappedCandidate() {
        return this.mappedCandidate;
    }

    @Override
    public List<LeafSubstructure<?,?>> applyTo(List<LeafSubstructure<?,?>> candidate) {
        List<LeafSubstructure<?,?>> copyOfCandidate = candidate.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());
        // apply superimposition to every atom of every substructure of the candidate
        copyOfCandidate.stream()
                .map(Substructure::getAllAtoms)
                .flatMap(List::stream)
                .forEach(atom -> this.rotation
                        .transpose()
                        .multiply(atom.getPosition())
                        .add(this.translation));
        return copyOfCandidate;
    }
}
