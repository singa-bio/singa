package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.SUBSTRATE;

/**
 * @author cl
 */
public class ReactionRule {

    public static int ruleId;

    private List<ReactantInformation> reactantInformation;

    public ReactionRule(List<ReactantInformation> reactantInformation) {
        this.reactantInformation = reactantInformation;
        ruleId++;
    }

    public List<ReactantInformation> getReactantInformation() {
        return reactantInformation;
    }

    public void setReactantInformation(List<ReactantInformation> reactantInformation) {
        this.reactantInformation = reactantInformation;
    }

    public static EntityStep create() {
        return new ReactionRuleBuilder();
    }

    public interface EntityStep {
        ModificationStep entity(ChemicalEntity entity);
    }

    public interface ModificationStep {

        ConditionStep binds(ChemicalEntity modificator, ModificationSite bindingSite);

        ConditionStep adds(ChemicalEntity modificator, ModificationSite bindingSite);

        ConditionStep remove(ChemicalEntity modificator, ModificationSite bindingSite);

        ConditionStep release(ChemicalEntity modificator, ModificationSite bindingSite);

        ConditionStep produce(ChemicalEntity result);

    }

    public interface ConditionStep {
        ConditionStep targetCondition(ReactantCondition condition);

        ConditionStep modificatorCondition(ReactantCondition condition);

        EntityStep andEntity();

        ModificationStep andModification();

        ReactionRule build();
    }

    public static class ReactionRuleBuilder implements EntityStep, ModificationStep, ConditionStep {

        private ChemicalEntity currentEntity;
        private Reactant currentTarget;
        private List<ReactantModification> currentTargetModifications;
        private List<ReactantCondition> currentTargetConditions;

        private Reactant currentModificator;
        private List<ReactantCondition> currentModificatorConditions;

        private List<ReactantInformation> reactantInformation;

        public ReactionRuleBuilder() {
            currentTargetModifications = new ArrayList<>();
            currentTargetConditions = new ArrayList<>();
            currentModificatorConditions = new ArrayList<>();
            reactantInformation = new ArrayList<>();
        }

        public ModificationStep entity(ChemicalEntity entity) {
            currentEntity = entity;
            currentTargetConditions.add(ReactantCondition.hasPart(entity));
            return this;
        }

        private ConditionStep performingModification(ReactantModification modification) {
            currentTargetModifications.add(modification);
            return this;
        }

        @Override
        public ConditionStep binds(ChemicalEntity modificator, ModificationSite bindingSite) {
            // determine modification
            ReactantModification modification = ReactantModification.bind(modificator)
                    .atSite(bindingSite)
                    .toTarget(currentEntity)
                    .build();
            // bindee
            currentTarget = new Reactant(modification.getTarget(), SUBSTRATE);
            // binder
            currentModificator = new Reactant(modification.getModificator(), SUBSTRATE);
            currentModificatorConditions.add(ReactantCondition.hasPart(modification.getModificator()));
            return performingModification(modification);
        }

        @Override
        public ConditionStep release(ChemicalEntity modificator, ModificationSite bindingSite) {
            ReactantModification modification = ReactantModification.release(modificator)
                    .atSite(bindingSite)
                    .toTarget(currentEntity)
                    .build();
            // binder
            currentTarget = new Reactant(modification.getModificator(), SUBSTRATE);
            currentTargetConditions.add(ReactantCondition.hasPart(modification.getModificator()));
            currentTargetConditions.add(ReactantCondition.hasPart(modification.getTarget()));

            return performingModification(modification);
        }

        @Override
        public ConditionStep remove(ChemicalEntity modificator, ModificationSite bindingSite) {
            // determine modification
            ReactantModification modification = ReactantModification.remove(modificator)
                    .atSite(bindingSite)
                    .toTarget(currentEntity)
                    .build();
            // reduced complex
            if (currentTarget == null) {
                currentTarget = new Reactant(modification.getTarget(), SUBSTRATE);
            }
            return performingModification(modification);
        }

        @Override
        public ConditionStep adds(ChemicalEntity modificator, ModificationSite bindingSite) {
            // determine modification
            ReactantModification modification = ReactantModification.add(modificator)
                    .atSite(bindingSite)
                    .toTarget(currentEntity)
                    .build();
            // original
            currentTarget = new Reactant(modification.getTarget(), SUBSTRATE);
            return performingModification(modification);
        }

        @Override
        public ConditionStep produce(ChemicalEntity result) {
            reactantInformation.add(new ReactantInformation(new Reactant(result, PRODUCT), Collections.emptyList(), Collections.emptyList()));
            return this;
        }

        @Override
        public ConditionStep targetCondition(ReactantCondition condition) {
            currentTargetConditions.add(condition);
            return this;
        }

        @Override
        public ConditionStep modificatorCondition(ReactantCondition condition) {
            currentModificatorConditions.add(condition);
            return this;
        }

        @Override
        public EntityStep andEntity() {
            // compile previous information
            return this;
        }

        @Override
        public ModificationStep andModification() {
            // add another modification
            return this;
        }

        @Override
        public ReactionRule build() {
            if (currentTarget != null) {
                reactantInformation.add(new ReactantInformation(currentTarget, currentTargetConditions, currentTargetModifications));
            }
            if (currentModificator != null) {
                reactantInformation.add(new ReactantInformation(currentModificator, currentModificatorConditions, Collections.emptyList()));
            }
            return new ReactionRule(reactantInformation);
        }
    }


}
