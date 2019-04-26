package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.List;

/**
 * @author cl
 */
public interface ReactantBehavior {

    List<ReactantSet> generateReactantSets(Updatable updatable);

    List<ChemicalEntity> getReferencedEntities();

    void addReactant(Reactant reactant);

    List<Reactant> getSubstrates();

    List<Reactant> getProducts();

    List<Reactant> getCatalysts();

}
