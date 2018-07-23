package bio.singa.structure.model.molecules;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.AbstractMapGraph;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.structure.elements.Element;
import bio.singa.structure.elements.ElementProvider;
import bio.singa.structure.model.oak.BondType;

/**
 * @author cl
 */
public class MoleculeGraph extends AbstractMapGraph<MoleculeAtom, MoleculeBond, Vector2D, Integer> {

    // TODO add a reference of this to Species

    public int addNextAtom(char elementSymbol) {
        return addNextAtom(String.valueOf(elementSymbol));
    }

    public int addNextAtom(String elementSymbol) {
        return addNextAtom(ElementProvider.getElementBySymbol(elementSymbol).orElseThrow(() -> new IllegalArgumentException("The symbol " + elementSymbol + " represents no valid element.")));
    }

    public int addNextAtom(Element element) {
        MoleculeAtom atom = new MoleculeAtom(nextNodeIdentifier(),
                Vectors.generateRandom2DVector(new Rectangle(100, 100)), element);
        addNode(atom);
        return atom.getIdentifier();
    }

    public int addNextAtom(Element element, int charge) {
        final Element ion = element.asIon(charge);
        return addNextAtom(ion);
    }

    public int addNextAtom(Element element, int charge, int massNumber) {
        final Element modifiedElement = element.asIon(charge).asIsotope(massNumber);
        return addNextAtom(modifiedElement);
    }

    @Override
    public int addEdgeBetween(int identifier, MoleculeAtom source, MoleculeAtom target) {
        return addEdgeBetween(new MoleculeBond(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(MoleculeAtom source, MoleculeAtom target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    public int addEdgeBetween(MoleculeAtom source, MoleculeAtom target, MoleculeBondType bondType) {
        final MoleculeBond bond = new MoleculeBond(nextEdgeIdentifier());
        bond.setType(bondType);
        return addEdgeBetween(bond, source, target);
    }

    int addEdgeBetween(int edgeIdentifier, int sourceIdentifier, int targetIdentifier, BondType bondType) {
        final MoleculeBond bond = new MoleculeBond(edgeIdentifier);
        switch (bondType) {
            case SINGLE_BOND:
            default:
                bond.setType(MoleculeBondType.SINGLE_BOND);
                break;
            case DOUBLE_BOND:
                bond.setType(MoleculeBondType.DOUBLE_BOND);
                break;
            case TRIPLE_BOND:
                bond.setType(MoleculeBondType.TRIPLE_BOND);
                break;
        }
        MoleculeAtom source = getNode(sourceIdentifier);
        MoleculeAtom target = getNode(targetIdentifier);
        addEdgeBetween(bond, source, target);
        return edgeIdentifier;
    }

    @Override
    public Integer nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getNodes().size();
    }


}
