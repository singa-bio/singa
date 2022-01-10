package bio.singa.structure.model.interfaces;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.mathematics.vectors.Vectors3D;
import bio.singa.structure.model.families.StructuralFamily;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A leaf substructure is a collection of atoms with meta data assigned to it. A leaf substructure can be part of a
 * macro molecule, such as amino acid residues or nucleotides, or independent small molecules.
 *
 * @author cl
 */
public interface LeafSubstructure extends AtomContainer {

    /**
     * Returns the complete leaf identifier. The leaf identifier consists of the PDB identifer, the model identifier,
     * the chain identifier, the serial of the leaf substructure and optionally an insertion code.
     *
     * @return The leaf identifier.
     * @see LeafIdentifier
     */
    LeafIdentifier getIdentifier();

    /**
     * Returns the {@link StructuralFamily} of this entity.
     *
     * @return the {@link StructuralFamily}
     */
    StructuralFamily getFamily();

    default Vector3D getPosition() {
        return Vectors3D.get3DCentroid(getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()));
    }

    Optional<? extends Atom> getAtomByName(String atomName);

    void setAnnotatedAsHeteroAtom(boolean annotatedAsHetAtom);

    boolean isAnnotatedAsHeteroAtom();

    /**
     * Returns the three letter code of the chemical compound represented by this leaf substructure.
     *
     * @return The three letter code of the chemical compound represented by this leaf substructure.
     */
    String getThreeLetterCode();


    /**
     * Returns a copy of this {@link LeafSubstructure}.
     *
     * @param <LeafImplementation> The type of the {@link LeafSubstructure}.
     * @return A copy of this leaf substructure.
     */
    <LeafImplementation extends LeafSubstructure> LeafImplementation getCopy();

    /**
     * Returns the flat String representation of this leaf substructure in the form:
     * <pre>
     *     [LeafIdentifier] [ThreeLetterCode]
     * </pre>
     * For example:
     * <pre>
     *     1c0a-1-B-631 C
     *     1c0a-1-A-78 ARG
     *     1c0a-1-A-1048 HOH
     * </pre>
     *
     * @return The flat String representation of this leaf substructure.
     */
    default String flatToString() {
        return getIdentifier() + " " + getThreeLetterCode();
    }
}
