package bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes;

import bio.singa.chemistry.entities.ChemicalEntity;

import java.util.List;

/**
 * @author cl
 */
public interface ReactantType {

    List<ReactantSet> generateReactantSets();

    List<ChemicalEntity> getReferencedEntities();

}
