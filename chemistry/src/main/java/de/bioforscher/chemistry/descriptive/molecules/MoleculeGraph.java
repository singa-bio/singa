package de.bioforscher.chemistry.descriptive.molecules;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.bonds.BondType;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.graphs.model.GenericEdge;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;

/**
 * Created by Christoph on 21/11/2016.
 */
public class MoleculeGraph extends AbstractGraph<MoleculeAtom, MoleculeBond, Vector2D> {

    private int atomCounter;

    /**
     * A iterating variable to add a new bond.
     */
    private int nextBondIdentifier;

    public int getNextNodeIdentifier() {
        return this.atomCounter++;
    }

    public int addNextAtom(char elementSymbol) {
        return addNextAtom(String.valueOf(elementSymbol));
    }

    public int addNextAtom(String elementSymbol) {
        return addNextAtom(ElementProvider.getElementBySymbol(elementSymbol));
    }

    public int addNextAtom(Element element) {
        MoleculeAtom atom = new MoleculeAtom(getNextNodeIdentifier(),
                VectorUtilities.generateRandomVectorInRectangle(new Rectangle(100,100)), element);
        addNode(atom);
        return atom.getIdentifier();
    }

    public int addNextAtom(Element element, int charge) {
        Element ion = element.asIon(charge);
        return addNextAtom(ion);
    }

    public int addNextAtom(Element element, int charge, int numberOfNeutrons) {
        Element ion = element.asIon(charge);
        ion.asIsotope(numberOfNeutrons);
        return addNextAtom(ion);
    }

    @Override
    public void addEdgeBetween(MoleculeAtom source, MoleculeAtom target) {
        addEdgeBetween(source, target, MoleculeBondType.SINGLE_BOND);
    }

    public void addEdgeBetween(MoleculeAtom source, MoleculeAtom target, MoleculeBondType bondType) {
        MoleculeBond edge = new MoleculeBond(this.nextBondIdentifier);
        edge.setType(bondType);
        edge.setSource(source);
        edge.setTarget(target);
        addEdge(this.nextBondIdentifier, edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        this.nextBondIdentifier++;
    }

}
