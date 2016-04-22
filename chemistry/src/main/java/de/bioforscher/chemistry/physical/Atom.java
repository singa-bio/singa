package de.bioforscher.chemistry.physical;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.mathematics.graphs.model.AbstractNode;
import de.bioforscher.mathematics.vectors.Vector3D;

public class Atom extends AbstractNode<Atom, Vector3D> {

    private Element element;

    public Atom(int identifier, Vector3D position, Element element) {
        super(identifier, position);
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "Atom [element=" + this.element + ", " + super.getPosition() + "]";
    }

}
