package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.mathematics.algorithms.superimposition.Superimposition;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link Superimposition} for {@link BranchSubstructure}s.
 *
 * @author fk
 */
public class SubstructureSuperimposition implements Superimposition<LeafSubstructure<?, ?>> {

    private final double rmsd;
    private final Vector translation;
    private final Matrix rotation;
    private final List<LeafSubstructure<?, ?>> mappedCandidate;
    private final List<LeafSubstructure<?, ?>> mappedFullCandidate;

    public SubstructureSuperimposition(double rmsd, Vector translation, Matrix rotation, List<LeafSubstructure<?, ?>> mappedCandidate,
                                       List<LeafSubstructure<?, ?>> mappedFullCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.mappedCandidate = mappedCandidate;
        this.mappedFullCandidate = mappedFullCandidate;
    }

    /**
     * Returns a string representation of the {@link SubstructureSuperimposition}, that is:
     * <p>
     * <pre>[RMSD]_[PDB-ID of mapped candidates]|[candidate residues]...</pre>
     *
     * TODO move this to interface, as other superimpositions should also get a string representation.
     *
     * @return The full string representation of this {@link SubstructureSuperimposition}.
     */
    public String getStringRepresentation() {
        return this.mappedCandidate.stream()
                .map(Object::toString)
                .collect(Collectors.joining("_", this.rmsd + "_" + this.mappedCandidate.get(0).getPdbId()
                        + "|", ""));
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
    public List<LeafSubstructure<?, ?>> getMappedCandidate() {
        return this.mappedCandidate;
    }

    @Override
    public List<LeafSubstructure<?, ?>> getMappedFullCandidate() {
        return this.mappedFullCandidate;
    }

    @Override
    public List<LeafSubstructure<?, ?>> applyTo(List<LeafSubstructure<?, ?>> candidate) {
        List<LeafSubstructure<?, ?>> copyOfCandidate = candidate.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());
        // apply superimposition to every atom of every substructure of the candidate
        copyOfCandidate.stream()
                .map(Substructure::getAllAtoms)
                .flatMap(List::stream)
                .forEach(atom -> atom.setPosition(this.rotation
                        .transpose()
                        .multiply(atom.getPosition())
                        .add(this.translation).as(Vector3D.class)));
        return copyOfCandidate;
    }
}
