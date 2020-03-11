package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vectors2D;

public class Nodes {

    public static RegularNode createRandomlyPlacedNode(int identifier) {
        return createRandomlyPlacedNode(identifier, Graphs.DEFAULT_BOUNDING_BOX);
    }

    public static RegularNode createRandomlyPlacedNode(int identifier, Rectangle rectangle) {
        return new RegularNode(identifier, Vectors2D.generateRandom2DVector(rectangle));
    }

}
