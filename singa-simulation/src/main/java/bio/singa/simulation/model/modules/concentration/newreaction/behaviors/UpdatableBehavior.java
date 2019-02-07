package bio.singa.simulation.model.modules.concentration.newreaction.behaviors;

import bio.singa.simulation.model.modules.concentration.reactants.Reactant;

import java.util.List;

/**
 * @author cl
 */
public interface UpdatableBehavior {

    List<Reactant> getSubstrates();

    List<Reactant> getProducts();

    List<ReactantConcentration> collectReactants(List<Reactant> reactants);

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



}
