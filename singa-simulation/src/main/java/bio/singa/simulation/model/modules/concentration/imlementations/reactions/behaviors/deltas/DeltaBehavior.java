package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public interface DeltaBehavior {

    List<Reactant> getSubstrates();

    List<Reactant> getProducts();

    List<Reactant> getCatalysts();

    List<ReactantConcentration> collectReactants(Collection<Reactant> reactants);

    default List<ReactantConcentration> collectCatalysts() {
        return collectReactants(getCatalysts());
    }

    default List<ReactantConcentration> collectSubstrates() {
        return collectReactants(getSubstrates());
    }

    default List<ReactantConcentration> collectProducts() {
        return collectReactants(getProducts());
    }

    List<ReactantDelta> generateDeltas(List<Reactant> reactants, double velocity);

    default List<ReactantDelta> generateSubstrateDeltas(double velocity) {
        return generateDeltas(getSubstrates(), -velocity);
    }

    default List<ReactantDelta> generateProductDeltas(double velocity) {
        return generateDeltas(getProducts(), velocity);
    }

    default boolean containsSubstrates(ConcentrationContainer concentrationContainer) {
        List<Reactant> substrates = getSubstrates();
        for (Reactant substrate : substrates) {
            if (concentrationContainer.get(substrate.getPreferredTopology(), substrate.getEntity()) == 0.0) {
                return false;
            }
        }
        return true;
    }


}
