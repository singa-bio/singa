package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.modules.concentration.reactants.CatalyticReactant;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.DECREASING;
import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.INCREASING;

/**
 * Reactions in general are {@link ConcentrationBasedModule}s following the laws of chemical kinetics. The calculation
 * of the velocity is defined by the concrete implementation of this module. In general {@link Reactant} are defined
 * as {@link StoichiometricReactant} that are consumed or produced during the reaction or {@link CatalyticReactant}
 * that are used to modify the reaction speed but whose concentration is not altered.
 *
 * @author cl
 * @see ReversibleReaction
 * @see MichaelisMentenReaction
 * @see NthOrderReaction
 */
public abstract class Reaction extends ConcentrationBasedModule<SectionDeltaFunction> {

    /**
     * The stoichiometric reactants.
     */
    List<StoichiometricReactant> substrates;

    List<StoichiometricReactant> products;

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
        ArrayList<StoichiometricReactant> completeList = new ArrayList<>();
        completeList.addAll(substrates);
        completeList.addAll(products);
        return completeList;
    }

    public void addStochiometricReactant(StoichiometricReactant stoichiometricReactant) {
        if (stoichiometricReactant.isSubstrate()) {
            substrates.add(stoichiometricReactant);
        } else {
            products.add(stoichiometricReactant);
        }
    }

    public boolean substratesAvailable(Updatable updatable) {
        return updatable.getConcentrationContainer().getReferencedEntities().containsAll(getSubstrateEntities());
    }

    public List<StoichiometricReactant> getSubstrates() {
        return substrates;
    }

    public List<StoichiometricReactant> getProducts() {
        return products;
    }

    /**
     * Returns all substrates of this reaction.
     *
     * @return All substrates of this reaction.
     */
    public List<ChemicalEntity> getSubstrateEntities() {
        return substrates.stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all products of this reaction.
     *
     * @return All products of this reaction.
     */
    public List<ChemicalEntity> getProductEntities() {
        return products.stream()
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
    double determineEffectiveConcentration(ConcentrationContainer concentrationContainer, ReactantRole role) {
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
        for (StoichiometricReactant substrate : substrates) {
            double deltaValue = -velocity * substrate.getStoichiometricNumber();
            deltas.add(new ConcentrationDelta(this, supplier.getCurrentSubsection(), substrate.getEntity(), Quantities.getQuantity(deltaValue, Environment.getConcentrationUnit())));
        }
        for (StoichiometricReactant product : products) {
            double deltaValue = velocity * product.getStoichiometricNumber();
            deltas.add(new ConcentrationDelta(this, supplier.getCurrentSubsection(), product.getEntity(), Quantities.getQuantity(deltaValue, Environment.getConcentrationUnit())));
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
        return substrates.stream()
                .map(substrate -> (substrate.getStoichiometricNumber() > 1 ? substrate.getStoichiometricNumber() : "") + " "
                        + substrate.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
    }

    protected String collectProductsString() {
        return products.stream()
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
        return getClass().getSimpleName() + ": " + (getIdentifier() == null ? "" : getIdentifier()) + " (" + getReactionString() + ")";
    }

    public static abstract class Builder<TopLevelType extends Reaction, BuilderType extends Builder> {

        final TopLevelType topLevelObject;
        final BuilderType builderObject;

        public Builder(Simulation simulation) {
            topLevelObject = createObject(simulation);
            topLevelObject.setSimulation(simulation);
            topLevelObject.substrates = new ArrayList<>();
            topLevelObject.products = new ArrayList<>();
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

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, DECREASING, topology));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, DECREASING, stoichiometricNumber));
            topLevelObject.addReferencedEntity(chemicalEntity);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            topLevelObject.addStochiometricReactant(new StoichiometricReactant(chemicalEntity, DECREASING, topology, stoichiometricNumber));
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
