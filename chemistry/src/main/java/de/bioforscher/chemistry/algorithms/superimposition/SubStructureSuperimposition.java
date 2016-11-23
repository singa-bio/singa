package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.mathematics.algorithms.superimposition.Superimposition;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link Superimposition} for {@link SubStructure}s.
 *
 * @author fk
 */
public class SubStructureSuperimposition implements Superimposition<SubStructure> {

    private final double rmsd;
    private final Vector translation;
    private final Matrix rotation;
    private final List<SubStructure> mappedCandidate;

    public SubStructureSuperimposition(double rmsd, Vector translation, Matrix rotation, List<SubStructure> mappedCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.mappedCandidate = mappedCandidate;
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
    public List<SubStructure> getMappedCandidate() {
        return this.mappedCandidate;
    }

    @Override
    public List<SubStructure> applyTo(List<SubStructure> candidate) {
        List<SubStructure> copyOfCandidate = candidate.stream()
                .map(SubStructure::getCopy)
                .collect(Collectors.toList());
        // apply superimposition to every atom of every substructure of the candidate
        copyOfCandidate.stream()
                .map(SubStructure::getAtomContainingSubstructures)
                .flatMap(List::stream)
                .map(SubStructure::getAllAtoms)
                .flatMap(List::stream)
                .forEach(atom -> this.rotation
                        .transpose()
                        .multiply(atom.getPosition())
                        .add(this.translation));
        return copyOfCandidate;
    }
}
