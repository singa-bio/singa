package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ModificationSite;

import java.util.List;

import static bio.singa.simulation.model.rules.reactions.CompositionPredicate.*;

/**
 * @author cl
 */
public class ReactantCondition {

    private final CompositionPredicate compositionPredicate;
    private ChemicalEntity entity;
    private int number;

    public ReactantCondition(CompositionPredicate compositionPredicate) {
        this.compositionPredicate = compositionPredicate;
    }

    public CompositionPredicate getCompositionPredicate() {
        return compositionPredicate;
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean test(ChemicalEntity entity) {
        return compositionPredicate.getPredicate().test(this, entity);
    }

    public static boolean testAll(List<ReactantCondition> conditions, ChemicalEntity complexEntity) {
        for (ReactantCondition condition : conditions) {
            if (!condition.test(complexEntity)) {
                return false;
            }
        }
        return true;
    }


    public static ReactantCondition hasPart(ChemicalEntity entity) {
        ReactantCondition condition = new ReactantCondition(HAS_PART);
        condition.setEntity(entity);
        return condition;
    }

    public static ReactantCondition hasNumerOfPart(ChemicalEntity entity, int number) {
        ReactantCondition condition = new ReactantCondition(HAS_N_PART);
        condition.setEntity(entity);
        condition.setNumber(number);
        return condition;
    }

    public static ReactantCondition hasNotPart(ChemicalEntity entity) {
        ReactantCondition condition = new ReactantCondition(HAS_NOT_PART);
        condition.setEntity(entity);
        return condition;
    }

    public static ReactantCondition isUnoccupied(ModificationSite modificationSite) {
        ReactantCondition condition = new ReactantCondition(IS_UNOCCUPIED);
        condition.setEntity(modificationSite);
        return condition;
    }

    public static ReactantCondition solitaryBinding() {
        return new ReactantCondition(SOLITARY_BINDING);
    }

    public static ReactantCondition isSmallMolecule() {
        return new ReactantCondition(IS_SMALL_MOLECULE);
    }

    @Override
    public String toString() {
        switch (compositionPredicate) {
            case HAS_PART:
            case HAS_NOT_PART:
            case IS_UNOCCUPIED:
                return String.format(compositionPredicate.getDescriptor(), getEntity());
            case HAS_N_PART:
                return String.format(compositionPredicate.getDescriptor(), getEntity(), getNumber());
            case IS_SMALL_MOLECULE:
            case SOLITARY_BINDING:
                return compositionPredicate.getDescriptor();
            default:
                return "unkonwn condition";
        }
    }
}
