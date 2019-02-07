package bio.singa.simulation.model.modules.concentration.newreaction.behaviors;

import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.newreaction.ReactionEvent;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.sections.CellSubsection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class NodeBehavior implements UpdatableBehavior {

    private ReactionEvent event;


    public NodeBehavior(ReactionEvent event) {
        this.event = event;
    }

    @Override
    public List<Reactant> getSubstrates() {
        return event.getReactants().getSubstrates();
    }

    @Override
    public List<Reactant> getProducts() {
        return event.getReactants().getProducts();
    }

    @Override
    public List<ReactantConcentration> collectReactants(List<Reactant> reactants) {
        List<ReactantConcentration> concentrations = new ArrayList<>();
        for (Reactant reactant : reactants) {
            concentrations.add(new ReactantConcentration(reactant, event.getCurrentNodeContainer().get(reactant.getPreferredTopology(), reactant.getEntity())));
        }
        return concentrations;
    }

    @Override
    public List<ReactantDelta> generateDeltas(List<Reactant> reactants, double velocity) {
        List<ReactantDelta> deltas = new ArrayList<>();
        for (Reactant reactant : reactants) {
            CellSubsection subsection = event.getCurrentNodeContainer().getSubsection(reactant.getPreferredTopology());
            deltas.add(new ReactantDelta(new ConcentrationDeltaIdentifier(event.getCurrentNode(), subsection, reactant.getEntity()), velocity * reactant.getStoichiometricNumber()));
        }
        return deltas;
    }


}
