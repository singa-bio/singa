package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;

import java.util.List;

/**
 * @author cl
 */
public interface ReactantBehavior {

    List<ReactantSet> getReactantSets();

    List<ChemicalEntity> getReferencedEntities();

    void addReactant(Reactant reactant);

    List<Reactant> getSubstrates();

    List<Reactant> getProducts();

    List<Reactant> getCatalysts();

}
