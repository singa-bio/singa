package de.bioforscher.chemistry.physical;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class Atom implements StructuralEntity<Atom> {

    private int identifier;
    private Element element;
    private String atomName;
    private Vector3D position;

    private List<Atom> neighbours;

    public Atom(int identifier, Element element, String atomName, Vector3D position) {
        this.identifier = identifier;
        this.element = element;
        this.atomName = atomName;
        this.position = position;
        this.neighbours = new ArrayList<>();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getAtomName() {
        return atomName;
    }

    public void setAtomName(String atomName) {
        this.atomName = atomName;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    @Override
    public void addNeighbour(Atom node) {
        this.neighbours.add(node);
    }

    @Override
    public List<Atom> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Atom> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return "Atom{" +
                "identifier=" + identifier +
                ", element=" + element +
                ", atomName='" + atomName + '\'' +
                ", position=" + position +
                '}';
    }
}
