package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;

/**
 * Used to identify changes to concentrations ({@link Delta}s) in maps.
 *
 * @author cl
 */
class DeltaIdentifier {

    /**
     * The object the delta is assigned to.
     */
    private final Updatable updatable;

    /**
     * The cell section the delta is assigned to.
     */
    private final CellSection section;

    /**
     * The chemical entity the delta is assigned to.
     */
    private final ChemicalEntity entity;

    /**
     * Creates a new DeltaIdentifier.
     *
     * @param updatable The object the delta is assigned to.
     * @param section The cell section the delta is assigned to.
     * @param entity The entity the delta is assigned to.
     */
    DeltaIdentifier(Updatable updatable, CellSection section, ChemicalEntity entity) {
        this.updatable = updatable;
        this.section = section;
        this.entity = entity;
    }

    /**
     * Returns the object the delta is assigned to.
     *
     * @return The object the delta is assigned to.
     */
    public Updatable getUpdatable() {
        return updatable;
    }

    /**
     * Returns the cell section the delta is assigned to.
     *
     * @return The cell section the delta is assigned to.
     */
    public CellSection getSection() {
        return section;
    }

    /**
     * Returns the chemical entity the delta is assigned to.
     *
     * @return The chemical entity the delta is assigned to.
     */
    public ChemicalEntity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeltaIdentifier that = (DeltaIdentifier) o;

        if (updatable != null ? !updatable.equals(that.updatable) : that.updatable != null) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) return false;
        return entity != null ? entity.equals(that.entity) : that.entity == null;
    }

    @Override
    public int hashCode() {
        int result = updatable != null ? updatable.hashCode() : 0;
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return updatable.getStringIdentifier() + "-" + section.getIdentifier() + "-" + entity.getIdentifier();
    }

}
