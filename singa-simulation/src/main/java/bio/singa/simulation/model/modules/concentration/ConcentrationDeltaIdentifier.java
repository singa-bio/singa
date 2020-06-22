package bio.singa.simulation.model.modules.concentration;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.Objects;

/**
 * Used to identify changes to concentrations ({@link ConcentrationDelta}s) (mostly in maps).
 *
 * @author cl
 */
public class ConcentrationDeltaIdentifier {

    /**
     * The object the delta is assigned to.
     */
    private final Updatable updatable;

    /**
     * The cell section the delta is assigned to.
     */
    private final CellSubsection section;

    private final String reference;

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
    public ConcentrationDeltaIdentifier(Updatable updatable, CellSubsection section, ChemicalEntity entity) {
        this(updatable, section, "", entity);
    }

    public ConcentrationDeltaIdentifier(Updatable updatable, CellSubsection section, String reference, ChemicalEntity entity) {
        this.updatable = updatable;
        this.section = section;
        this.reference = reference;
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
    public CellSubsection getSubsection() {
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

    public String getReference() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcentrationDeltaIdentifier that = (ConcentrationDeltaIdentifier) o;
        return Objects.equals(updatable, that.updatable) &&
                Objects.equals(section, that.section) &&
                Objects.equals(reference, that.reference) &&
                Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updatable, section, reference, entity);
    }

    @Override
    public String toString() {
        return updatable.getStringIdentifier() + "-" + section.getIdentifier() + "-" + entity.getIdentifier();
    }

}
