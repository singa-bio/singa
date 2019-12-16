package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas;

import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionEvent;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.sections.CellSubsection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class MembraneRestrictedBehavior implements DeltaBehavior {

    private ReactionEvent event;

    public MembraneRestrictedBehavior(ReactionEvent event) {
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
    public List<Reactant> getCatalysts() {
        return event.getReactants().getCatalysts();
    }

    @Override
    public List<ReactantConcentration> collectReactants(Collection<Reactant> reactants) {
        List<ReactantConcentration> concentrations = new ArrayList<>();
        for (Reactant reactant : reactants) {
            if (reactant.getPreferredTopology().equals(MEMBRANE)) {
                concentrations.add(new ReactantConcentration(reactant, event.getMembraneRestrictedContainer().get(MEMBRANE, reactant.getEntity())));
            } else {
                concentrations.add(new ReactantConcentration(reactant, event.getCurrentNodeContainer().get(reactant.getPreferredTopology(), reactant.getEntity())));
            }
        }
        return concentrations;
    }

    @Override
    public List<ReactantDelta> generateDeltas(List<Reactant> reactants, double velocity) {
        List<ReactantDelta> deltas = new ArrayList<>();
        for (Reactant reactant : reactants) {
            CellSubsection subsection;
            if (reactant.getPreferredTopology().equals(MEMBRANE)) {
                subsection = event.getMembraneRestrictedContainer().getMembraneSubsection();
                deltas.add(new ReactantDelta(new ConcentrationDeltaIdentifier(event.getCurrentMembraneRestrictedUpdatable(), subsection, reactant.getEntity()), velocity * reactant.getStoichiometricNumber()));
            } else {
                subsection = event.getCurrentNodeContainer().getSubsection(reactant.getPreferredTopology());
                deltas.add(new ReactantDelta(new ConcentrationDeltaIdentifier(event.getCurrentNode(), subsection, reactant.getEntity()), velocity * reactant.getStoichiometricNumber()));
            }
        }
        return deltas;
    }



}
