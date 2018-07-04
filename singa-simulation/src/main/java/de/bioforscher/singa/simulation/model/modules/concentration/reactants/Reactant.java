package de.bioforscher.singa.simulation.model.modules.concentration.reactants;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;

/**
 * A {@code Reactant} encapsulates a {@link ChemicalEntity} for the use in {@link Reaction}s. This abstract class has
 * currently two implementations {@link StoichiometricReactant}s are Reactants that are consumed or produced during the
 * reaction. {@link CatalyticReactant}s are influencing the velocity (reaction rate), but are not consumed or produced.
 */
public abstract class Reactant {

    private ChemicalEntity entity;
    private ReactantRole role;

    public Reactant(ChemicalEntity entity, ReactantRole role) {
        this.entity = entity;
        this.role = role;
    }

    /**
     * Gets the entity representing this reactant.
     *
     * @return The entity representing this reactant.
     */
    public ChemicalEntity getEntity() {
        return entity;
    }

    /**
     * Sets the entity representing this reactant.
     *
     * @param entity The entity representing this reactant.
     */
    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    /**
     * Gets the role of this reactant.
     *
     * @return The role of this reactant.
     */
    public ReactantRole getRole() {
        return role;
    }

    /**
     * Gets the role of this reactant.
     *
     * @param role The role of this reactant.
     */
    public void setRole(ReactantRole role) {
        this.role = role;
    }


}
