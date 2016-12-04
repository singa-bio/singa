package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;

/**
 * An Atom is the physical instance of an atom in three dimensional space. This is the smallest entity representable
 * in a {@link BranchSubstructure}.
 */
public class RegularAtom implements Atom {

    /**
     * The identifier.
     */
    private int identifier;
    /**
     * The element.
     */
    private Element element;
    /**
     * An additional name such as CA or CB.
     */
    private AtomName atomName;
    /**
     * The AtomName as String
     */
    private String atomNameString;
    /**
     * The position.
     */
    private Vector3D position;
    /**
     * References of the neighboring atoms.
     */
    private List<Atom> neighbours;

    /**
     * Creates a new atom with the given identifier, element, name and position.
     *
     * @param identifier     The identifier.
     * @param element        The element.
     * @param atomNameString The name as String.
     * @param position       The position.
     */
    public RegularAtom(int identifier, Element element, String atomNameString, Vector3D position) {
        this.atomNameString = atomNameString;
        this.atomName = AtomName.getAtomNameFromString(atomNameString);
        this.identifier = identifier;
        this.element = element;
        this.position = position;
        this.neighbours = new ArrayList<>();
    }

    /**
     * This is a copy constructor. Creates a new atom with the same attributes as the given atom. The neighbours of this
     * atom are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations to the
     * lifecycle of the atom to copy. If you want to keep the neighbouring atoms, copy the superordinate
     * substructure that contains this atom and it will also traverse and copy the neighbouring atoms.
     *
     *  @param atom The atom to copy.
     */
    public RegularAtom(Atom atom) {
        this.atomNameString = atom.getAtomNameString();
        this.atomName = atom.getAtomName();
        this.identifier = atom.getIdentifier();
        this.element =  atom.getElement();
        this.position = new Vector3D(atom.getPosition());
        this.neighbours = new ArrayList<>();
    }

    /**
     * Returns the Element.
     *
     * @return The Element.
     */
    @Override
    public Element getElement() {
        return this.element;
    }

    /**
     * Sets the Element.
     *
     * @param element The element.
     */
    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * Sets the identifier.
     *
     * @param identifier The identifier.
     */
    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public Vector3D getPosition() {
        return this.position;
    }

    /**
     * Sets the position.
     *
     * @param position The position.
     */
    @Override
    public void setPosition(Vector3D position) {
        this.position = position;
    }

    @Override
    public void addNeighbour(Atom node) {
        this.neighbours.add(node);
    }

    @Override
    public List<Atom> getNeighbours() {
        return this.neighbours;
    }

    @Override
    public int getDegree() {
        return this.neighbours.size();
    }

    @Override
    public String getAtomNameString() {
        return this.atomNameString;
    }

    @Override
    public void setAtomNameString(String atomNameString) {
        this.atomNameString = atomNameString;
    }

    @Override
    public AtomName getAtomName() {
        return this.atomName;
    }

    @Override
    public void setAtomName(AtomName atomName) {
        this.atomName = atomName;
    }

    @Override
    public Atom getCopy() {
        return new RegularAtom(this);
    }

    @Override
    public String toString() {
        return "RegularAtom{" +
                "identifier=" + this.identifier +
                ", element=" + this.element +
                ", atomName=" + this.atomName +
                ", atomNameString='" + this.atomNameString + '\'' +
                ", position=" + this.position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegularAtom that = (RegularAtom) o;
        if (this.identifier != that.identifier) return false;
        if (this.element != null ? !this.element.equals(that.element) : that.element != null) return false;
        if (!this.atomNameString.equals(that.atomNameString)) return false;
        return this.position.equals(that.position);
    }

    @Override
    public int hashCode() {
        int result = this.identifier;
        result = 31 * result + (this.element != null ? this.element.hashCode() : 0);
        result = 31 * result + this.atomNameString.hashCode();
        result = 31 * result + this.position.hashCode();
        return result;
    }
}
