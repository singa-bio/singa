package bio.singa.mathematics.graphs.model;

import bio.singa.mathematics.vectors.Vector;

/**
 * @author cl
 */
public class GraphPredicates {

    public static boolean isLeafNode(Node node) {
        return node.getDegree() == 1;
    }

    public static boolean haveSameIdentifiers(Node first, Node second) {
        return first.getIdentifier().equals(second.getIdentifier());
    }

    public static boolean haveSamePosition(Node first, Node second) {
        return first.getPosition().equals(second.getPosition());
    }

    public static boolean nodeHasPosition(Node node, Vector position) {
        return node.getPosition().equals(position);
    }

}
