package de.bioforscher.chemistry.descriptive.molecules;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.Vectors;

/**
 * @author cl
 */
public class MoleculeGraph extends AbstractGraph<MoleculeAtom, MoleculeBond, Vector3D> {

    public int addNextAtom(String elementSymbol) {
        return addNextAtom(ElementProvider.getElementBySymbol(elementSymbol).orElse(ElementProvider.UNKOWN));
    }

    public int addNextAtom(Element element) {
        return addNode(new MoleculeAtom(nextNodeIdentifier(), Vectors.generateRandomVector3D(), element));
    }

    @Override
    public int addEdgeBetween(int identifier, MoleculeAtom source, MoleculeAtom target) {
        return addEdgeBetween(new MoleculeBond(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(MoleculeAtom source, MoleculeAtom target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }
}
