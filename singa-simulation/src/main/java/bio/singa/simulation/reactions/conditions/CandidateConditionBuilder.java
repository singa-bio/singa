package bio.singa.simulation.reactions.conditions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;

public class CandidateConditionBuilder {

    public static CandidateCondition hasAnyOfEntity(ChemicalEntity entity) {
        return new HasAnyNumberOfEntities(entity);
    }

    public static CandidateCondition hasOneOfEntity(ChemicalEntity entity) {
        return new HasNumberOfEntities(entity, 1);
    }

    public static CandidateCondition hasNoneOfEntity(ChemicalEntity entity) {
        return new HasNumberOfEntities(entity, 0);
    }

    public static CandidateCondition hasNumberOfEntity(ChemicalEntity entity, int number) {
        return new HasNumberOfEntities(entity, number);
    }

    public static CandidateCondition hasNotNumberOfEntity(ChemicalEntity entity, int number) {
        return new HasNumberOfEntities(entity, number);
    }

    public static CandidateCondition hasOccupiedBindingSite(BindingSite bindingSite) {
        return new HasOccupiedBindingSite(bindingSite);
    }

    public static CandidateCondition isOnlyBoundAt(BindingSite bindingSite) {
        return new IsOnlyBoundAt(bindingSite);
    }

    public static CandidateCondition hasUnoccupiedBindingSite(BindingSite bindingSite) {
        return new HasUnoccupiedBindingSite(bindingSite);
    }

    public static CandidateCondition hasNoMoreThanNumberOfPartners(ChemicalEntity entity, int number) {
        return new HasNoMoreThanNumberOfPartners(entity, number);
    }


}
