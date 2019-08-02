package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ModificationSite;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author cl
 */
public class ReactantCondition {

    private ChemicalEntity site;
    private ChemicalEntity modification;
    private int number;

    private BiPredicate<ReactantCondition, ComplexEntity> condition;

    public static BiPredicate<ReactantCondition, ComplexEntity> HAS_PART = (condition, entity) -> Objects.nonNull(entity.find(condition.getSite()));

    public static BiPredicate<ReactantCondition, ComplexEntity> HAS_NOT_PART = (condition, entity) -> Objects.isNull(entity.find(condition.getSite()));

    public static  BiPredicate<ReactantCondition, ComplexEntity> SOLITARY_BINDING = (condition, entity) -> {
        for (ModificationSite site : entity.getSites()) {
            if (site.isOccupied()) {
                return false;
            }
        }
        return true;
    };

    public static  BiPredicate<ReactantCondition, ComplexEntity> COUNT_PART = (condition, entity) -> entity.countParts(condition.getModification()) == condition.number;

    public ReactantCondition(ChemicalEntity site, BiPredicate<ReactantCondition, ComplexEntity> condition) {
        this.site = site;
        this.condition = condition;
    }

    public ReactantCondition(ChemicalEntity site, ChemicalEntity modification, BiPredicate<ReactantCondition, ComplexEntity> condition) {
        this.site = site;
        this.modification = modification;
        this.condition = condition;
    }

    public ChemicalEntity getSite() {
        return site;
    }

    public void setSite(ChemicalEntity site) {
        this.site = site;
    }

    public BiPredicate<ReactantCondition, ComplexEntity> getCondition() {
        return condition;
    }

    public void setCondition(BiPredicate<ReactantCondition, ComplexEntity> condition) {
        this.condition = condition;
    }

    public ChemicalEntity getModification() {
        return modification;
    }

    public void setModification(ChemicalEntity modification) {
        this.modification = modification;
    }

    public boolean test(ComplexEntity entity) {
        return condition.test(this, entity);
    }

    public static List<ChemicalEntity> reduce(List<ReactantCondition> conditions, List<ChemicalEntity> entities) {
        if (conditions.isEmpty()) {
            return entities;
        }
        List<ChemicalEntity> remainingEntities = new ArrayList<>(entities);
        ListIterator<ChemicalEntity> iterator = remainingEntities.listIterator();

        while (iterator.hasNext()) {
            ChemicalEntity entity = iterator.next();
            if (entity instanceof ComplexEntity) {
                ComplexEntity complexEntity = (ComplexEntity) entity;
                if (!testAll(conditions, complexEntity)) {
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }

        return remainingEntities;
    }

    public static boolean testAll(List<ReactantCondition> conditions, ComplexEntity complexEntity) {
        for (ReactantCondition condition : conditions) {
            if (!condition.test(complexEntity)) {
                return false;
            }
        }
        return true;
    }


    public static ReactantCondition hasPart(ChemicalEntity entity) {
        return new ReactantCondition(entity, HAS_PART);
    }

    public static ReactantCondition hasNumerOfPart(ChemicalEntity entity, int number) {
        ReactantCondition condition = new ReactantCondition(null, COUNT_PART);
        condition.setModification(entity);
        condition.number = number;
        return condition;
    }

    public static ReactantCondition hasNotPart(ChemicalEntity entity) {
        return new ReactantCondition(entity, HAS_NOT_PART);
    }

    public static ReactantCondition solitaryBinding() {
        return new ReactantCondition(null, SOLITARY_BINDING);
    }


}
