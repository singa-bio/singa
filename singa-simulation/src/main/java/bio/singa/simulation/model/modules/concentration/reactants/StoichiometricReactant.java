package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellTopology;

/**
 * {@code StoichiometricReactant}s are {@link Reactant}s that are consumed or produced during the reaction. A
 * StoichiometricReactant can be rate determining and has an associated stoichiometric number and/or reaction order. In
 * Reactions that are not elementary reactions, the reaction order is used to determine the velocity of a reaction. In
 * elementary reactions the stoichiometric number is used to this end.
 */
public class StoichiometricReactant extends Reactant {

    /**
     * The number of molecules required for one reaction.
     */
    private double stoichiometricNumber;

    /**
     * The reaction order of the reactant.
     */
    private double reactionOrder;

    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role, CellTopology prefferedTopology, double stoichiometricNumber, double reactionOrder) {
        super(entity, role, prefferedTopology);
        this.stoichiometricNumber = stoichiometricNumber;
        this.reactionOrder = reactionOrder;
    }

    /**
     * Creates a new {@link StoichiometricReactant}.
     * @param entity The referenced entity.
     * @param role The reactant role.
     * @param stoichiometricNumber The number of molecules required for one reaction.
     * @param reactionOrder The reaction order of the reactant.
     */
    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role, double stoichiometricNumber, double reactionOrder) {
        this(entity, role, stoichiometricNumber);
        this.reactionOrder = reactionOrder;
    }

    /**
     * Creates a new {@link StoichiometricReactant}. Reaction order is set to 1.
     * @param entity The referenced entity.
     * @param role The reactant role.
     * @param stoichiometricNumber The number of molecules required for one reaction.
     */
    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role, double stoichiometricNumber) {
        this(entity, role);
        this.stoichiometricNumber = stoichiometricNumber;
    }

    /**
     * Creates a new {@link StoichiometricReactant}. Reaction order and spectrometric number are set to 1 .
     * @param entity The referenced entity.
     * @param role The reactant role.
     */
    public StoichiometricReactant(ChemicalEntity entity, ReactantRole role) {
        super(entity, role);
        stoichiometricNumber = 1;
        reactionOrder = 1;
    }

    public StoichiometricReactant(ChemicalEntity chemicalEntity, ReactantRole role, CellTopology topology) {
        super(chemicalEntity, role, topology);
        stoichiometricNumber = 1;
        reactionOrder = 1;
    }

    public StoichiometricReactant(ChemicalEntity chemicalEntity, ReactantRole role, CellTopology topology, double stoichiometricNumber) {
        super(chemicalEntity, role, topology);
        this.stoichiometricNumber = stoichiometricNumber;
    }

    /**
     * Returns the stoichiometric number (the number of molecules required for one reaction.).
     * @return the stoichiometric number.
     */
    public double getStoichiometricNumber() {
        return stoichiometricNumber;
    }

    /**
     * Returns the reaction order.
     * @return The reaction order.
     */
    public double getReactionOrder() {
        return reactionOrder;
    }

    /**
     * Returns true if this reactant is a substrate.
     * @return true if this reactant is a substrate.
     */
    public boolean isSubstrate() {
        return getRole() == ReactantRole.DECREASING;
    }

    /**
     * Returns true if this reactant is a product.
     * @return true if this reactant is a product.
     */
    public boolean isProduct() {
        return getRole() == ReactantRole.INCREASING;
    }

}
