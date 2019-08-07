package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.SUBSTRATE;
import static bio.singa.simulation.model.rules.reactions.ReactionNetworkGenerator.fromBoundStatus;

/**
 * @author cl
 */
public class ReactionRule {

    private List<ReactantInformation> reactantInformation;
    private boolean productsOnly;

    public ReactionRule(List<ReactantInformation> reactantInformation) {
        this.reactantInformation = reactantInformation;
    }

    public List<ReactantInformation> getReactantInformation() {
        return reactantInformation;
    }

    public void setReactantInformation(List<ReactantInformation> reactantInformation) {
        this.reactantInformation = reactantInformation;
    }

    public boolean isProductsOnly() {
        return productsOnly;
    }

    public void setProductsOnly(boolean productsOnly) {
        this.productsOnly = productsOnly;
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
            // binder
            currentTarget = new Reactant(modification.getTarget(), SUBSTRATE, fromBoundStatus(modification.getTarget().isMembraneBound()));
            currentTargetConditions.add(ReactantCondition.isUnoccupied(modification.getSite()));
            // bindee
            if (!(modification.getModificator() instanceof SmallMolecule)) {
                currentModificator = new Reactant(modification.getModificator(), SUBSTRATE, fromBoundStatus(modification.getModificator().isMembraneBound()));
                currentModificatorConditions.add(ReactantCondition.hasPart(modification.getModificator()));
                currentModificatorConditions.add(ReactantCondition.isUnoccupied(modification.getSite()));
            } else {
                currentModificator = new Reactant(modification.getModificator(), SUBSTRATE, fromBoundStatus(modification.getModificator().isMembraneBound()));
                currentModificatorConditions.add(ReactantCondition.isSmallMolecule());
            }

            return performingModification(modification);
        }

        @Override
        public ConditionStep release(ChemicalEntity modificator, ModificationSite bindingSite) {
            ReactantModification modification = ReactantModification.release(modificator)
                    .atSite(bindingSite)
                    .toTarget(currentEntity)
                    .build();
            // binder
            currentTarget = new Reactant(modification.getModificator(), SUBSTRATE, fromBoundStatus(modification.getModificator().isMembraneBound()));
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
                currentTarget = new Reactant(modification.getTarget(), SUBSTRATE, fromBoundStatus(modification.getTarget().isMembraneBound()));
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
            currentTarget = new Reactant(modification.getTarget(), SUBSTRATE, fromBoundStatus(modification.getTarget().isMembraneBound()));
            return performingModification(modification);
        }

        @Override
        public ConditionStep produce(ChemicalEntity result) {
            reactantInformation.add(new ReactantInformation(new Reactant(result, PRODUCT, fromBoundStatus(result.isMembraneBound())), Collections.emptyList(), Collections.emptyList()));
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
