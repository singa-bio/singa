package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.ChemistryFeatureContainer;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractSectionSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A chemical reaction is a process that leads to the transformation of one set of chemical substances to another.
 * The {@link ChemicalEntity ChemicalEntity}s used in a Reaction are encapsulated to {@link Reactant}s to define
 * stoichiometry and {@link ReactantRole}. The implementations of Reaction have to specify how to calculate the actual
 * acceleration of a reaction.
 *
 * @author cl
 */
public abstract class Reaction extends AbstractSectionSpecificModule implements Featureable {

    /**
     * The features available for automatic annotation and assignment.
     */
    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    /**
     * The stoichiometric reactants.
     */
    private List<StoichiometricReactant> stoichiometricReactants;

    /**
     * True if this reaction is an elementary reaction.
     */
    private boolean elementary;

    /**
     * The features of the reaction.
     */
    private FeatureContainer features;

    /**
     * Creates a new reaction for a simulation.
     *
     * @param simulation The simulations
     */
    protected Reaction(Simulation simulation) {
        super(simulation);
        stoichiometricReactants = new ArrayList<>();
        features = new ChemistryFeatureContainer();
    }

    /**
     * Returns the list of reactants for this reaction.
     *
     * @return The list of reactants for this reaction.
     */
    public List<StoichiometricReactant> getStoichiometricReactants() {
        return stoichiometricReactants;
    }

    /**
     * Sets the list of reactants for this reaction.
     *
     * @param stoichiometricReactants The list of reactants for this reaction.
     */
    public void setStoichiometricReactants(List<StoichiometricReactant> stoichiometricReactants) {
        this.stoichiometricReactants = stoichiometricReactants;
    }

    public void addStochiometricReactant(StoichiometricReactant stoichiometricReactant) {
        stoichiometricReactants.add(stoichiometricReactant);
    }

    /**
     * Returns all substrates of this reaction.
     *
     * @return All substrates of this reaction.
     */
    public List<ChemicalEntity> getSubstrates() {
        return stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isSubstrate)
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all products of this reaction.
     *
     * @return All products of this reaction.
     */
    public List<ChemicalEntity> getProducts() {
        return stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isProduct)
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    /**
     * Determines the concentration of reactants that influence the velocity of the reaction.
     *
     * @param concentrationContainer The container, where the concentrations are collected.
     * @param role The role that is to be summarized ({@link ReactantRole#INCREASING} for Products and {@link
     * ReactantRole#DECREASING} for Substrates).
     * @return The total concentration.
     */
    protected double determineEffectiveConcentration(ConcentrationContainer concentrationContainer, ReactantRole role) {
        double product = 1.0;
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            if (reactant.getRole() == role) {
                if (isElementary()) {
                    product *= concentrationContainer.get(getCurrentCellSection(), reactant.getEntity()).getValue().doubleValue();
                } else {
                    product *= Math.pow(concentrationContainer.get(getCurrentCellSection(), reactant.getEntity()).getValue().doubleValue(),
                            reactant.getReactionOrder());
                }
            }
        }
        return product;
    }

    /**
     * Calculates all deltas for all reactants for the reaction.
     *
     * @param concentrationContainer The concentration container to calculate the deltas for.
     * @return The calculated deltas.
     */
    protected List<Delta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();
        double velocity = calculateVelocity(concentrationContainer);
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            double deltaValue;
            if (reactant.isSubstrate()) {
                deltaValue = -velocity * reactant.getStoichiometricNumber();
            } else {
                deltaValue = velocity * reactant.getStoichiometricNumber();

            }
            deltas.add(new Delta(this, getCurrentCellSection(), reactant.getEntity(), Quantities.getQuantity(deltaValue, Environment.getConcentrationUnit())));
        }
        return deltas;
    }

    /**
     * Calculates the reaction velocity, depending on the kinetic law.
     *
     * @param concentrationContainer The concentration container to calculate the deltas for.
     * @return The reaction velocity.
     */
    public abstract double calculateVelocity(ConcentrationContainer concentrationContainer);

    /**
     * Returns {@code true} if this Reaction is considered elementary and {@code false} otherwise.
     *
     * @return {@code true} if this Reaction is considered elementary and {@code false} otherwise.
     */
    public boolean isElementary() {
        return elementary;
    }

    /**
     * Sets this Reaction as elementary.
     *
     * @param elementary {@code true} if this Reaction is elementary and {@code false} otherwise.
     */
    public void setElementary(boolean elementary) {
        this.elementary = elementary;
    }

    /**
     * Returns a nicely formatted string representation of the reaction.
     *
     * @return A nicely formatted string representation of the reaction.
     */
    public String getReactionString() {
        String substrates = collectSubstrateString();
        String products = collectProductsString();
        if (substrates.length() > 1 && Character.isWhitespace(substrates.charAt(0))) {
            substrates = substrates.substring(1);
        }
        return substrates + " \u27f6" + products;
    }

    protected String collectSubstrateString() {
        return stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isSubstrate)
                .map(substrate -> (substrate.getStoichiometricNumber() > 1 ? substrate.getStoichiometricNumber() : "") + " "
                        + substrate.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
    }

    protected String collectProductsString() {
        return stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isProduct)
                .map(product -> (product.getStoichiometricNumber() > 1 ? product.getStoichiometricNumber() : "") + " "
                        + product.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
    }

    public String getStringForProtocol() {
        return getClass().getSimpleName() + " summary:" + System.lineSeparator() +
                "  " + "primary identifier: " + getIdentifier().getIdentifier() + System.lineSeparator() +
                "  " + "reaction: " + getReactionString() + System.lineSeparator() +
                "  " + "features: " + System.lineSeparator() +
                features.listFeatures("    ");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getIdentifier() + " (" + getReactionString() + ")";
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
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public static abstract class Builder<TopLevelType extends Reaction, BuilderType extends Builder> {

        protected final TopLevelType topLevelObject;
        protected final BuilderType builderObject;

        public Builder(Simulation simulation) {
            topLevelObject = createObject(simulation);
            builderObject = getBuilder();
        }

        protected abstract TopLevelType createObject(Simulation primaryIdentifer);

        protected abstract BuilderType getBuilder();

        public BuilderType identifier(SimpleStringIdentifier identifier) {
            topLevelObject.setIdentifier(identifier);
            return builderObject;
        }

        public BuilderType identifier(String identifier) {
            topLevelObject.setIdentifier(new SimpleStringIdentifier(identifier));
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, ReactantRole.DECREASING));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, ReactantRole.DECREASING, stoichiometricNumber));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber, double reactionOrder) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, ReactantRole.DECREASING, stoichiometricNumber, reactionOrder));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addProduct(ChemicalEntity chemicalEntity) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, ReactantRole.INCREASING));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, ReactantRole.INCREASING, stoichiometricNumber));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType setNonElementary() {
            topLevelObject.setElementary(false);
            return builderObject;
        }

        public TopLevelType build() {
            if (!topLevelObject.isElementary()) {
                topLevelObject.setElementary(true);
            }
            topLevelObject.addModuleToSimulation();
            return topLevelObject;
        }

    }

}
