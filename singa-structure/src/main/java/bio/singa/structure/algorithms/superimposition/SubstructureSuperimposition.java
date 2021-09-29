package bio.singa.structure.algorithms.superimposition;

import bio.singa.mathematics.algorithms.superimposition.Superimposition;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.AtomContainer;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.LeafSubstructureContainer;
import bio.singa.structure.model.oak.PdbLeafIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.structure.model.oak.PdbLeafIdentifier.DEFAULT_INSERTION_CODE;


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
        return reference;
    }

    @Override
    public List<LeafSubstructure<?>> getCandidate() {
        return candidate;
    }

    /**
     * Returns a string representation of the {@link SubstructureSuperimposition}, that is:
     * <pre>[RMSD]_[PDB-ID of mapped candidates]_[candidate residues]...</pre>
     * The ordering of {@link PdbLeafIdentifier}s corresponds to the actual found optimal alignment to the reference.
     * TODO move this to interface, as other superimpositions should also get a string representation.
     *
     * @return The full string representation of this {@link SubstructureSuperimposition}.
     */
    public String getStringRepresentation() {
        return mappedCandidate.stream()
                .map(leafSubstructure -> leafSubstructure.getChainIdentifier() + "-"
                        + leafSubstructure.getIdentifier().getSerial()
                        + (leafSubstructure.getIdentifier().getInsertionCode() != DEFAULT_INSERTION_CODE ? leafSubstructure.getIdentifier().getInsertionCode() : ""))
                .collect(Collectors.joining("_", getFormattedRmsd() + "_"
                        + mappedCandidate.get(0).getPdbIdentifier()
                        + "_", ""));
    }

    @Override
    public String toString() {
        return "SubstructureSuperimposition{" +
                "rmsd=" + rmsd +
                '}';
    }

    @Override
    public double getRmsd() {
        return rmsd;
    }

    @Override
    public Vector getTranslation() {
        return translation;
    }

    @Override
    public Matrix getRotation() {
        return rotation;
    }

    @Override
    public List<LeafSubstructure<?>> getMappedCandidate() {
        return mappedCandidate;
    }

    @Override
    public List<LeafSubstructure<?>> getMappedFullCandidate() {
        return mappedFullCandidate;
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
                .forEach(atom -> atom.setPosition(rotation.transpose()
                        .multiply(atom.getPosition())
                        .add(translation).as(Vector3D.class)));
        return copyOfCandidate;
    }

    public <T extends LeafSubstructureContainer> T applyTo(T candidate) {
        T candidateCopy = candidate.getCopy();
        // apply superimposition to every atom of every substructure of the candidate
        candidateCopy.getAllAtoms()
                .forEach(atom -> atom.setPosition(rotation.transpose()
                        .multiply(atom.getPosition())
                        .add(translation).as(Vector3D.class)));
        return candidateCopy;
    }

}
