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
import de.bioforscher.singa.simulation.model.newsections.CellTopology;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNodeSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import javax.measure.Quantity;
import java.util.*;

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
    private CellTopology binderTopology;

    private ChemicalEntity bindee;
    private CellTopology bindeeTopology;

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
        double velocity = calculateVelocity(concentrationContainer);
        // change ligand concentration
        // deltas.add(new Delta(this, concentrationContainer.getSubsection(bindeeTopology), bindee, Quantities.getQuantity(-velocity, getConcentrationUnit())));
        // change unbound receptor concentration
        // deltas.add(new Delta(this, concentrationContainer.getSubsection(binderTopology), binder, Quantities.getQuantity(-velocity, getConcentrationUnit())));
        // change bound receptor concentration
        // deltas.add(new Delta(this, concentrationContainer.getSubsection(binderTopology), complex, Quantities.getQuantity(velocity, getConcentrationUnit())));
        return deltas;
    }

    private double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // get rates
        final double forwardsRateConstant = getScaledForwardsReactionRate().getValue().doubleValue();
        final double backwardsRateConstant = getScaledBackwardsReactionRate().getValue().doubleValue();
        // get concentrations
        final double bindeeConcentration = concentrationContainer.get(bindeeTopology, bindee).getValue().doubleValue();
        final double binderConcentration = concentrationContainer.get(binderTopology, binder).getValue().doubleValue();
        final double complexConcentration = concentrationContainer.get(bindeeTopology, complex).getValue().doubleValue();
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
        BinderSelection in(CellTopology bindeeTopology);
    }

    public interface BinderSelection {
        BinderSectionSelection by(ChemicalEntity binder);

        BinderSectionSelection by(ChemicalEntity binder, RateConstant forwardsRateConstant);
    }

    public interface BinderSectionSelection {
        BuilderStep to(CellTopology binderTopology);
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
        public BinderSelection in(CellTopology binderTopology) {
            module.bindeeTopology = binderTopology;
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
        public BuilderStep to(CellTopology binderTopology) {
            module.binderTopology = binderTopology;
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
