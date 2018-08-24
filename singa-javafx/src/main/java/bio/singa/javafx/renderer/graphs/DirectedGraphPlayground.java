package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import bio.singa.mathematics.graphs.model.DirectedEdge;
import bio.singa.mathematics.graphs.model.DirectedGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;

import java.util.function.BiFunction;

/**
 * @author cl
 */
public class DirectedGraphPlayground {

    public static void main(String[] args)  {

        DirectedGraph<GenericNode<String>> pattern = new DirectedGraph<>();

        GenericNode<String> a = new GenericNode<>(pattern.nextNodeIdentifier(), "A");
        pattern.addNode(a);
        GenericNode<String> b = new GenericNode<>(pattern.nextNodeIdentifier(), "B");
        pattern.addNode(b);
        GenericNode<String> c = new GenericNode<>(pattern.nextNodeIdentifier(), "C");
        pattern.addNode(c);

        pattern.addEdgeBetween(a,b);
        pattern.addEdgeBetween(c,b);

        DirectedGraph<GenericNode<String>> target = new DirectedGraph<>();

        GenericNode<String> ta = new GenericNode<>(target.nextNodeIdentifier(), "A");
        target.addNode(ta);
        GenericNode<String> tb = new GenericNode<>(target.nextNodeIdentifier(), "B");
        target.addNode(tb);
        GenericNode<String> tc = new GenericNode<>(target.nextNodeIdentifier(), "C");
        target.addNode(tc);
        GenericNode<String> td = new GenericNode<>(target.nextNodeIdentifier(), "D");
        target.addNode(td);

        target.addEdgeBetween(ta, tb);
        target.addEdgeBetween(tc, tb);
        target.addEdgeBetween(td, tc);
        target.addEdgeBetween(tb, td);


        BiFunction<GenericNode<String>, GenericNode<String>, Boolean> nodeConditionExtractor =
                (first, second) -> first.getContent().equals(second.getContent());

        BiFunction<DirectedEdge<GenericNode<String>>, DirectedEdge<GenericNode<String>>, Boolean> edgeConditionExtractor = (first,second) -> {
            // (first, second) -> nodeConditionExtractor.apply(first.getSource(), second.getSource()) && nodeConditionExtractor.apply(first.getTarget(), second.getTarget());
            return true;
        };

        RISubgraphFinder<GenericNode<String>, DirectedEdge<GenericNode<String>>, Vector2D, Integer, DirectedGraph<GenericNode<String>>> finder;
        finder = new RISubgraphFinder<>(pattern, target, nodeConditionExtractor, edgeConditionExtractor);

        GraphDisplayApplication.graph = pattern;

        GraphRenderer renderer = GraphDisplayApplication.getRenderer();
        renderer.getRenderingOptions().setDisplayText(true);
        renderer.getRenderingOptions().setTextExtractor(node -> ((GenericNode<String>) node).getContent());

        Application.launch(GraphDisplayApplication.class);

    }
}
