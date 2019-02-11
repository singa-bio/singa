package bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.List;

/**
 * @author cl
 */
public interface ReactantType {

    List<ReactantSet> generateReactantSets(Updatable updatable);

    List<ChemicalEntity> getReferencedEntities();

}
