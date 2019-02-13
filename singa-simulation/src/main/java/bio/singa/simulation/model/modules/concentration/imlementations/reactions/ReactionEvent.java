package bio.singa.simulation.model.modules.concentration.imlementations.reactions;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.NodeBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.ReactantDelta;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.DeltaBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.VesicleBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
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
    private Vesicle currentVesicle;

    private DeltaBehavior updatableBehavior;

    public ReactionEvent(Reaction reaction, ReactantSet reactants) {
        this.reaction = reaction;
        this.reactants = reactants;
    }

    public List<ReactantDelta> collectDeltas(Updatable updatable) {
        List<ReactantDelta> deltas = new ArrayList<>();
        if (updatable instanceof Vesicle) {
            setCurrentVesicle((Vesicle) updatable);
            updatableBehavior = new VesicleBehavior(this);
            Map<AutomatonNode, Double> associatedNodes = currentVesicle.getAssociatedNodes();
            for (Map.Entry<AutomatonNode, Double> entry : associatedNodes.entrySet()) {
                setCurrentNode(entry.getKey());
                // assuming equal distribution of entities on the membrane surface,
                // the fraction of the associated surface is used to scale the velocity
                double velocity = reaction.getKineticLaw().determineVelocity(this) * entry.getValue();
                deltas.addAll(updatableBehavior.generateSubstrateDeltas(velocity));
                deltas.addAll(updatableBehavior.generateProductDeltas(velocity));
            }
        } else {
            setCurrentNode((AutomatonNode) updatable);
            updatableBehavior = new NodeBehavior(this);
            if (updatableBehavior.containsSubstrates(getCurrentNode().getConcentrationContainer())) {
                double velocity = reaction.getKineticLaw().determineVelocity(this);
                deltas.addAll(updatableBehavior.generateSubstrateDeltas(velocity));
                deltas.addAll(updatableBehavior.generateProductDeltas(velocity));
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

    public ConcentrationContainer getCurrentVesicleContainer() {
        if (reaction.getSupplier().isStrutCalculation()) {
            return reaction.getScope().getHalfStepConcentration(currentVesicle);
        } else {
            return currentVesicle.getConcentrationContainer();
        }
    }

    public AutomatonNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(AutomatonNode currentNode) {
        this.currentNode = currentNode;
    }

    public Vesicle getCurrentVesicle() {
        return currentVesicle;
    }

    public void setCurrentVesicle(Vesicle currentVesicle) {
        this.currentVesicle = currentVesicle;
    }

    public DeltaBehavior getUpdatableBehavior() {
        return updatableBehavior;
    }

    public ReactantSet getReactants() {
        return reactants;
    }
}
