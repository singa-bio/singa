package bio.singa.simulation.model.modules.concentration.newreaction;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.newreaction.behaviors.NodeBehavior;
import bio.singa.simulation.model.modules.concentration.newreaction.behaviors.ReactantDelta;
import bio.singa.simulation.model.modules.concentration.newreaction.behaviors.UpdatableBehavior;
import bio.singa.simulation.model.modules.concentration.newreaction.behaviors.VesicleBehavior;
import bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes.ReactantSet;
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

    private UpdatableBehavior updatableBehavior;

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
            double velocity = reaction.getKineticLaw().determineVelocity(this);
            deltas.addAll(updatableBehavior.generateSubstrateDeltas(velocity));
            deltas.addAll(updatableBehavior.generateProductDeltas(velocity));
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

    public UpdatableBehavior getUpdatableBehavior() {
        return updatableBehavior;
    }

    public ReactantSet getReactants() {
        return reactants;
    }
}
