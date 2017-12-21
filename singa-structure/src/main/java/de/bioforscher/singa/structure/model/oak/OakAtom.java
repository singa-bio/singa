package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.interfaces.Atom;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class OakAtom implements Atom {

    /**
     * The PDB identifier.
     */
    private final int identifier;

    /**
     * The element.
     */
    private final Element element;

    /**
     * The AtomName as String
     */
    private final String atomName;

    /**
     * References of the neighboring atoms.
     */
    private final Set<OakAtom> neighbours;

    /**
     * The position.
     */
    private Vector3D position;

    /**
     * Creates a new atom with the given pdbIdentifier, element, name and position.
     *
     * @param identifier The pdbIdentifier.
     * @param element The element.
     * @param atomName The name (pdb).
     * @param position The position.
     */
    public OakAtom(int identifier, Element element, String atomName, Vector3D position) {
        this.atomName = atomName;
        this.identifier = identifier;
        this.element = element;
        this.position = position;
        neighbours = new HashSet<>();
    }

    /**
     * This is a copy constructor. Creates a new atom with the same attributes as the given atom. The neighbours of this
     * atom are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations to the
     * lifecycle of the atom to copy. If you want to keep the neighbouring atoms, copy the superordinate substructure
     * that contains this atom and it will also traverse and copy the neighbouring atoms.
     *
     * @param atom The atom to copy.
     */
    public OakAtom(OakAtom atom) {
        atomName = atom.atomName;
        identifier = atom.identifier;
        element = atom.element;
        position = new Vector3D(atom.position);
        neighbours = new HashSet<>();
    }


    @Override
    public Integer getAtomIdentifier() {
        return identifier;
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3D position) {
        this.position = position;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getAtomName() {
        return atomName;
    }

    public void addNeighbour(OakAtom node) {
        neighbours.add(node);
    }

    public Set<OakAtom> getNeighbours() {
        return neighbours;
    }

    public OakAtom getCopy() {
        return new OakAtom(this);
    }

    @Override
    public String toString() {
        return flatToString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OakAtom that = (OakAtom) o;

        if (identifier != that.identifier) return false;
        if (atomName != null ? !atomName.equals(that.atomName) : that.atomName != null)
            return false;
        return position != null ? position.equals(that.position) : that.position == null;
    }

    @Override
    public int hashCode() {
        int result = identifier;
        result = 31 * result + (atomName != null ? atomName.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

}
