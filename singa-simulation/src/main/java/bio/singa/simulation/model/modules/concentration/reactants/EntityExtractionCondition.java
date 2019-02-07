package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author cl
 */
public class EntityExtractionCondition {

    private String identifier;

    private ChemicalEntity entity;
    private BiFunction<Collection<ChemicalEntity>, ChemicalEntity, List<ChemicalEntity>> function;

    public EntityExtractionCondition(String identifier, ChemicalEntity entity, BiFunction<Collection<ChemicalEntity>, ChemicalEntity, List<ChemicalEntity>> function) {
        this.identifier = identifier;
        this.entity = entity;
        this.function = function;
    }

    public List<ChemicalEntity> reduce(Collection<ChemicalEntity> entities, ChemicalEntity entity) {
        return function.apply(entities, entity);
    }

    public String getIdentifier() {
        return identifier;
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public BiFunction<Collection<ChemicalEntity>, ChemicalEntity, List<ChemicalEntity>> getFunction() {
        return function;
    }

}
