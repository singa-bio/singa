package de.bioforscher.singa.chemistry.descriptive.molecules;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.graphs.model.AbstractNode;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class MoleculeAtom extends AbstractNode<MoleculeAtom, Vector2D, Integer> {

    private Element element;

    public MoleculeAtom(int identifier) {
        super(identifier);
        this.element = ElementProvider.UNKOWN;
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
        this.element = moleculeAtom.element;
    }

    public Element getElement() {
        return this.element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return this.element.toString() + ":" + this.getIdentifier();
    }

    @Override
    public MoleculeAtom getCopy() {
        return new MoleculeAtom(this);
    }
}
