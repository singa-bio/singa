package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.core.utility.Pair;

/**
 * Created by Christoph on 23/11/2016.
 */
public class GraphPlayground {

    public static void main(String[] args) {

        GenericGraph<Pair<Integer>> graph = new GenericGraph<>();

        GenericNode<Pair<Integer>> node1 = new GenericNode<>(0, new Pair<>(20,21));
        GenericNode<Pair<Integer>> node2 = new GenericNode<>(1, new Pair<>(10,11));

        graph.addNode(node1);
        graph.addNode(node2);

        graph.addEdgeBetween(node1, node2);

        graph.getNodes().forEach(System.out::println);
        System.out.println();
        graph.getEdges().forEach(System.out::println);

    }

}
