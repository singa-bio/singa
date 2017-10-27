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
     * The pdbIdentifier.
     */
    private int identifier;

    /**
     * The element.
     */
    private Element element;

    /**
     * The AtomName as String
     */
    private String atomName;

    /**
     * The position.
     */
    private Vector3D position;

    /**
     * References of the neighboring atoms.
     */
    private Set<OakAtom> neighbours;

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
        this.neighbours = new HashSet<>();
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
        this.atomName = atom.atomName;
        this.identifier = atom.identifier;
        this.element = atom.element;
        this.position = new Vector3D(atom.position);
        this.neighbours = new HashSet<>();
    }


    @Override
    public Integer getIdentifier() {
        return this.identifier;
    }

    @Override
    public Vector3D getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Vector3D position) {
        this.position = position;
    }

    @Override
    public Element getElement() {
        return this.element;
    }

    @Override
    public String getAtomName() {
        return this.atomName;
    }

    public void addNeighbour(OakAtom node) {
        this.neighbours.add(node);
    }

    public Set<OakAtom> getNeighbours() {
        return this.neighbours;
    }

    public OakAtom getCopy() {
        return new OakAtom(this);
    }

    @Override
    public String toString() {
        return "Atom: " + this.atomName +
                " " + this.identifier +
                " (" + this.position.getX() + ", " + this.position.getY() + ", " + this.position.getZ() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OakAtom that = (OakAtom) o;

        if (this.identifier != that.identifier) return false;
        if (this.atomName != null ? !this.atomName.equals(that.atomName) : that.atomName != null)
            return false;
        return this.position != null ? this.position.equals(that.position) : that.position == null;
    }

    @Override
    public int hashCode() {
        int result = this.identifier;
        result = 31 * result + (this.atomName != null ? this.atomName.hashCode() : 0);
        result = 31 * result + (this.position != null ? this.position.hashCode() : 0);
        return result;
    }

}
