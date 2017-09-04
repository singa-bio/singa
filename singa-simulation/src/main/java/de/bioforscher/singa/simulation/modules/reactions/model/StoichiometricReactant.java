package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;

/**
 * {@code StoichiometricReactant}s are {@link Reactant}s that are consumed or produced during the reaction. A
 * StoichiometricReactant can be rate determining and has an associated stoichiometric number and/or reaction order. In
 * Reactions that are not elementary reactions, the reaction order is used to determine the velocity of a reaction. In
 * elementary reactions the stoichiometric number is used to this end.
 */
public class StoichiometricReactant extends Reactant {

    private double stoichiometricNumber;
    private double reactionOrder;
    private boolean rateDetermining;

    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role, double stoichiometricNumber, double reactionOrder) {
        this(entity, role, stoichiometricNumber);
        this.reactionOrder = reactionOrder;
    }

    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role, double stoichiometricNumber) {
        this(entity, role);
        this.stoichiometricNumber = stoichiometricNumber;
    }

    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role, boolean rateDetermining) {
        this(entity, role);
        this.rateDetermining = rateDetermining;
    }

    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role) {
        super(entity, role);
        this.stoichiometricNumber = 1;
        this.reactionOrder = 1;
    }

    public double getStoichiometricNumber() {
        return this.stoichiometricNumber;
    }

    public void setStoichiometricNumber(double stoichiometricNumber) {
        this.stoichiometricNumber = stoichiometricNumber;
    }

    public double getReactionOrder() {
        return this.reactionOrder;
    }

    public void setReactionOrder(double reactionOrder) {
        this.reactionOrder = reactionOrder;
    }

    public boolean isRateDetermining() {
        return this.rateDetermining;
    }

    public void setRateDetermining(boolean rateDetermining) {
        this.rateDetermining = rateDetermining;
    }

    public boolean isSubstrate() {
        return getRole() == ReactantRole.DECREASING;
    }

    public boolean isProduct() {
        return getRole() == ReactantRole.INCREASING;
    }


}
