package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.physical.model.StructuralEntity;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.core.utility.Nameable;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;

/**
 * An Atom is the physical instance of an atom in three dimensional space. This is the smallest entity representable
 * in a {@link Structure}.
 */
public class Atom implements StructuralEntity<Atom>, Nameable {

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
     * @param identifier The identifier.
     * @param element The element.
     * @param atomName The name.
     * @param position The position.
     */
    public Atom(int identifier, Element element, AtomName atomName, Vector3D position) {
        this.identifier = identifier;
        this.element = element;
        this.atomName = atomName;
        this.position = position;
        this.neighbours = new ArrayList<>();
    }

    /**
     * Returns the Element.
     * @return The Element.
     */
    public Element getElement() {
        return this.element;
    }

    /**
     * Sets the Element.
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
     * @param identifier The identifier.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public Vector3D getPosition() {
        return this.position;
    }

    /**
     * Sets the position.
     * @param position The position.
     */
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

    public void setNeighbours(List<Atom> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return "Atom{" +
                "identifier=" + this.identifier +
                ", element=" + this.element +
                ", name='" + getName() + '\'' +
                ", position=" + this.position +
                '}';
    }

    @Override
    public String getName() {
        return this.atomName.getName();
    }

    public AtomName getAtomName() {
        return this.atomName;
    }

    public void setAtomName(AtomName atomName) {
        this.atomName = atomName;
    }
}
