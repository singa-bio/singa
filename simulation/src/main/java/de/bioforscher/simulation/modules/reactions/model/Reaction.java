package de.bioforscher.simulation.modules.reactions.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;

/**
 * A chemical reaction is a process that leads to the transformation of one set of chemical substances to another.
 * The {@link de.bioforscher.chemistry.descriptive.ChemicalEntity ChemicalEntity}s used in a Reaction are
 * encapsulated to {@link Reactant}s to define stoichiometry and {@link ReactantRole}. The implementations of
 * Reaction have to specify how to calculate the actual acceleration of a reaction.
 */
public abstract class Reaction {

    private List<StoichiometricReactant> stoichiometricReactants;
    private boolean elementary;

    public Reaction() {
        this.stoichiometricReactants = new ArrayList<>();
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
     * @param node The node, where the concentrations are collected.
     * @param role The role that is to be summarized ({@link ReactantRole#INCREASING} for Products and {@link
     * ReactantRole#DECREASING} for Substrates).
     * @return The total concentration.
     */
    protected Quantity<MolarConcentration> determineConcentration(BioNode node, ReactantRole role) {
        double product = 1.0;
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            if (reactant.getRole() == role) {
                if (isElementary()) {
                    product *= node.getConcentration(reactant.getEntity()).getValue().doubleValue();
                } else {
                    product *= Math.pow(node.getConcentration(reactant.getEntity()).getValue().doubleValue(),
                            reactant.getReactionOrder());
                }
            }
        }
        return Quantities.getQuantity(product, MOLE_PER_LITRE);
    }

    /**
     * Determines the actual acceleration of concentration that would result in a change in concentration if the
     * reaction is occurring in the given {@link BioNode}.
     *
     * @param node The node containing the concentrations of species subject to this reaction.
     * @return The acceleration of concentration (change in reaction rate for the species of this reaction)
     */
    public abstract Quantity<ReactionRate> calculateAcceleration(BioNode node);

    public abstract Set<ChemicalEntity> collectAllReferencedEntities();
    /**
     * Returns {@code true} if this Reaction is considered elementary and {@code false} otherwise.
     *
     * @return
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
        final DecimalFormat format = new DecimalFormat("#.##");
        String substrates = this.stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isSubstrate)
                .map(substrate -> format.format(substrate.getStoichiometricNumber()) + " "
                        + substrate.getEntity().getName())
                .collect(Collectors.joining(" + "));

        String products = this.stoichiometricReactants.stream()
                .filter(StoichiometricReactant::isProduct)
                .map(substrate -> format.format(substrate.getStoichiometricNumber()) + " "
                        + substrate.getEntity().getName())
                .collect(Collectors.joining(" + "));

        return substrates + " \u27f6 " + products;


    }


}
