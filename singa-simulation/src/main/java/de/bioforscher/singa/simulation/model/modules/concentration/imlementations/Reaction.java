package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.simulation.model.modules.concentration.reactants.ReactantRole.DECREASING;
import static de.bioforscher.singa.simulation.model.modules.concentration.reactants.ReactantRole.INCREASING;

/**
 * @author cl
 */
public abstract class Reaction extends ConcentrationBasedModule<SectionDeltaFunction> {

    /**
     * The stoichiometric reactants.
     */
    protected List<StoichiometricReactant> stoichiometricReactants;

    /**
     * True if this reaction is an elementary reaction.
     */
    private boolean elementary;

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
                    product *= concentrationContainer.get(supplier.getCurrentSubsection(), reactant.getEntity()).getValue().doubleValue();
                } else {
                    product *= Math.pow(concentrationContainer.get(supplier.getCurrentSubsection(), reactant.getEntity()).getValue().doubleValue(),
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
    protected List<ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        List<ConcentrationDelta> deltas = new ArrayList<>();
        double velocity = calculateVelocity(concentrationContainer);
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            double deltaValue;
            if (reactant.isSubstrate()) {
                deltaValue = -velocity * reactant.getStoichiometricNumber();
            } else {
                deltaValue = velocity * reactant.getStoichiometricNumber();

            }
            deltas.add(new ConcentrationDelta(this, supplier.getCurrentSubsection(), reactant.getEntity(), Quantities.getQuantity(deltaValue, Environment.getConcentrationUnit())));
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

    public abstract void initialize();

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
        return substrates + " \u27f6 " + products;
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
                "  " + "primary identifier: " + getIdentifier() + System.lineSeparator() +
                "  " + "reaction: " + getReactionString() + System.lineSeparator() +
                "  " + "features: " + System.lineSeparator() +
                listFeatures("    ");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getIdentifier() + " (" + getReactionString() + ")";
    }

    public static abstract class Builder<TopLevelType extends Reaction, BuilderType extends Builder> {

        protected final TopLevelType topLevelObject;
        protected final BuilderType builderObject;

        public Builder(Simulation simulation) {
            topLevelObject = createObject(simulation);
            topLevelObject.setSimulation(simulation);
            topLevelObject.stoichiometricReactants = new ArrayList<>();
            builderObject = getBuilder();
        }

        protected abstract TopLevelType createObject(Simulation primaryIdentifer);

        protected abstract BuilderType getBuilder();

        public BuilderType identifier(String identifier) {
            topLevelObject.setIdentifier(identifier);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, DECREASING));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, DECREASING, stoichiometricNumber));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber, double reactionOrder) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, DECREASING, stoichiometricNumber, reactionOrder));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addProduct(ChemicalEntity chemicalEntity) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, INCREASING));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, INCREASING, stoichiometricNumber));
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
            topLevelObject.initialize();
            return topLevelObject;
        }

    }

}
