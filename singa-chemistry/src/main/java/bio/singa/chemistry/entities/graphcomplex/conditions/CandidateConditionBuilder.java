package bio.singa.chemistry.entities.graphcomplex.conditions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

import java.util.function.Predicate;

public class CandidateConditionBuilder {

    public static Predicate<GraphComplex> hasAnyOfEntity(ChemicalEntity entity) {
        return new HasAnyNumberOfEntities(entity);
    }

    public static Predicate<GraphComplex> hasNoneOfEntity(ChemicalEntity entity) {
        return new HasAnyNumberOfEntities(entity).negate();
    }

    public static Predicate<GraphComplex> hasNumberOfEntity(ChemicalEntity entity, int number) {
        return new HasNumberOfEntities(entity, number);
    }

    public static Predicate<GraphComplex> hasNotNumberOfEntity(ChemicalEntity entity, int number) {
        return new HasNumberOfEntities(entity, number);
    }

    public static Predicate<GraphComplex> hasOccupiedBindingSite(BindingSite bindingSite) {
        return new HasOccupiedBindingSite(bindingSite);
    }

    public static Predicate<GraphComplex> hasUnoccupiedBindingSite(BindingSite bindingSite) {
        return new HasUnoccupiedBindingSite(bindingSite);
    }

    public static Predicate<GraphComplex> hasNoMoreThanNumberOfPartners(ChemicalEntity entity, int number) {
        return new HasNoMoreThanNumberOfPartners(entity, number);
    }


}
