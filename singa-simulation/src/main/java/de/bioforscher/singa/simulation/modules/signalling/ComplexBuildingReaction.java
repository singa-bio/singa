package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.ChemistryFeatureContainer;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.concentrations.SimpleConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNodeSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;

import static de.bioforscher.singa.features.parameters.EnvironmentalParameters.getTransformedMolarConcentration;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.MetricPrefix.NANO;

/**
 * @author cl
 */
public class ComplexBuildingReaction extends AbstractNodeSpecificModule implements Featureable {

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    static {
        requiredFeatures.add(ForwardsRateConstant.class);
        requiredFeatures.add(BackwardsRateConstant.class);
    }

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    public static BindeeSelection inSimulation(Simulation simulation) {
        return new BindingBuilder(simulation);
    }

    /**
     * The features of the reaction.
     */
    private FeatureContainer features;
    private RateConstant forwardsReactionRate;
    private RateConstant backwardsReactionRate;

    private ChemicalEntity binder;
    private CellSectionState binderCellSectionState;
    private CellSection binderRelevantCellSection;

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
        // in no complex has been set create it
        if (complex == null) {
            complex = new ComplexedChemicalEntity.Builder(binder.getIdentifier().getIdentifier() + ":" + bindee.getIdentifier().getIdentifier())
                    .addAssociatedPart(binder)
                    .addAssociatedPart(bindee)
                    .build();
        }
        // reference entities for this module
        addReferencedEntity(bindee);
        addReferencedEntity(binder);
        addReferencedEntity(complex);
        // reference module in simulation
        addModuleToSimulation();
    }

    private List<Delta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();

        if (nonMembraneBinding(concentrationContainer)) {
            // membrane is not relevant
            if (concentrationContainer instanceof SimpleConcentrationContainer) {
                // non-membrane to non-membrane in non-membrane node
                SimpleConcentrationContainer simpleContainer = (SimpleConcentrationContainer) concentrationContainer;
                bindeeRelevantCellSection = simpleContainer.getCellSection();
                binderRelevantCellSection = simpleContainer.getCellSection();
                deltas.addAll(determineDeltasForSections(concentrationContainer));
            } else {
                throw new IllegalStateException("The current node " + getCurrentNode() + " seems to be annotated as " +
                        "membrane but is not initialized with a membrane concentration container.");
            }
        } else if (noSectionChanges()) {
            // sections are not relevant
            MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
            // membrane to membrane binding
            // if membrane, always user outer layer
            bindeeRelevantCellSection = membraneContainer.getOuterLayerSection();
            binderRelevantCellSection = membraneContainer.getOuterLayerSection();
            deltas.addAll(determineDeltasForSections(concentrationContainer));
            // non-membrane to non-membrane but in membrane node
            // inner phase
            bindeeRelevantCellSection = membraneContainer.getInnerPhaseSection();
            binderRelevantCellSection = membraneContainer.getInnerPhaseSection();
            deltas.addAll(determineDeltasForSections(concentrationContainer));
            // outer phase
            bindeeRelevantCellSection = membraneContainer.getOuterPhaseSection();
            binderRelevantCellSection = membraneContainer.getOuterPhaseSection();
            deltas.addAll(determineDeltasForSections(concentrationContainer));
        } else {
            // sections are relevant since they are different
            MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
            // deltas for inner phase
            if (bindeeCellSectionState == CellSectionState.MEMBRANE) {
                // if membrane, always user outer layer
                bindeeRelevantCellSection = membraneContainer.getOuterLayerSection();
            } else {
                bindeeRelevantCellSection = membraneContainer.getInnerPhaseSection();
            }
            if (binderCellSectionState == CellSectionState.MEMBRANE) {
                // if membrane, always user outer layer
                binderRelevantCellSection = membraneContainer.getOuterLayerSection();
            } else {
                binderRelevantCellSection = membraneContainer.getInnerPhaseSection();
            }
            deltas.addAll(determineDeltasForSections(concentrationContainer));
            // deltas for outer phase
            if (bindeeCellSectionState == CellSectionState.MEMBRANE) {
                // if membrane, always user outer layer
                bindeeRelevantCellSection = membraneContainer.getOuterLayerSection();
            } else {
                bindeeRelevantCellSection = membraneContainer.getOuterPhaseSection();
            }
            if (binderCellSectionState == CellSectionState.MEMBRANE) {
                // if membrane, always user outer layer
                binderRelevantCellSection = membraneContainer.getOuterLayerSection();
            } else {
                binderRelevantCellSection = membraneContainer.getOuterPhaseSection();
            }
            deltas.addAll(determineDeltasForSections(concentrationContainer));
        }
        return deltas;
    }

    private boolean nonMembraneBinding(ConcentrationContainer concentrationContainer) {
        return !(concentrationContainer instanceof MembraneContainer);
    }

    private boolean noSectionChanges() {
        return bindeeCellSectionState == binderCellSectionState;
    }

    private List<Delta> determineDeltasForSections(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();
        double velocity = calculateVelocity(concentrationContainer);
        // change ligand concentration
        deltas.add(new Delta(this, bindeeRelevantCellSection, bindee, Quantities.getQuantity(-velocity, /* getTransformedMolarConcentration() */ NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration())));
        // change unbound receptor concentration
        deltas.add(new Delta(this, binderRelevantCellSection, binder, Quantities.getQuantity(-velocity, /* getTransformedMolarConcentration() */ NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration())));
        // change bound receptor concentration
        deltas.add(new Delta(this, binderRelevantCellSection, complex, Quantities.getQuantity(velocity, /* getTransformedMolarConcentration() */ NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration())));
        return deltas;
    }

    private double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // get rates
        final double forwardsRateConstant = getScaledForwardsReactionRate().getValue().doubleValue();
        final double backwardsRateConstant = getScaledBackwardsReactionRate().getValue().doubleValue();
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

    public String getReactionString() {
        String substrates = binder.getIdentifier() + " + " + bindee.getIdentifier();
        String products = complex.getIdentifier().toString();
        return substrates + " \u21CB " + products;
    }

    @Override
    public String getStringForProtocol() {
        return getClass().getSimpleName() + " summary:" + System.lineSeparator() +
                "  " + "primary identifier: " + getIdentifier().getIdentifier() + System.lineSeparator() +
                "  " + "reaction: " + getReactionString() + System.lineSeparator() +
                "  " + "binding: " + bindee.getIdentifier() + " is bound to " + binder.getIdentifier() + System.lineSeparator() +
                "  " + "features: " + System.lineSeparator() +
                features.listFeatures("    ");
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
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
    protected <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass) {
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
    public void checkFeatures() {
        boolean forwardsRateFound = false;
        boolean backwardsRateFound = false;
        for (Feature<?> feature : getFeatures()) {
            // any forwards rate constant
            if (feature instanceof ForwardsRateConstant) {
                forwardsRateFound = true;
            }
            // any backwards rate constant
            if (feature instanceof BackwardsRateConstant) {
                backwardsRateFound = true;
            }
        }
        if (!forwardsRateFound || !backwardsRateFound) {
            throw new FeatureUnassignableException("Required reaction rates unavailable.");
        }
    }

    private Quantity getScaledForwardsReactionRate() {
        if (forwardsReactionRate == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof ForwardsRateConstant) {
                    forwardsReactionRate = (RateConstant) feature;
                    break;
                }
            }
        }
        if (halfTime) {
            return forwardsReactionRate.getHalfScaledQuantity();
        }
        return forwardsReactionRate.getScaledQuantity();

    }

    private Quantity getScaledBackwardsReactionRate() {
        if (backwardsReactionRate == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof BackwardsRateConstant) {
                    backwardsReactionRate = (RateConstant) feature;
                    break;
                }
            }
        }
        if (halfTime) {
            return backwardsReactionRate.getHalfScaledQuantity();
        }
        return backwardsReactionRate.getScaledQuantity();
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getIdentifier() + " (" + getReactionString() + ")";
    }

    public interface BindeeSelection {
        BindeeSelection identifier(String identifier);

        BindeeSelection identifier(SimpleStringIdentifier identifier);

        BindeeSectionSelection of(ChemicalEntity bindee);

        BindeeSectionSelection of(ChemicalEntity bindee, RateConstant forwardsRateConstant);
    }

    public interface BindeeSectionSelection {
        BinderSelection in(CellSectionState bindeeSection);
    }

    public interface BinderSelection {
        BinderSectionSelection by(ChemicalEntity binder);

        BinderSectionSelection by(ChemicalEntity binder, RateConstant forwardsRateConstant);
    }

    public interface BinderSectionSelection {
        BuilderStep to(CellSectionState binderSection);
    }

    public interface BuilderStep {
        BuilderStep formingComplex(ComplexedChemicalEntity complex);

        ComplexBuildingReaction build();
    }

    public static class BindingBuilder implements BinderSelection, BinderSectionSelection, BindeeSelection, BindeeSectionSelection, BuilderStep {

        private ComplexBuildingReaction module;

        public BindingBuilder(Simulation simulation) {
            module = new ComplexBuildingReaction(simulation);
        }

        @Override
        public BindeeSelection identifier(String identifier) {
            return identifier(new SimpleStringIdentifier(identifier));
        }

        @Override
        public BindeeSelection identifier(SimpleStringIdentifier identifier) {
            module.setIdentifier(identifier);
            return this;
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity bindee) {
            module.bindee = bindee;
            return this;
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity binder, RateConstant forwardsRateConstant) {
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
        public BinderSectionSelection by(ChemicalEntity binder, RateConstant backwardsRateConstant) {
            module.setFeature(backwardsRateConstant);
            return by(binder);
        }

        @Override
        public BuilderStep to(CellSectionState bindeeSection) {
            module.binderCellSectionState = bindeeSection;
            return this;
        }

        @Override
        public BuilderStep formingComplex(ComplexedChemicalEntity complex) {
            module.complex = complex;
            return this;
        }

        @Override
        public ComplexBuildingReaction build() {
            module.initialize();
            return module;
        }
    }

}
