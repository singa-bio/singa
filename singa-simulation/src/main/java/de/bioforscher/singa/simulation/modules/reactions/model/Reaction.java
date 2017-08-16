package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.modules.model.AbstractSectionSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.units.ri.quantity.Quantities;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * A chemical reaction is a process that leads to the transformation of one set of chemical substances to another.
 * The {@link ChemicalEntity ChemicalEntity}s used in a Reaction are
 * encapsulated to {@link Reactant}s to define stoichiometry and {@link ReactantRole}. The implementations of
 * Reaction have to specify how to calculate the actual acceleration of a reaction.
 */
public abstract class Reaction extends AbstractSectionSpecificModule implements Featureable {

    private List<StoichiometricReactant> stoichiometricReactants;
    private boolean elementary;

    private FeatureContainer features;
    protected final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    public Reaction(Simulation simulation) {
        super(simulation);
        initialize();
    }

    public Reaction() {
        super();
        initialize();
    }

    private void initialize() {
        this.stoichiometricReactants = new ArrayList<>();
        this.features = new FeatureContainer();
    }

    /**
     * Returns the list of reactants for this reaction.
     *
     * @return The list of reactants for this reaction.
     */
    public List<StoichiometricReactant> getStoichiometricReactants() {
        return this.stoichiometricReactants;
    }

    /**
     * Sets the list of reactants for this reaction.
     *
     * @param stoichiometricReactants The list of reactants for this reaction.
     */
    public void setStoichiometricReactants(List<StoichiometricReactant> stoichiometricReactants) {
        this.stoichiometricReactants = stoichiometricReactants;
    }

    public List<ChemicalEntity> getSubstrates() {
        return this.stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isSubstrate)
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    public List<ChemicalEntity> getProducts() {
        return this.stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isProduct)
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    /**
     * Determines the concentration of reactants that influence the velocity of the reaction.
     *
     * @param concentrationContainer The container, where the concentrations are collected.
     * @param role The role that is to be summarized ({@link ReactantRole#INCREASING} for Products and {@link
     *             ReactantRole#DECREASING} for Substrates).
     * @return The total concentration.
     */
    protected double determineConcentration(ConcentrationContainer concentrationContainer, ReactantRole role) {
        double product = 1.0;
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            if (reactant.getRole() == role) {
                if (isElementary()) {
                    product *= concentrationContainer.getAvailableConcentration(getCurrentCellSection(), reactant.getEntity()).getValue().doubleValue();
                } else {
                    product *= Math.pow(concentrationContainer.getAvailableConcentration(getCurrentCellSection(), reactant.getEntity()).getValue().doubleValue(),
                            reactant.getReactionOrder());
                }
            }
        }
        return product;
    }


    public List<Delta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();
        double velocity = calculateVelocity(concentrationContainer);
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            double deltaValue;
            if (reactant.isSubstrate()) {
                deltaValue = -velocity * reactant.getStoichiometricNumber();
            } else {
                deltaValue = velocity * reactant.getStoichiometricNumber();

            }
            deltas.add(new Delta(getCurrentCellSection(), reactant.getEntity(), Quantities.getQuantity(deltaValue, MOLE_PER_LITRE)));
        }
        return deltas;
    }

    public abstract double calculateVelocity(ConcentrationContainer concentrationContainer);

    /**
     * Returns {@code true} if this Reaction is considered elementary and {@code false} otherwise.
     *
     * @return {@code true} if this Reaction is considered elementary and {@code false} otherwise.
     */
    public boolean isElementary() {
        return this.elementary;
    }

    /**
     * Sets this Reaction as elementary.
     *
     * @param elementary {@code true} if this Reaction is elementary and {@code false} otherwise.
     */
    public void setElementary(boolean elementary) {
        this.elementary = elementary;
    }

    public String getDisplayString() {
        String substrates = this.stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isSubstrate)
                .map(substrate -> (substrate.getStoichiometricNumber() > 1 ? substrate.getStoichiometricNumber() : "") + " "
                        + substrate.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
        String products = this.stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isProduct)
                .map(product -> (product.getStoichiometricNumber() > 1 ? product.getStoichiometricNumber() : "") + " "
                        + product.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
        return substrates + " \u27f6" + products;
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return this.features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return this.features.getFeature(featureTypeClass);
    }

    protected <FeatureContent> FeatureContent getScaledFeature(Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = getFeature(featureClass);
        if (this.halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        this.features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        this.features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return this.features.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return this.availableFeatures;
    }

}
