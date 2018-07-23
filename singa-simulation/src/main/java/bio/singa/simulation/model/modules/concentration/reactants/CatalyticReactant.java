package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;

/**
 * {@code CatalyticReactant}s are {@link Reactant}s that are not consumed or produced during Reactions, but
 * influence its velocity (reaction rate). An {@link ReactantRole#INCREASING} role is associated with an increase in
 * velocity. {@link ReactantRole#DECREASING} associates an inhibition of the reaction.
 */
public class CatalyticReactant extends Reactant {

    /**
     * The strength of the catalytic behaviour.
     */
    private double strength;

    /**
     * Creates a new catalytic reactant.
     * @param entity The referenced entity.
     * @param role The reactants role.
     */
    public CatalyticReactant(ChemicalEntity entity, ReactantRole role) {
        super(entity, role);
    }

    /**
     * Gets the strength of this Reactant.
     *
     * @return The strength of this Reactant.
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Sets the strength of this Reactant.
     *
     * @param strength The strength of this Reactant.
     */
    public void setStrength(double strength) {
        this.strength = strength;
    }

    /**
     * Returns {@code true} if this Reactant is increasing the velocity of the associated reaction, and {@code false}
     * otherwise.
     *
     * @return {@code true} if this Reactant is increasing the velocity of the associated reaction, and {@code false}
     * otherwise.
     */
    public boolean isAccelerator() {
        return getRole() == ReactantRole.INCREASING;
    }

    /**
     * Returns {@code true} if this Reactant is decreasing the velocity of the associated reaction, and {@code false}
     * otherwise.
     *
     * @return {@code true} if this Reactant is decreasing the velocity of the associated reaction, and {@code false}
     * otherwise.
     */
    public boolean isInhibitor() {
        return getRole() == ReactantRole.DECREASING;
    }
}
