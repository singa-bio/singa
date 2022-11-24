package bio.singa.structure.model.interfaces;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.chemistry.model.elements.Element;

/**
 * An Atom is the physical instance of an atom in three dimensional space.
 *
 * @author cl
 */
public interface Atom {

    /**
     * Returns the atom identifier, an integer greater or equal to 1. The identifier of the atom is not unique.
     * Depending on the structure format you are reading there might be different atom identifiers for the same pdb
     * structure. This means, if you have parse the pdb file of a pdb structure or the MMTF file of the same structure,
     * you cannot rely on the identity of atoms in both structures.
     *
     * @return The atom identifier.
     */
    int getAtomIdentifier();

    /**
     * Returns the position in three dimensional space.
     *
     * @return The position.
     */
    Vector3D getPosition();

    /**
     * Sets the position in three dimensional space.
     *
     * @param position The position.
     */
    void setPosition(Vector3D position);

    double getBFactor();

    void setBFactor(double bFactor);

    /**
     * Returns the {@link Element}.
     *
     * @return The {@link Element}.
     */
    Element getElement();

    /**
     * Returns a short atom name. For example "CA" for C-alpha atoms in amino acids. The atom names might not be
     * universally the same. Atoms with the name "OP2" might be called "O2P" in other structures.
     *
     * @return A short atom name.
     */
    String getAtomName();

    /**
     * Sets the name of the atom.
     */
    void setAtomName(String atomName);

    Atom getCopy();

    /**
     * Returns the flat String representation of this atom in the form:
     * <pre>
     *     [AtomIdentifier]: [AtomName] ([xCoordinate], [yCoordinate], [zCoordinate])
     * </pre>
     * For example:
     * <pre>
     *     5444: CA (70.31099700927734, 35.60300064086914, -10.967000007629395)
     *     412: N2 (62.20399856567383, -8.505999565124512, 22.573999404907227)
     * </pre>
     *
     * @return The flat String representation of this atom.
     */
    default String flatToString() {
        return getAtomIdentifier() + ": " + getAtomName() + " " + getPosition();
    }

}
