package de.bioforscher.mathematics.graphs.util;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.vectors.VectorUtilities;

public class NodeFactory {

    public static RegularNode createRandomlyPlacedNode(int identifier, Rectangle rectangle) {
        return new RegularNode(identifier, VectorUtilities.generateRandomVectorInRectangle(rectangle));
    }

}
