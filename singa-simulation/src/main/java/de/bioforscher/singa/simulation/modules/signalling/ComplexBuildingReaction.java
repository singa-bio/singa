package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.ChemistryFeatureContainer;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.concentrations.SimpleConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNodeSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.*;

import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.MEMBRANE;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.NON_MEMBRANE;

/**
 * @author cl
 */
public class ComplexBuildingReaction extends AbstractNodeSpecificModule implements Featureable {

    public static BindeeSelection inSimulation(Simulation simulation) {
        return new BindingBuilder(simulation);
    }

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        availableFeatures.add(ForwardsRateConstant.class);
        availableFeatures.add(BackwardsRateConstant.class);
    }

    /**
     * The features of the reaction.
     */
    private FeatureContainer features;

    private ChemicalEntity binder;
    private CellSectionState binderCellSectionState;
    private CellSection binderRelevantCellSection;

    private boolean outerLayerIsRelevant;
    private boolean innerLayerIsRelevant;

    private ChemicalEntity bindee;
    private CellSectionState bindeeCellSectionState;
    private CellSection bindeeRelevantCellSection;

    private ComplexedChemicalEntity complex;

    /**
     * Creates a new section independent module for the given simulation.
     *
     * @param simulation The simulation.
     */
    private ComplexBuildingReaction(Simulation simulation) {
        super(simulation);
        features = new ChemistryFeatureContainer();
        // deltas
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    private void initialize() {
        complex = new ComplexedChemicalEntity.Builder(binder.getIdentifier().getIdentifier() + "-" + bindee.getIdentifier().getIdentifier())
                .addAssociatedPart(binder)
                .addAssociatedPart(bindee)
                .build();
        // reference entities for this module
        addReferencedEntity(bindee);
        addReferencedEntity(binder);
        addReferencedEntity(complex);
        // reference module in simulation
        addModuleToSimulation();
    }

    private List<Delta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();
        determineRelevantSectionStates(concentrationContainer);
        if (!innerLayerIsRelevant && !outerLayerIsRelevant) {
            // membrane irrelevant binding
            if (concentrationContainer instanceof SimpleConcentrationContainer) {
                SimpleConcentrationContainer simpleContainer = (SimpleConcentrationContainer) concentrationContainer;
                bindeeRelevantCellSection = simpleContainer.getCellSection();
                binderRelevantCellSection = simpleContainer.getCellSection();
                deltas.addAll(determineDeltasForSections(concentrationContainer));
            } else {
                MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
                bindeeRelevantCellSection = membraneContainer.getOuterPhaseSection();
                binderRelevantCellSection = membraneContainer.getOuterPhaseSection();
                deltas.addAll(determineDeltasForSections(concentrationContainer));
                bindeeRelevantCellSection = membraneContainer.getInnerPhaseSection();
                binderRelevantCellSection = membraneContainer.getInnerPhaseSection();
                deltas.addAll(determineDeltasForSections(concentrationContainer));
            }
        } else if (innerLayerIsRelevant) {
            // inner layer and phase
            MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
            if (bindeeCellSectionState == CellSectionState.MEMBRANE) {
                bindeeRelevantCellSection = membraneContainer.getInnerLayerSection();
            } else {
                bindeeRelevantCellSection = membraneContainer.getInnerPhaseSection();
            }
            if (binderCellSectionState == CellSectionState.MEMBRANE) {
                binderRelevantCellSection = membraneContainer.getInnerLayerSection();
            } else {
                binderRelevantCellSection = membraneContainer.getInnerPhaseSection();
            }
            deltas.addAll(determineDeltasForSections(concentrationContainer));
        } else {
            // outer layer and phase
            MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
            if (bindeeCellSectionState == CellSectionState.MEMBRANE) {
                bindeeRelevantCellSection = membraneContainer.getOuterLayerSection();
            } else {
                bindeeRelevantCellSection = membraneContainer.getOuterPhaseSection();
            }
            if (binderCellSectionState == CellSectionState.MEMBRANE) {
                binderRelevantCellSection = membraneContainer.getOuterLayerSection();
            } else {
                binderRelevantCellSection = membraneContainer.getOuterPhaseSection();
            }
            deltas.addAll(determineDeltasForSections(concentrationContainer));
        }
        return deltas;
    }

    private void determineRelevantSectionStates(ConcentrationContainer concentrationContainer) {
        if (bindeeCellSectionState == NON_MEMBRANE && binderCellSectionState == MEMBRANE ||
                bindeeCellSectionState == MEMBRANE && binderCellSectionState == NON_MEMBRANE) {
            // absorb to entity in membrane
            MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
            // determine relevant side(s)
            // a side is relevant if it contains either binder or complex molecules
            if (membraneContainer.getAvailableConcentration(membraneContainer.getMembrane().getOuterLayer(), binder).getValue().doubleValue() > 0.0 ||
                    membraneContainer.getAvailableConcentration(membraneContainer.getMembrane().getOuterLayer(), complex).getValue().doubleValue() > 0.0) {
                outerLayerIsRelevant = true;
            }
            if (membraneContainer.getAvailableConcentration(membraneContainer.getMembrane().getInnerLayer(), binder).getValue().doubleValue() > 0.0 ||
                    membraneContainer.getAvailableConcentration(membraneContainer.getMembrane().getInnerLayer(), complex).getValue().doubleValue() > 0.0) {
                innerLayerIsRelevant = true;
            }
        }
    }

    private List<Delta> determineDeltasForSections(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();
        double velocity = calculateVelocity(concentrationContainer);
        // change ligand concentration
        deltas.add(new Delta(this, bindeeRelevantCellSection, bindee, Quantities.getQuantity(-velocity, EnvironmentalParameters.getTransformedMolarConcentration())));
        // change unbound receptor concentration
        deltas.add(new Delta(this, binderRelevantCellSection, binder, Quantities.getQuantity(-velocity, EnvironmentalParameters.getTransformedMolarConcentration())));
        // change bound receptor concentration
        deltas.add(new Delta(this, binderRelevantCellSection, complex, Quantities.getQuantity(velocity, EnvironmentalParameters.getTransformedMolarConcentration())));
        return deltas;
    }

    private double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // get rates
        final double forwardsRateConstant = getScaledFeature(ForwardsRateConstant.class).getValue().doubleValue();
        final double backwardsRateConstant = getScaledFeature(BackwardsRateConstant.class).getValue().doubleValue();
        // get concentrations
        final double bindeeConcentration = concentrationContainer.getAvailableConcentration(bindeeRelevantCellSection, bindee).getValue().doubleValue();
        final double binderConcentration = concentrationContainer.getAvailableConcentration(binderRelevantCellSection, binder).getValue().doubleValue();
        final double complexConcentration = concentrationContainer.getAvailableConcentration(bindeeRelevantCellSection, complex).getValue().doubleValue();
        // calculate velocity
        return forwardsRateConstant * binderConcentration * bindeeConcentration - backwardsRateConstant * complexConcentration;
    }


    public ComplexedChemicalEntity getComplex() {
        return complex;
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return features.getFeature(featureTypeClass);
    }

    /**
     * Returns the feature for the entity. The feature is scaled according to the time step size and considering half
     * steps.
     *
     * @param featureClass The feature to get.
     * @param <FeatureContentType> The type of the feature.
     * @return The requested feature for the corresponding entity.
     */
    protected <FeatureContentType> FeatureContentType getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass) {
        ScalableFeature<FeatureContentType> feature = getFeature(featureClass);
        if (halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return features.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public interface BindeeSelection {
        BindeeSectionSelection of(ChemicalEntity bindee);
        BindeeSectionSelection of(ChemicalEntity bindee, ForwardsRateConstant forwardsRateConstant);
    }

    public interface BindeeSectionSelection {
        BinderSelection in(CellSectionState bindeeSection);
    }

    public interface BinderSelection {
        BinderSectionSelection by(ChemicalEntity binder);
        BinderSectionSelection by(ChemicalEntity binder, BackwardsRateConstant forwardsRateConstant);
    }

    public interface BinderSectionSelection {
        ComplexBuildingReaction to(CellSectionState binderSection);
    }

    @Override
    public String toString() {
        return "ComplexBuildingReaction ("+binder.getIdentifier()+" binds "+bindee.getIdentifier()+")";
    }

    public static class BindingBuilder implements BinderSelection, BinderSectionSelection, BindeeSelection, BindeeSectionSelection {

        private ComplexBuildingReaction module;

        public BindingBuilder(Simulation simulation) {
            module = new ComplexBuildingReaction(simulation);
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity bindee) {
            module.bindee = bindee;
            return this;
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity binder, ForwardsRateConstant forwardsRateConstant) {
            module.setFeature(forwardsRateConstant);
            return of(binder);
        }

        @Override
        public BinderSelection in(CellSectionState binderSection) {
            module.bindeeCellSectionState = binderSection;
            return this;
        }

        @Override
        public BinderSectionSelection by(ChemicalEntity binder) {
            module.binder = binder;
            return this;
        }

        @Override
        public BinderSectionSelection by(ChemicalEntity binder, BackwardsRateConstant backwardsRateConstant) {
            module.setFeature(backwardsRateConstant);
            return by(binder);
        }

        @Override
        public ComplexBuildingReaction to(CellSectionState bindeeSection) {
            module.binderCellSectionState = bindeeSection;
            module.initialize();
            return module;
        }

    }

}
