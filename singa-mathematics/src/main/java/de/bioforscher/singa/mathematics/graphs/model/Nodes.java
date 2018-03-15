package de.bioforscher.singa.mathematics.graphs.model;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vectors;

public class Nodes {

    public static RegularNode createRandomlyPlacedNode(int identifier) {
        return createRandomlyPlacedNode(identifier, Graphs.DEFAULT_BOUNDING_BOX);
    }

    public static RegularNode createRandomlyPlacedNode(int identifier, Rectangle rectangle) {
        return new RegularNode(identifier, Vectors.generateRandom2DVector(rectangle));
    }

}
