package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.mathematics.algorithms.superimposition.Superimposition;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fkaiser on 10.11.16.
 */
public class SubStructureSuperimposer {

    private List<SubStructure> reference;
    private List<SubStructure> candidate;
    private double rmsd;
    private Vector translation;
    private Matrix rotation;
    private Vector3D referenceCentroid;

    private SubStructureSuperimposer(SubStructure reference, SubStructure candidate) {
        this.reference = reference.getAtomContainingSubstructures();
        this.candidate = candidate.getAtomContainingSubstructures();

        if (this.reference.size() != this.candidate.size())
            throw new IllegalArgumentException("Two lists of substructures cannot be superimposed if they " +
                    "differ in size.");
    }

    public static Superimposition calculateSVDSuperimposition(SubStructure reference, SubStructure candidate) {

        return new SubStructureSuperimposer(reference, candidate).calculateSubStructureSuperimposition();
    }

    private Superimposition calculateSubStructureSuperimposition() {
        center();
        calculateRotation();
        calculateTranslation();
        applyMapping();
        calculateRMSD();
//        return new Superimposition(this.rmsd, this.translation, this.rotation, this.mappedCandidate);
        return null;
    }

    private void initialize() {
    }

    private void calculateRMSD() {

    }

    private void applyMapping() {

    }

    private void calculateTranslation() {
    }

    private void calculateRotation() {
    }

    private void center() {
        List<Vector> referenceAtoms = this.reference.stream().map(SubStructure::getAllAtoms)
                .flatMap(Collection::stream).map(Atom::getPosition)
                .collect(Collectors.toList());
        this.referenceCentroid = VectorUtilities.getCentroid(referenceAtoms).as(Vector3D.class);
        this.shiftedReference = this.reference.stream().map(this::centerSubStructure).collect(Collectors.toList());
    }

    private SubStructure centerSubStructure(SubStructure subStructure, Vector3D centroid) {
        List<Atom> centeredAtoms = new ArrayList<>();
        for (Atom atom : subStructure.getAllAtoms()) {
            centeredAtoms.add(new RegularAtom(atom.getIdentifier(), atom.getElement(), atom.getAtomNameString(),
                    atom.getPosition().subtract(centroid)));
        }
        return new SubStructure()
    }
}
