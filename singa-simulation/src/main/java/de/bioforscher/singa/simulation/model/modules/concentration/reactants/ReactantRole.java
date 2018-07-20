package de.bioforscher.singa.simulation.model.modules.concentration.reactants;

/**
 * The role of the reactant: Increasing, for products and accelerating reactants and decreasing for substrates and
 * inhibiting reactants.
 *
 * @author cl
 */
public enum ReactantRole {

    /**
     * Used for products and accelerating reactants.
     */
    INCREASING,

    /**
     * Used for substrates and inhibiting reactants.
     */
    DECREASING

}
