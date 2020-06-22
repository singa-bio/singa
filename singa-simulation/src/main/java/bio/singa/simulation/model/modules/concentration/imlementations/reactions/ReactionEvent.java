package bio.singa.simulation.model.modules.concentration.imlementations.reactions;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.DeltaBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.NodeBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.ReactantDelta;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.MembraneRestrictedBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import bio.singa.simulation.model.modules.qualitative.implementations.EndocytoticPit;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class ReactionEvent {

    private Reaction reaction;
    private ReactantSet reactants;

    private AutomatonNode currentNode;
    private Updatable currentMembraneRestrictedUpdatable;

    private DeltaBehavior updatableBehavior;

    public ReactionEvent(Reaction reaction, ReactantSet reactants) {
        this.reaction = reaction;
        this.reactants = reactants;
    }

    public List<ReactantDelta> collectDeltas(Updatable updatable) {
        List<ReactantDelta> deltas = new ArrayList<>();
        if (updatable instanceof Vesicle) {
            Vesicle vesicle = (Vesicle) updatable;
            setCurrentMembraneRestrictedUpdatable(vesicle);
            updatableBehavior = new MembraneRestrictedBehavior(this);
            Map<AutomatonNode, Double> associatedNodes = vesicle.getAssociatedNodes();
            for (Map.Entry<AutomatonNode, Double> entry : associatedNodes.entrySet()) {
                setCurrentNode(entry.getKey());
                // assuming equal distribution of entities on the membrane surface,
                // the fraction of the associated surface is used to scale the velocity
                double velocity = reaction.getKineticLaw().determineVelocity(this) * entry.getValue();
                deltas.addAll(updatableBehavior.generateSubstrateDeltas(velocity));
                deltas.addAll(updatableBehavior.generateProductDeltas(velocity));
            }
        } else if (updatable instanceof AutomatonNode) {
            setCurrentNode((AutomatonNode) updatable);
            updatableBehavior = new NodeBehavior(this);
            if (updatableBehavior.containsSubstrates(getCurrentNode().getConcentrationContainer())) {
                double velocity = reaction.getKineticLaw().determineVelocity(this);
                if (velocity != 0.0) {
                    deltas.addAll(updatableBehavior.generateSubstrateDeltas(velocity));
                    deltas.addAll(updatableBehavior.generateProductDeltas(velocity));
                }
            }
        } else {
            EndocytoticPit pit = (EndocytoticPit) updatable;
            setCurrentMembraneRestrictedUpdatable(pit);
            setCurrentNode(pit.getAssociatedNode());
            updatableBehavior = new MembraneRestrictedBehavior(this);
            if (updatableBehavior.containsSubstrates(getCurrentNode().getConcentrationContainer())) {
                double velocity = reaction.getKineticLaw().determineVelocity(this);
                if (velocity != 0.0) {
                    deltas.addAll(updatableBehavior.generateSubstrateDeltas(velocity));
                    deltas.addAll(updatableBehavior.generateProductDeltas(velocity));
                }
            }
        }
        return deltas;
    }

    public ConcentrationContainer getCurrentNodeContainer() {
        if (reaction.getSupplier().isStrutCalculation()) {
            return reaction.getScope().getHalfStepConcentration(currentNode);
        } else {
            return currentNode.getConcentrationContainer();
        }
    }

    public ConcentrationContainer getMembraneRestrictedContainer() {
        if (reaction.getSupplier().isStrutCalculation()) {
            return reaction.getScope().getHalfStepConcentration(currentMembraneRestrictedUpdatable);
        } else {
            return currentMembraneRestrictedUpdatable.getConcentrationContainer();
        }
    }

    public AutomatonNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(AutomatonNode currentNode) {
        this.currentNode = currentNode;
    }

    public Updatable getCurrentMembraneRestrictedUpdatable() {
        return currentMembraneRestrictedUpdatable;
    }

    public void setCurrentMembraneRestrictedUpdatable(Updatable currentMembraneRestrictedUpdatable) {
        this.currentMembraneRestrictedUpdatable = currentMembraneRestrictedUpdatable;
    }

    public DeltaBehavior getUpdatableBehavior() {
        return updatableBehavior;
    }

    public ReactantSet getReactants() {
        return reactants;
    }

}
