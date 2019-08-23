package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ModificationSite;
import bio.singa.chemistry.entities.graphcomplex.ModificationOperation;
import bio.singa.core.utility.Pair;

/**
 * @author cl
 */
public class ReactantModification {

    private ModificationOperation operationType;

    private ChemicalEntity target;
    private ChemicalEntity modificator;
    private ModificationSite site;

    private ComplexEntity targetSite;
    private ComplexEntity modificationSite;

    public ReactantModification(ChemicalEntity target, ChemicalEntity modificator, ModificationSite site, ModificationOperation operationType) {
        this.modificator = modificator;
        this.site = site;
        this.target = target;
        this.operationType = operationType;
        targetSite = ComplexEntity.from(target, site);
        modificationSite = ComplexEntity.from(site, modificator);
    }

    public ModificationOperation getOperationType() {
        return operationType;
    }

    public void setOperationType(ModificationOperation operationType) {
        this.operationType = operationType;
    }

    public ChemicalEntity getTarget() {
        return target;
    }

    public void setTarget(ChemicalEntity target) {
        this.target = target;
    }

    public ChemicalEntity getModificator() {
        return modificator;
    }

    public void setModificator(ChemicalEntity modificator) {
        this.modificator = modificator;
    }

    public ModificationSite getSite() {
        return site;
    }

    public void setSite(ModificationSite site) {
        this.site = site;
    }

    public ComplexEntity getTargetSite() {
        return targetSite;
    }

    public void setTargetSite(ComplexEntity targetSite) {
        this.targetSite = targetSite;
    }

    public ComplexEntity getModificationSite() {
        return modificationSite;
    }

    public void setModificationSite(ComplexEntity modificationSite) {
        this.modificationSite = modificationSite;
    }

    public ComplexEntity apply(ComplexEntity target) {
        switch (operationType) {
            case ADD: {
                ComplexEntity modifiedEntity = target.copy();
                // replace original site with the complex
                modifiedEntity.attach(getModificationSite(), getSite());
                return modifiedEntity;
            }
            case REMOVE: {
                ComplexEntity modifiedEntity = target.copy();
                // replace original site with the complex
                modifiedEntity.removeFromPosition(getModificator(), getModificationSite());
                return modifiedEntity;
            }
            default: {
                throw new IllegalArgumentException("Add and remove operations can only be called with one arguments");
            }
        }
    }

    public Pair<ComplexEntity> release(ComplexEntity target) {
        if (operationType == ModificationOperation.RELEASE) {
            ComplexEntity modifiedEntity = target.copy();
            // released entity
            ComplexEntity splitOffPart = modifiedEntity.splitOfComplex(getTarget(), getModificator(), getSite());
            return new Pair<>(modifiedEntity, splitOffPart);
        }
        throw new IllegalArgumentException("Bind operations can only be called with two arguments");
    }

    public ComplexEntity apply(ComplexEntity target, ChemicalEntity modificator) {
        if (operationType == ModificationOperation.BIND) {// copy original entity
            ComplexEntity modifiedEntity = target.copy();
            // replace original site with the complex
            modifiedEntity.attach(modificator, getSite());
            return modifiedEntity;
        }
        throw new IllegalArgumentException("Bind operations can only be called with two arguments");
    }

    public static BindingSiteStep bind(ChemicalEntity modificator) {
        EntityModificationBuilder entityModificationBuilder = new EntityModificationBuilder(ModificationOperation.BIND);
        entityModificationBuilder.modificator = modificator;
        return entityModificationBuilder;
    }

    public static BindingSiteStep release(ChemicalEntity modificator) {
        EntityModificationBuilder entityModificationBuilder = new EntityModificationBuilder(ModificationOperation.RELEASE);
        entityModificationBuilder.modificator = modificator;
        return entityModificationBuilder;
    }

    public static BindingSiteStep remove(ChemicalEntity modificator) {
        EntityModificationBuilder entityModificationBuilder = new EntityModificationBuilder(ModificationOperation.REMOVE);
        entityModificationBuilder.modificator = modificator;
        return entityModificationBuilder;
    }

    public static BindingSiteStep add(ChemicalEntity modificator) {
        EntityModificationBuilder entityModificationBuilder = new EntityModificationBuilder(ModificationOperation.ADD);
        entityModificationBuilder.modificator = modificator;
        return entityModificationBuilder;
    }

    public interface BindingSiteStep {
        TargetStep atSite(ModificationSite bindingSite);
    }

    public interface TargetStep {
        BuildStep toTarget(ChemicalEntity target);
    }

    public interface BuildStep {
        ReactantModification build();
    }

    public static class EntityModificationBuilder implements BindingSiteStep, TargetStep, BuildStep {

        private ModificationOperation operation;
        private ChemicalEntity target;
        private ChemicalEntity modificator;
        private ModificationSite site;

        public EntityModificationBuilder(ModificationOperation operation) {
            this.operation = operation;
        }

        @Override
        public TargetStep atSite(ModificationSite site) {
            this.site = site;
            return this;
        }

        @Override
        public BuildStep toTarget(ChemicalEntity target) {
            this.target = target;
            return this;
        }

        @Override
        public ReactantModification build() {
            return new ReactantModification(target, modificator, site, operation);
        }

    }

    @Override
    public String toString() {
        switch (operationType) {
            case BIND:
            case ADD:
            case REMOVE:
                return String.format(getOperationType().getDescriptor(), getModificator(), getTarget(), getSite());
            case RELEASE:
                return String.format(getOperationType().getDescriptor(), getTarget(), getModificator(), getSite());
            default:
                return "unknown modification";
        }
    }
}
