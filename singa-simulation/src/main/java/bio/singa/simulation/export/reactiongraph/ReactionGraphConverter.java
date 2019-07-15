package bio.singa.simulation.export.reactiongraph;

import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.Simulation;

/**
 * @author cl
 */
public class ReactionGraphConverter {

    private ReactionGraph graph;

    public static ReactionGraph convert(Simulation simulation) {
        ReactionGraphConverter converter = new ReactionGraphConverter(simulation);
        return converter.graph;
    }

    private ReactionGraphConverter(Simulation simulation) {
        graph = new ReactionGraph();
        simulation.getModules().forEach(this::convertModule);
    }

    private void convertModule(UpdateModule module) {

    }

//    private void addContentOfModule(Reaction reaction) {
//        ArrayList<ReactionGraphNode> productNodes = new ArrayList<>();
//        for (Reactant product : reaction.getProducts()) {
//            ReactionGraphNode productNode = graph.addNode(product.getEntity());
//            productNodes.add(productNode);
//        }
//        ArrayList<ReactionGraphNode> substrateNodes = new ArrayList<>();
//        for (Reactant substrate : reaction.getSubstrates()) {
//            ReactionGraphNode substrateNode = graph.addNode(substrate.getEntity());
//            substrateNodes.add(substrateNode);
//        }
//        for (ReactionGraphNode substrateNode : substrateNodes) {
//            for (ReactionGraphNode productNode : productNodes) {
//                if (substrateNode != productNode) {
//                    graph.addEdgeBetween(substrateNode, productNode);
//                }
//            }
//        }
//    }

//    private void addContentOfModule(SectionDependentReaction reaction) {
//        ArrayList<ReactionGraphNode> productNodes = new ArrayList<>();
//        for (Reactant product : reaction.getProducts()) {
//            ReactionGraphNode productNode = graph.addNode(product.getEntity());
//            productNodes.add(productNode);
//        }
//        ArrayList<ReactionGraphNode> substrateNodes = new ArrayList<>();
//        for (Reactant substrate : reaction.getSubstrates()) {
//            ReactionGraphNode substrateNode = graph.addNode(substrate.getEntity());
//            substrateNodes.add(substrateNode);
//        }
//        for (ReactionGraphNode substrateNode : substrateNodes) {
//            for (ReactionGraphNode productNode : productNodes) {
//                if (substrateNode != productNode) {
//                    graph.addEdgeBetween(substrateNode, productNode);
//                }
//            }
//        }
//    }

//    private void addContentOfModule(DynamicReaction reaction) {
//        ArrayList<ReactionGraphNode> productNodes = new ArrayList<>();
//        for (Reactant product : reaction.getProducts()) {
//            ReactionGraphNode productNode = graph.addNode(product.getEntity());
//            productNodes.add(productNode);
//        }
//        ArrayList<ReactionGraphNode> substrateNodes = new ArrayList<>();
//        for (Reactant substrate : reaction.getSubstrates()) {
//            ReactionGraphNode substrateNode = graph.addNode(substrate.getEntity());
//            substrateNodes.add(substrateNode);
//        }
//        for (ReactionGraphNode substrateNode : substrateNodes) {
//            for (ReactionGraphNode productNode : productNodes) {
//                if (substrateNode != productNode) {
//                    graph.addEdgeBetween(substrateNode, productNode);
//                }
//            }
//        }
//    }

}
