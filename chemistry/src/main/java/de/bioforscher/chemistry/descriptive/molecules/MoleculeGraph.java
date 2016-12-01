package de.bioforscher.chemistry.descriptive.molecules;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;

/**
 * Created by Christoph on 21/11/2016.
 */
public class MoleculeGraph extends AbstractGraph<MoleculeAtom, MoleculeBond, Vector3D> {

    private int atomCounter;

    public int getNextNodeIdentifier() {
        return this.atomCounter++;
    }

    public int addNextAtom(char elementSymbol) {
        return addNextAtom(String.valueOf(elementSymbol));
    }

    public int addNextAtom(String elementSymbol) {
        return addNextAtom(ElementProvider.getElementBySymbol(elementSymbol).orElse(ElementProvider.UNKOWN));
    }

    public int addNextAtom(Element element) {
        MoleculeAtom atom = new MoleculeAtom(getNextNodeIdentifier(), VectorUtilities.generateRandomVector3D(), element);
        addNode(atom);
        return atom.getIdentifier();
    }


    @Override
    public void addEdgeBetween(MoleculeAtom source, MoleculeAtom target) {

    }
}
