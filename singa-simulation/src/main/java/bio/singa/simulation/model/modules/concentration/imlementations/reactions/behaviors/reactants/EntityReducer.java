package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;

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

    public static EntityCompositionCondition hasPart(ChemicalEntity reducer) {
        return new EntityCompositionCondition("HAS_PART", reducer, EntityReducer::hasPartFunction);
    }

    public static EntityCompositionCondition hasOnce(ChemicalEntity reducer) {
        return new EntityCompositionCondition("HAS_PART_ONCE", reducer, EntityReducer::hasOnceFunction);
    }

    public static EntityCompositionCondition hasTwice(ChemicalEntity reducer) {
        return new EntityCompositionCondition("HAS_PART_TWICE", reducer, EntityReducer::hasTwiceFunction);
    }

    private static List<ChemicalEntity> hasPartFunction(Collection<ChemicalEntity> entities, ChemicalEntity reducer) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>();
        for (ChemicalEntity entity : entities) {
            if (entity.equals(reducer)) {
                reducedEntities.add(entity);
            } else if (entity instanceof ComplexEntity) {
                if (((ComplexEntity) entity).find(reducer) != null) {
                    reducedEntities.add(entity);
                }
            }
        }
        return reducedEntities;
    }

    private static List<ChemicalEntity> hasTwiceFunction(Collection<ChemicalEntity> entities, ChemicalEntity reducer) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>();
        for (ChemicalEntity entity : entities) {
            if (entity.equals(reducer)) {
                reducedEntities.add(entity);
            } else if (entity instanceof ComplexEntity) {
                if (containsTwice(((ComplexEntity) entity).getAllData(), reducer, 2)) {
                    reducedEntities.add(entity);
                }
            }
        }
        return reducedEntities;
    }

    private static List<ChemicalEntity> hasOnceFunction(Collection<ChemicalEntity> entities, ChemicalEntity reducer) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>();
        for (ChemicalEntity entity : entities) {
            if (entity.equals(reducer)) {
                reducedEntities.add(entity);
            } else if (entity instanceof ComplexEntity) {
                if (containsTwice(((ComplexEntity) entity).getAllData(), reducer, 1)) {
                    reducedEntities.add(entity);
                }
            }
        }
        return reducedEntities;
    }

    private static boolean containsTwice(Collection<ChemicalEntity> entities, ChemicalEntity searched, int count) {
        int i = 0;
        for (ChemicalEntity current : entities) {
            if (current.equals(searched)) {
                i++;
            }
        }
        return i == count;
    }

    public static EntityCompositionCondition hasNotPart(ChemicalEntity reducer) {
        return new EntityCompositionCondition("HAS_NOT_PART", reducer, EntityReducer::hasNotPartFunction);
    }

    private static List<ChemicalEntity> hasNotPartFunction(Collection<ChemicalEntity> entities, ChemicalEntity reducer) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>();
        for (ChemicalEntity entity : entities) {
            if (entity.equals(reducer)) {
                continue;
            } else if (entity instanceof ComplexEntity) {
                if (((ComplexEntity) entity).find(reducer) != null) {
                    continue;
                }
            }
            reducedEntities.add(entity);
        }
        return reducedEntities;
    }

    public static List<ChemicalEntity> apply(Collection<ChemicalEntity> entities, EntityCompositionCondition... conditions) {
        return apply(entities, Arrays.asList(conditions));
    }

    public static List<ChemicalEntity> apply(Collection<ChemicalEntity> entities, Collection<EntityCompositionCondition> conditions) {
        List<ChemicalEntity> reducedEntities = new ArrayList<>(entities);
        for (EntityCompositionCondition condition : conditions) {
            reducedEntities = condition.reduce(reducedEntities, condition.getEntity());
        }
        return reducedEntities;
    }

}
