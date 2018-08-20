package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.sections.CellTopology;

/**
 * A {@code Reactant} encapsulates a {@link ChemicalEntity} for the use in {@link Reaction}s. This abstract class has
 * currently two implementations {@link StoichiometricReactant}s are Reactants that are consumed or produced during the
 * reaction. {@link CatalyticReactant}s are influencing the velocity (reaction rate), but are not consumed or produced.
 */
public abstract class Reactant {

    /**
     * The referenced entity.
     */
    private ChemicalEntity entity;

    /**
     * The role of the reactant.
     */
    private ReactantRole role;

    private CellTopology prefferedTopology;

    /**
     * Creates a new reactant.
     * @param entity The referenced entity.
     * @param role The reactants role.
     */
    Reactant(ChemicalEntity entity, ReactantRole role) {
        this.entity = entity;
        this.role = role;
        prefferedTopology = CellTopology.INNER;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, CellTopology prefferedTopology) {
        this.entity = entity;
        this.role = role;
        this.prefferedTopology = prefferedTopology;
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

    public CellTopology getPrefferedTopology() {
        return prefferedTopology;
    }

    public void setPrefferedTopology(CellTopology prefferedTopology) {
        this.prefferedTopology = prefferedTopology;
    }
}
