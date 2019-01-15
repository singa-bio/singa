package bio.singa.simulation.renderer.reactiongraph;

import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.DynamicReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.SectionDependentReaction;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.ArrayList;

/**
 * @author cl
 */
public class ReactionGraphConverter {

    private Simulation simulation;

    private ReactionGraph graph;


    public static ReactionGraph convert(Simulation simulation) {
        ReactionGraphConverter converter = new ReactionGraphConverter(simulation);
        return converter.graph;
    }

    private ReactionGraphConverter(Simulation simulation) {
        this.simulation = simulation;
        graph = new ReactionGraph();
        simulation.getModules().forEach(this::convertModule);
    }

    private void convertModule(UpdateModule module) {
        if (module instanceof Reaction) {
            addContentOfModule(((Reaction) module));
        } else if (module instanceof SectionDependentReaction) {
            addContentOfModule(((SectionDependentReaction) module));
        } else if (module instanceof ComplexBuildingReaction) {
            addContentOfModule(((ComplexBuildingReaction) module));
        } else if (module instanceof DynamicReaction) {
            addContentOfModule(((DynamicReaction) module));
        }
    }

    private void addContentOfModule(Reaction reaction) {
        ArrayList<ReactionGraphNode> productNodes = new ArrayList<>();
        for (Reactant product : reaction.getProducts()) {
            ReactionGraphNode productNode = graph.addNode(product.getEntity());
            productNodes.add(productNode);
        }
        ArrayList<ReactionGraphNode> substrateNodes = new ArrayList<>();
        for (Reactant substrate : reaction.getSubstrates()) {
            ReactionGraphNode substrateNode = graph.addNode(substrate.getEntity());
            substrateNodes.add(substrateNode);
        }
        for (ReactionGraphNode substrateNode : substrateNodes) {
            for (ReactionGraphNode productNode : productNodes) {
                if (substrateNode != productNode) {
                    graph.addEdgeBetween(substrateNode, productNode);
                }
            }
        }
    }

    private void addContentOfModule(SectionDependentReaction reaction) {
        ArrayList<ReactionGraphNode> productNodes = new ArrayList<>();
        for (Reactant product : reaction.getProducts()) {
            ReactionGraphNode productNode = graph.addNode(product.getEntity());
            productNodes.add(productNode);
        }
        ArrayList<ReactionGraphNode> substrateNodes = new ArrayList<>();
        for (Reactant substrate : reaction.getSubstrates()) {
            ReactionGraphNode substrateNode = graph.addNode(substrate.getEntity());
            substrateNodes.add(substrateNode);
        }
        for (ReactionGraphNode substrateNode : substrateNodes) {
            for (ReactionGraphNode productNode : productNodes) {
                if (substrateNode != productNode) {
                    graph.addEdgeBetween(substrateNode, productNode);
                }
            }
        }
    }

    private void addContentOfModule(ComplexBuildingReaction reaction) {
        ReactionGraphNode binderNode = graph.addNode(reaction.getBinder());
        ReactionGraphNode bindeeNode = graph.addNode(reaction.getBindee());
        ReactionGraphNode complexNode = graph.addNode(reaction.getComplex());
        graph.addEdgeBetween(bindeeNode, complexNode);
        graph.addEdgeBetween(binderNode, complexNode);
    }

    private void addContentOfModule(DynamicReaction reaction) {
        ArrayList<ReactionGraphNode> productNodes = new ArrayList<>();
        for (Reactant product : reaction.getProducts()) {
            ReactionGraphNode productNode = graph.addNode(product.getEntity());
            productNodes.add(productNode);
        }
        ArrayList<ReactionGraphNode> substrateNodes = new ArrayList<>();
        for (Reactant substrate : reaction.getSubstrates()) {
            ReactionGraphNode substrateNode = graph.addNode(substrate.getEntity());
            substrateNodes.add(substrateNode);
        }
        for (ReactionGraphNode substrateNode : substrateNodes) {
            for (ReactionGraphNode productNode : productNodes) {
                if (substrateNode != productNode) {
                    graph.addEdgeBetween(substrateNode, productNode);
                }
            }
        }
    }

}
