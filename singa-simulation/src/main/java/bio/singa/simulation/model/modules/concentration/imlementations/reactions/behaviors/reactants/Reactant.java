package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.sections.CellTopology;

import javax.measure.Unit;
import java.util.Objects;

/**
 * A {@code Reactant} encapsulates a {@link ChemicalEntity} for the use in {@link Reaction}s.
 * {@link ReactantRole#CATALYTIC} reactants influence the velocity (reaction rate), but are not consumed or produced.
 * {@link ReactantRole#SUBSTRATE}s or {@link ReactantRole#PRODUCT} are consumed or produced during the reaction. A
 * stoichiometric Reactant can be rate determining and has an associated stoichiometric number and/or reaction order. In
 * Reactions that are not elementary reactions, the reaction order is used to determine the velocity of a reaction. In
 * elementary reactions the stoichiometric number is used to this end.
 */
public class Reactant {

    /**
     * The referenced entity.
     */
    private ChemicalEntity entity;

    /**
     * The role of the reactant.
     */
    private ReactantRole role;

    /**
     * The number of molecules required for one reaction.
     */
    private double stoichiometricNumber = 1;

    /**
     * The reaction order of the reactant.
     */
    private double reactionOrder = 1;

    /**
     * The preferred topology of the reactant
     */
    private CellTopology preferredTopology = CellTopology.INNER;

    /**
     * The preferred concentration unit that is used for this reactant in the expression.
     */
    private Unit<MolarConcentration>  preferredConcentrationUnit;

    /**
     * Creates a new reactant.
     *
     * @param entity The referenced entity.
     * @param role The reactants role.
     */
    public Reactant(ChemicalEntity entity, ReactantRole role) {
        this.entity = entity;
        this.role = role;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, CellTopology preferredTopology) {
        this(entity, role);
        this.preferredTopology = preferredTopology;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, CellTopology preferredTopology, Unit<MolarConcentration> preferredConcentrationUnit) {
        this.entity = entity;
        this.role = role;
        this.preferredTopology = preferredTopology;
        this.preferredConcentrationUnit = preferredConcentrationUnit;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, double stoichiometricNumber) {
        this(entity, role);
        this.stoichiometricNumber = stoichiometricNumber;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, Unit<MolarConcentration>  preferredConcentrationUnit) {
        this(entity, role);
        this.preferredConcentrationUnit = preferredConcentrationUnit;
    }

    public Reactant(ChemicalEntity chemicalEntity, ReactantRole role, CellTopology topology, double stoichiometricNumber) {
        this(chemicalEntity, role, topology);
        this.stoichiometricNumber = stoichiometricNumber;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, CellTopology preferredTopology, double stoichiometricNumber, double reactionOrder) {
        this(entity, role, preferredTopology);
        this.stoichiometricNumber = stoichiometricNumber;
        this.reactionOrder = reactionOrder;
    }

    public Reactant(ChemicalEntity entity, ReactantRole role, double stoichiometricNumber, double reactionOrder) {
        this(entity, role, stoichiometricNumber);
        this.reactionOrder = reactionOrder;
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

    public CellTopology getPreferredTopology() {
        return preferredTopology;
    }

    public void setPreferredTopology(CellTopology preferredTopology) {
        this.preferredTopology = preferredTopology;
    }

    public Unit<MolarConcentration> getPreferredConcentrationUnit() {
        return preferredConcentrationUnit;
    }

    public void setPreferredConcentrationUnit(Unit<MolarConcentration> preferredConcentrationUnit) {
        this.preferredConcentrationUnit = preferredConcentrationUnit;
    }

    /**
     * Returns the stoichiometric number (the number of molecules required for one reaction.).
     *
     * @return the stoichiometric number.
     */
    public double getStoichiometricNumber() {
        return stoichiometricNumber;
    }

    /**
     * Returns the reaction order.
     *
     * @return The reaction order.
     */
    public double getReactionOrder() {
        return reactionOrder;
    }

    /**
     * Returns true if this reactant is a substrate.
     *
     * @return true if this reactant is a substrate.
     */
    public boolean isSubstrate() {
        return getRole() == ReactantRole.SUBSTRATE;
    }

    /**
     * Returns true if this reactant is a product.
     *
     * @return true if this reactant is a product.
     */
    public boolean isProduct() {
        return getRole() == ReactantRole.PRODUCT;
    }

    public boolean isCatalyst() {
        return getRole() == ReactantRole.CATALYTIC;
    }

    @Override
    public String toString() {
        return "Reactant "+getEntity()+" "+getPreferredTopology();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reactant reactant = (Reactant) o;
        return Objects.equals(entity, reactant.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }
}
