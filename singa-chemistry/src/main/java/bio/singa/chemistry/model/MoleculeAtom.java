package bio.singa.chemistry.model;

import bio.singa.mathematics.graphs.model.AbstractNode;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.chemistry.model.elements.Element;
import bio.singa.chemistry.model.elements.ElementProvider;

import java.util.Objects;

/**
 * @author cl
 */
public class MoleculeAtom extends AbstractNode<MoleculeAtom, Vector2D, Integer> {

    private Element element;

    public MoleculeAtom(int identifier) {
        super(identifier);
        element = ElementProvider.UNKOWN;
    }

    public MoleculeAtom(int identifier, Vector2D position) {
        this(identifier, position, ElementProvider.UNKOWN);
    }

    public MoleculeAtom(int identifier, Vector2D position, Element element) {
        super(identifier, position);
        this.element = element;
    }

    private MoleculeAtom(MoleculeAtom moleculeAtom) {
        super(moleculeAtom);
        element = moleculeAtom.element;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return element.toString() + ":" + getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoleculeAtom)) return false;
        if (!super.equals(o)) return false;
        MoleculeAtom that = (MoleculeAtom) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), element);
    }

    @Override
    public MoleculeAtom getCopy() {
        return new MoleculeAtom(this);
    }

}
