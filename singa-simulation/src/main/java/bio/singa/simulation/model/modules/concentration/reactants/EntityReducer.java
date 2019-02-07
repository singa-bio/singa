package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class EntityReducer {

    private EntityReducer() {
    }

    public static EntityExtractionCondition hasPart(ChemicalEntity reducer) {
        return new EntityExtractionCondition("HAS_NOT_PART", reducer, EntityReducer::hasPartFunction);
    }

    private static List<ChemicalEntity> hasPartFunction(Collection<ChemicalEntity> entities, ChemicalEntity reducer) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>();
        for (ChemicalEntity entity : entities) {
            if (entity.equals(reducer)) {
                reducedEntities.add(entity);
            } else if (entity instanceof ComplexedChemicalEntity) {
                if (((ComplexedChemicalEntity) entity).getAllAssociatedChemicalEntities().contains(reducer)) {
                    reducedEntities.add(entity);
                }
            }
        }
        return reducedEntities;
    }

    public static EntityExtractionCondition hasNotPart(ChemicalEntity reducer) {
        return new EntityExtractionCondition("HAS_NOT_PART", reducer, EntityReducer::hasNotPartFunction);
    }

    private static List<ChemicalEntity> hasNotPartFunction(Collection<ChemicalEntity> entities, ChemicalEntity reducer) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>();
        for (ChemicalEntity entity : entities) {
            if (entity.equals(reducer)) {
                continue;
            } else if (entity instanceof ComplexedChemicalEntity) {
                if (((ComplexedChemicalEntity) entity).getAllAssociatedChemicalEntities().contains(reducer)) {
                    continue;
                }
            }
            reducedEntities.add(entity);
        }
        return reducedEntities;
    }

    public static List<ChemicalEntity> apply(Collection<ChemicalEntity> entities, EntityExtractionCondition... conditions) {
        return apply(entities, Arrays.asList(conditions));
    }

    public static List<ChemicalEntity> apply(Collection<ChemicalEntity> entities, Collection<EntityExtractionCondition> conditions) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>(entities);
        for (EntityExtractionCondition condition : conditions) {
            reducedEntities = condition.reduce(reducedEntities, condition.getEntity());
        }
        return reducedEntities;
    }

}
