package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * Used to identify changes to concentrations ({@link Delta}s) in maps.
 *
 * @author cl
 */
class DeltaIdentifier {

    /**
     * The node the delta is assigned to.
     */
    private final AutomatonNode node;

    /**
     * The cell section the delta is assigned to.
     */
    private final CellSection section;

    /**
     * The chemical entity the delta is assigned to.
     */
    private final ChemicalEntity<?> entity;

    /**
     * Creates a new DeltaIdentifier.
     *
     * @param node The node the delta is assigned to.
     * @param section The cell section the delta is assigned to.
     * @param entity The entity the delta is assigned to.
     */
    DeltaIdentifier(AutomatonNode node, CellSection section, ChemicalEntity<?> entity) {
        this.node = node;
        this.section = section;
        this.entity = entity;
    }

    /**
     * Returns the node the delta is assigned to.
     *
     * @return The node the delta is assigned to.
     */
    public AutomatonNode getNode() {
        return node;
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
    public ChemicalEntity<?> getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeltaIdentifier that = (DeltaIdentifier) o;

        if (node != null ? !node.equals(that.node) : that.node != null) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) return false;
        return entity != null ? entity.equals(that.entity) : that.entity == null;
    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return node.getIdentifier() + "-" + section.getIdentifier() + "-" + entity.getIdentifier();
    }

}
