package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

import java.util.function.Predicate;

public class CandidateConditionBuilder {

    public static Predicate<ComplexEntity> hasAnyOfEntity(ChemicalEntity entity) {
        return new HasAnyNumberOfEntities(entity);
    }

    public static Predicate<ComplexEntity> hasOneOfEntity(ChemicalEntity entity) {
        return new HasNumberOfEntities(entity, 1);
    }

    public static Predicate<ComplexEntity> hasNoneOfEntity(ChemicalEntity entity) {
        return new HasAnyNumberOfEntities(entity).negate();
    }

    public static Predicate<ComplexEntity> hasNumberOfEntity(ChemicalEntity entity, int number) {
        return new HasNumberOfEntities(entity, number);
    }

    public static Predicate<ComplexEntity> hasNotNumberOfEntity(ChemicalEntity entity, int number) {
        return new HasNumberOfEntities(entity, number);
    }

    public static Predicate<ComplexEntity> hasOccupiedBindingSite(BindingSite bindingSite) {
        return new HasOccupiedBindingSite(bindingSite);
    }

    public static Predicate<ComplexEntity> isOnlyBoundAt(BindingSite bindingSite) {
        return new IsOnlyBoundAt(bindingSite);
    }

    public static Predicate<ComplexEntity> hasUnoccupiedBindingSite(BindingSite bindingSite) {
        return new HasUnoccupiedBindingSite(bindingSite);
    }

    public static Predicate<ComplexEntity> hasNoMoreThanNumberOfPartners(ChemicalEntity entity, int number) {
        return new HasNoMoreThanNumberOfPartners(entity, number);
    }


}
