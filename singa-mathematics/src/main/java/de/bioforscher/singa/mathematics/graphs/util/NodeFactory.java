package de.bioforscher.singa.mathematics.graphs.util;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.vectors.Vectors;

public class NodeFactory {

    public static RegularNode createRandomlyPlacedNode(int identifier, Rectangle rectangle) {
        return new RegularNode(identifier, Vectors.generateRandom2DVector(rectangle));
    }

}
