package de.bioforscher.singa.mathematics.graphs.model;

import java.util.LinkedList;
import java.util.Vector;

/**
 * @author cl
 */
public class GraphPath<NodeType extends Node<NodeType, ? extends Vector, ?>, EdgeType extends Edge<NodeType>>  {

    private LinkedList<NodeType> nodes;
    private LinkedList<EdgeType> edges;

    public GraphPath() {
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
    }



}
