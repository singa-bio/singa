package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;

/**
 * An Atom is the physical instance of an atom in three dimensional space. This is the smallest entity representable
 * in a {@link SubStructure}.
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

    @Override public String toString() {
        return "RegularAtom{" +
               "identifier=" + identifier +
               ", element=" + element +
               ", atomName=" + atomName +
               ", atomNameString='" + atomNameString + '\'' +
               ", position=" + position +
               '}';
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

}
