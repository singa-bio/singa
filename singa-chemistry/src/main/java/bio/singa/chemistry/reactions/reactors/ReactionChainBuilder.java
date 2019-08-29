package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.GraphComplex;
import bio.singa.chemistry.reactions.modifications.ComplexEntityModification;
import bio.singa.chemistry.reactions.modifications.ComplexEntityModificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static bio.singa.chemistry.reactions.conditions.CandidateConditionBuilder.*;

/**
 * @author cl
 */
public class ReactionChainBuilder {

    public static AddReactorSecondarySubstrateStep add(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
        return new AddReactionChainBuilder(bindingSite, chemicalEntity);
    }

    public static AddReactorSecondarySubstrateStep add(ChemicalEntity chemicalEntity) {
        return new AddReactionChainBuilder(chemicalEntity);
    }

    public static BindReactorPrimaryConditionStep bind(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
        return new BindReactionChainBuilder(bindingSite, chemicalEntity);
    }

    public static BindReactorPrimaryConditionStep bind(ChemicalEntity chemicalEntity) {
        return new BindReactionChainBuilder(chemicalEntity);
    }

    public static ReleaseReactorPrimaryConditionStep release(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
        return new ReleaseReactionChainBuilder(bindingSite, chemicalEntity);
    }

    public static ReleaseReactorSecondarySubstrateStep release(ChemicalEntity chemicalEntity) {
        return new ReleaseReactionChainBuilder(chemicalEntity);
    }

    public static RemoveReactorSecondarySubstrateStep remove(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
        return new RemoveReactionChainBuilder(bindingSite, chemicalEntity);
    }

    public static RemoveReactorSecondarySubstrateStep remove(ChemicalEntity chemicalEntity) {
        return new RemoveReactionChainBuilder(chemicalEntity);
    }

    public interface ReactorChoiceStep {
        AddReactorSecondarySubstrateStep add(BindingSite bindingSite, ChemicalEntity chemicalEntity);

        AddReactorSecondarySubstrateStep add(ChemicalEntity chemicalEntity);

        BindReactorPrimaryConditionStep bind(BindingSite bindingSite, ChemicalEntity chemicalEntity);

        BindReactorPrimaryConditionStep bind(ChemicalEntity chemicalEntity);

        ReleaseReactorSecondarySubstrateStep release(BindingSite bindingSite, ChemicalEntity chemicalEntity);

        ReleaseReactorSecondarySubstrateStep release(ChemicalEntity chemicalEntity);

        RemoveReactorPrimaryConditionStep remove(BindingSite bindingSite, ChemicalEntity chemicalEntity);

        RemoveReactorSecondarySubstrateStep remove(ChemicalEntity chemicalEntity);
    }

    public interface ConnectorStep {
        ConnectorStep identifier(String identifier);

        ReactorChoiceStep and();

        ConnectorStep considerInversion();

        ReactionChain build();
    }


    public interface AddReactorPrimaryConditionStep extends ConnectorStep {
        AddReactorPrimaryConditionStep condition(Predicate<GraphComplex> condition);
    }

    public interface AddReactorSecondarySubstrateStep {
        AddReactorPrimaryConditionStep to(ChemicalEntity chemicalEntity);
    }


    public interface BindReactorPrimaryConditionStep extends BindReactorSecondarySubstrateStep {
        BindReactorPrimaryConditionStep primaryCondition(Predicate<GraphComplex> condition);
    }

    public interface BindReactorSecondarySubstrateStep {
        BindReactorSecondaryConditionStep to(ChemicalEntity chemicalEntity);
    }

    public interface BindReactorSecondaryConditionStep extends ConnectorStep {
        BindReactorSecondaryConditionStep secondaryCondition(Predicate<GraphComplex> condition);
    }


    public interface ReleaseReactorPrimaryConditionStep extends ConnectorStep {
        ReleaseReactorPrimaryConditionStep condition(Predicate<GraphComplex> condition);
    }

    public interface ReleaseReactorSecondarySubstrateStep {
        ReleaseReactorPrimaryConditionStep from(ChemicalEntity chemicalEntity);
    }

    public interface RemoveReactorPrimaryConditionStep extends ConnectorStep {
        RemoveReactorPrimaryConditionStep condition(Predicate<GraphComplex> condition);
    }

    public interface RemoveReactorSecondarySubstrateStep {
        RemoveReactorPrimaryConditionStep from(ChemicalEntity chemicalEntity);
    }


    public static abstract class AbstractReactorBuilder implements ReactorChoiceStep, ConnectorStep {

        private String identifier;
        protected List<ComplexReactor> reactors;

        protected ComplexEntityModification modification;
        protected List<Predicate<GraphComplex>> primaryCandidateConditions;
        protected List<Predicate<GraphComplex>> secondaryCandidateConditions;

        protected BindingSite bindingSite;
        protected ChemicalEntity primaryEntity;
        protected ChemicalEntity secondaryEntity;

        protected boolean considerInversion;

        public AbstractReactorBuilder() {
            reactors = new ArrayList<>();
            primaryCandidateConditions = new ArrayList<>();
            secondaryCandidateConditions = new ArrayList<>();
        }

        abstract ComplexReactor createAndChainReactor();

        protected void oneToOneAssignment(ChemicalEntity primaryEntity) {
            this.primaryEntity = primaryEntity;
            if (bindingSite == null) {
                bindingSite = BindingSite.forPair(primaryEntity, secondaryEntity);
            }
        }

        @Override
        public AddReactorSecondarySubstrateStep add(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
            AddReactionChainBuilder builder = new AddReactionChainBuilder(bindingSite, chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public AddReactorSecondarySubstrateStep add(ChemicalEntity chemicalEntity) {
            AddReactionChainBuilder builder = new AddReactionChainBuilder(chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public BindReactorPrimaryConditionStep bind(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
            BindReactionChainBuilder builder = new BindReactionChainBuilder(bindingSite, chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public BindReactorPrimaryConditionStep bind(ChemicalEntity chemicalEntity) {
            BindReactionChainBuilder builder = new BindReactionChainBuilder(chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public ReleaseReactorSecondarySubstrateStep release(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
            ReleaseReactionChainBuilder builder = new ReleaseReactionChainBuilder(bindingSite, chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public ReleaseReactorSecondarySubstrateStep release(ChemicalEntity chemicalEntity) {
            ReleaseReactionChainBuilder builder = new ReleaseReactionChainBuilder(chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public RemoveReactorPrimaryConditionStep remove(BindingSite bindingSite, ChemicalEntity chemicalEntity) {
            RemoveReactionChainBuilder builder = new RemoveReactionChainBuilder(bindingSite, chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        @Override
        public RemoveReactorSecondarySubstrateStep remove(ChemicalEntity chemicalEntity) {
            RemoveReactionChainBuilder builder = new RemoveReactionChainBuilder(chemicalEntity);
            passInformation(builder, this);
            return builder;
        }

        private static void passInformation(AbstractReactorBuilder nextBuilder, AbstractReactorBuilder lastBuilder) {
            nextBuilder.reactors.addAll(lastBuilder.reactors);
            nextBuilder.identifier = lastBuilder.identifier;
        }

        @Override
        public ReactorChoiceStep and() {
            ComplexReactor currentReactor = createAndChainReactor();
            reactors.add(currentReactor);
            return this;
        }

        @Override
        public ConnectorStep identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        @Override
        public ConnectorStep considerInversion() {
            considerInversion = true;
            return this;
        }

        @Override
        public ReactionChain build() {
            and();
            ReactionChain reactionChain = new ReactionChain(reactors);
            reactionChain.setIdentifier(identifier);
            reactionChain.setConsiderInversion(considerInversion);
            return reactionChain;
        }
    }

    public static class AddReactionChainBuilder extends AbstractReactorBuilder implements AddReactorPrimaryConditionStep, AddReactorSecondarySubstrateStep {

        public AddReactionChainBuilder(BindingSite bindingSite, ChemicalEntity secondaryEntity) {
            this.bindingSite = bindingSite;
            this.secondaryEntity = secondaryEntity;
        }

        public AddReactionChainBuilder(ChemicalEntity secondaryEntity) {
            this.secondaryEntity = secondaryEntity;
        }

        @Override
        ComplexReactor createAndChainReactor() {
            OneToOneReactor reactor = new OneToOneReactor();
            reactor.setModification(modification);
            reactor.setPrimaryCandidateConditions(primaryCandidateConditions);
            return reactor;
        }

        @Override
        public AddReactorPrimaryConditionStep condition(Predicate<GraphComplex> condition) {
            primaryCandidateConditions.add(condition);
            return this;
        }

        @Override
        public AddReactorPrimaryConditionStep to(ChemicalEntity primaryEntity) {
            oneToOneAssignment(primaryEntity);
            modification = ComplexEntityModificationBuilder.add(bindingSite, secondaryEntity);
            modification.setPrimaryEntity(primaryEntity);
            modification.setSecondaryEntity(secondaryEntity);
            primaryCandidateConditions.add(hasOneOfEntity(primaryEntity));
            primaryCandidateConditions.add(hasUnoccupiedBindingSite(bindingSite));
            return this;
        }

    }

    public static class BindReactionChainBuilder extends AbstractReactorBuilder implements BindReactorPrimaryConditionStep, BindReactorSecondaryConditionStep, BindReactorSecondarySubstrateStep {

        public BindReactionChainBuilder(BindingSite bindingSite, ChemicalEntity primaryEntity) {
            this.bindingSite = bindingSite;
            this.primaryEntity = primaryEntity;
        }

        public BindReactionChainBuilder(ChemicalEntity primaryEntity) {
            this.primaryEntity = primaryEntity;
        }

        @Override
        ComplexReactor createAndChainReactor() {
            TwoToOneReactor reactor = new TwoToOneReactor();
            reactor.setModification(modification);
            // small molecules are supposed to have only one possible binding partner at once
            if (modification.getPrimaryEntity() instanceof SmallMolecule) {
                primaryCandidateConditions.add(hasNoMoreThanNumberOfPartners(modification.getPrimaryEntity(), 0));
            }
            reactor.setPrimaryCandidateConditions(primaryCandidateConditions);
            if (modification.getSecondaryEntity() instanceof SmallMolecule) {
                secondaryCandidateConditions.add(hasNoMoreThanNumberOfPartners(modification.getSecondaryEntity(), 0));
            }
            reactor.setSecondCandidateConditions(secondaryCandidateConditions);
            return reactor;
        }

        @Override
        public BindReactorPrimaryConditionStep primaryCondition(Predicate<GraphComplex> condition) {
            primaryCandidateConditions.add(condition);
            return this;
        }

        @Override
        public BindReactorSecondaryConditionStep to(ChemicalEntity secondaryEntity) {
            this.secondaryEntity = secondaryEntity;
            if (bindingSite == null) {
                bindingSite = BindingSite.forPair(secondaryEntity, primaryEntity);
            }
            modification = ComplexEntityModificationBuilder.bind(bindingSite);

            modification.setPrimaryEntity(primaryEntity);
            primaryCandidateConditions.add(hasOneOfEntity(primaryEntity));
            primaryCandidateConditions.add(hasUnoccupiedBindingSite(bindingSite));

            modification.setSecondaryEntity(secondaryEntity);
            secondaryCandidateConditions.add(hasOneOfEntity(secondaryEntity));
            secondaryCandidateConditions.add(hasUnoccupiedBindingSite(bindingSite));
            return this;
        }

        @Override
        public BindReactorSecondaryConditionStep secondaryCondition(Predicate<GraphComplex> condition) {
            secondaryCandidateConditions.add(condition);
            return this;
        }
    }

    public static class ReleaseReactionChainBuilder extends AbstractReactorBuilder implements ReleaseReactorPrimaryConditionStep, ReleaseReactorSecondarySubstrateStep {

        public ReleaseReactionChainBuilder(BindingSite bindingSite, ChemicalEntity primaryEntity) {
            this.bindingSite = bindingSite;
            this.primaryEntity = primaryEntity;
        }

        public ReleaseReactionChainBuilder(ChemicalEntity primaryEntity) {
            this.primaryEntity = primaryEntity;
        }

        @Override
        ComplexReactor createAndChainReactor() {
            OneToTwoReactor reactor = new OneToTwoReactor();
            reactor.setModification(modification);
            reactor.setPrimaryCandidateConditions(primaryCandidateConditions);
            return reactor;
        }

        @Override
        public ReleaseReactorPrimaryConditionStep condition(Predicate<GraphComplex> condition) {
            primaryCandidateConditions.add(condition);
            return this;
        }

        @Override
        public ReleaseReactorPrimaryConditionStep from(ChemicalEntity secondaryEntity) {
            this.secondaryEntity = secondaryEntity;
            if (bindingSite == null) {
                bindingSite = BindingSite.forPair(secondaryEntity, primaryEntity);
            }
            modification = ComplexEntityModificationBuilder.release(bindingSite);

            modification.setPrimaryEntity(primaryEntity);
            modification.setSecondaryEntity(secondaryEntity);

            primaryCandidateConditions.add(hasOccupiedBindingSite(bindingSite));
            primaryCandidateConditions.add(hasOneOfEntity(primaryEntity));
            primaryCandidateConditions.add(hasOneOfEntity(secondaryEntity));
            return this;
        }

    }

    public static class RemoveReactionChainBuilder extends AbstractReactorBuilder implements RemoveReactorPrimaryConditionStep, RemoveReactorSecondarySubstrateStep {

        public RemoveReactionChainBuilder(BindingSite bindingSite, ChemicalEntity secondaryEntity) {
            this.bindingSite = bindingSite;
            this.secondaryEntity = secondaryEntity;
        }

        public RemoveReactionChainBuilder(ChemicalEntity secondaryEntity) {
            this.secondaryEntity = secondaryEntity;
        }

        @Override
        ComplexReactor createAndChainReactor() {
            OneToOneReactor reactor = new OneToOneReactor();
            reactor.setModification(modification);
            reactor.setPrimaryCandidateConditions(primaryCandidateConditions);
            return reactor;
        }

        @Override
        public RemoveReactorPrimaryConditionStep condition(Predicate<GraphComplex> condition) {
            primaryCandidateConditions.add(condition);
            return this;
        }

        @Override
        public RemoveReactorPrimaryConditionStep from(ChemicalEntity primaryEntity) {
            oneToOneAssignment(primaryEntity);
            modification = ComplexEntityModificationBuilder.remove(bindingSite, secondaryEntity);
            modification.setPrimaryEntity(primaryEntity);
            modification.setSecondaryEntity(secondaryEntity);
            primaryCandidateConditions.add(hasOneOfEntity(primaryEntity));
            primaryCandidateConditions.add(hasOccupiedBindingSite(bindingSite));
            return this;
        }

    }

}
