package de.bioforscher.singa.structure.model.interfaces;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors3D;
import de.bioforscher.singa.structure.model.families.StructuralFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.oak.Exchangeable;
import de.bioforscher.singa.structure.parser.pdb.structures.tokens.AtomToken;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A leaf substructure is a collection of atoms with meta data assigned to it. A leaf substructure can be part of a
 * macro molecule, such as amino acid residues or nucleotides, or independent small molecules.
 *
 * @author cl
 */
public interface LeafSubstructure<FamilyType extends StructuralFamily> extends AtomContainer, Exchangeable<FamilyType> {

    /**
     * Returns the complete leaf identifier. The leaf identifier consists of the PDB identifer, the model identifier,
     * the chain identifier, the serial of the leaf substructure and optionally an insertion code.
     *
     * @return The leaf identifier.
     * @see LeafIdentifier
     */
    LeafIdentifier getIdentifier();

    default String getPdbIdentifier() {
        return getIdentifier().getPdbIdentifier();
    }

    default Integer getModelIdentifier() {
        return getIdentifier().getModelIdentifier();
    }

    default String getChainIdentifier() {
        return getIdentifier().getChainIdentifier();
    }

    default Integer getSerial() {
        return getIdentifier().getSerial();
    }

    default char getInsertionCode() {
        return getIdentifier().getInsertionCode();
    }

    default Vector3D getPosition() {
        return Vectors3D.getCentroid(getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()));
    }

    default List<String> getPdbLines() {
        return AtomToken.assemblePDBLine(this);
    }

    boolean containsAtomWithName(String atomName);

    Optional<Atom> getAtomByName(String atomName);

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
     *     1C0A-1-B-631 C
     *     1C0A-1-A-78 ARG
     *     1C0A-1-A-1048 HOH
     * </pre>
     *
     * @return The flat String representation of this leaf substructure.
     */
    default String flatToString() {
        return getIdentifier() + " " + getThreeLetterCode();
    }

}
