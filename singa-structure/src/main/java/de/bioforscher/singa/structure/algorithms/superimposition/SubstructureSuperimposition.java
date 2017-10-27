package de.bioforscher.singa.structure.algorithms.superimposition;

import de.bioforscher.singa.mathematics.algorithms.superimposition.Superimposition;
import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.interfaces.AtomContainer;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link Superimposition} for {@link AtomContainer}s.
 *
 * @author fk
 */
public class SubstructureSuperimposition implements Superimposition<LeafSubstructure<?>> {

    private final double rmsd;
    private final Vector translation;
    private final Matrix rotation;
    private final List<LeafSubstructure<?>> reference;
    private final List<LeafSubstructure<?>> candidate;
    private final List<LeafSubstructure<?>> mappedCandidate;
    private final List<LeafSubstructure<?>> mappedFullCandidate;

    public SubstructureSuperimposition(double rmsd, Vector translation, Matrix rotation,
                                       List<LeafSubstructure<?>> reference,
                                       List<LeafSubstructure<?>> candidate,
                                       List<LeafSubstructure<?>> mappedCandidate,
                                       List<LeafSubstructure<?>> mappedFullCandidate) {
        this.rmsd = rmsd;
        this.translation = translation;
        this.rotation = rotation;
        this.reference = reference;
        this.candidate = candidate;
        this.mappedCandidate = mappedCandidate;
        this.mappedFullCandidate = mappedFullCandidate;
    }

    @Override
    public List<LeafSubstructure<?>> getReference() {
        return this.reference;
    }

    @Override
    public List<LeafSubstructure<?>> getCandidate() {
        return this.candidate;
    }

    /**
     * Returns a string representation of the {@link SubstructureSuperimposition}, that is:
     * <pre>[RMSD]_[PDB-ID of mapped candidates]_[candidate residues]...</pre>
     * TODO move this to interface, as other superimpositions should also get a string representation.
     *
     * @return The full string representation of this {@link SubstructureSuperimposition}.
     */
    public String getStringRepresentation() {
        return this.mappedCandidate.stream()
                .sorted(Comparator.comparing(LeafSubstructure::getIdentifier))
                .map(leafSubstructure -> leafSubstructure.getChainIdentifier() + "-"
                        + leafSubstructure.getIdentifier().getSerial()
                        + (leafSubstructure.getInsertionCode() == LeafIdentifier.DEFAULT_INSERTION_CODE ? "" : leafSubstructure.getInsertionCode())
                )
                .collect(Collectors.joining("_", getFormattedRmsd() + "_"
                        + this.mappedCandidate.get(0).getPdbIdentifier()
                        + "_", ""));
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
    public List<LeafSubstructure<?>> getMappedCandidate() {
        return this.mappedCandidate;
    }

    @Override
    public List<LeafSubstructure<?>> getMappedFullCandidate() {
        return this.mappedFullCandidate;
    }

    @Override
    public List<LeafSubstructure<?>> applyTo(List<LeafSubstructure<?>> candidate) {
        List<LeafSubstructure<?>> copyOfCandidate = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : candidate) {
            copyOfCandidate.add(leafSubstructure.getCopy());
        }

        // apply superimposition to every atom of every substructure of the candidate
        copyOfCandidate.stream()
                .map(AtomContainer::getAllAtoms)
                .flatMap(List::stream)
                .forEach(atom -> atom.setPosition(this.rotation.transpose()
                        .multiply(atom.getPosition())
                        .add(this.translation).as(Vector3D.class)));
        return copyOfCandidate;
    }
}
