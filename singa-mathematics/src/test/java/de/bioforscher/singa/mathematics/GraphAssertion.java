package de.bioforscher.singa.mathematics;

import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector;
import org.junit.Assert;

import java.util.Collection;

/**
 * @author cl
 */
public class GraphAssertion {

    public static <NodeType extends Node<NodeType, VectorType, IdentifierType>,
            EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> void assertGraphContainsNodes(GraphType graph, IdentifierType... expectedIdentifiers) {

        Collection<NodeType> nodes = graph.getNodes();
        for (IdentifierType identifier : expectedIdentifiers) {
            boolean contained = false;
            for (NodeType node : nodes) {
                if (node.getIdentifier().equals(identifier)) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                Assert.fail("The graph was expected to contain a node with the identifier <" + identifier + ">, but no node with this identifier could be found.");
            }
        }

    }

}
