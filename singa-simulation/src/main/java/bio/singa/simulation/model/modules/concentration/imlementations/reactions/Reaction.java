package bio.singa.simulation.model.modules.concentration.imlementations.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.ReactantDelta;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.KineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantSet;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class Reaction extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    private KineticLaw kineticLaw;
    private ReactantBehavior reactantBehavior;

    void postConstruct() {
        reactantBehavior.getReferencedEntities().forEach(this::addReferencedEntity);
        // TODO apply meaningful condition
        if (hasMembraneAssociatedEntities()) {
            setApplicationCondition(this::hasMembrane);
        } else {
            setApplicationCondition(updatable -> true);
        }
        // TODO apply meaningful condition
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // reference entities for this module
    }

    private boolean hasMembraneAssociatedEntities() {
        // if any entity is associated to the membrane
        for (ChemicalEntity referencedEntity : reactantBehavior.getReferencedEntities()) {
            if (referencedEntity.isMembraneBound()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMembrane(Updatable updatable) {
        return updatable.getCellRegion().hasMembrane();
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        List<ReactantSet> reactantSets = reactantBehavior.generateReactantSets(supplier.getCurrentUpdatable());
        for (ReactantSet reactantSet : reactantSets) {
            ReactionEvent reactionEvent = new ReactionEvent(this, reactantSet);
            List<ReactantDelta> reactantDeltas = reactionEvent.collectDeltas(supplier.getCurrentUpdatable());
            for (ReactantDelta reactantDelta : reactantDeltas) {
                addDelta(deltas, reactantDelta.getIdentifier(), reactantDelta.getDelta());
            }
        }
        return deltas;
    }

    private void addDelta(Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas, ConcentrationDeltaIdentifier identifier, double concentrationDelta) {
        if (deltas.containsKey(identifier)) {
            deltas.put(identifier, deltas.get(identifier).add(concentrationDelta));
        } else {
            deltas.put(identifier, new ConcentrationDelta(this, identifier.getSubsection(), identifier.getEntity(), concentrationDelta));
        }
    }

    public KineticLaw getKineticLaw() {
        return kineticLaw;
    }

    public void setKineticLaw(KineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    public ReactantBehavior getReactantBehavior() {
        return reactantBehavior;
    }

    public void setReactantBehavior(ReactantBehavior reactantBehavior) {
        this.reactantBehavior = reactantBehavior;
    }
}
