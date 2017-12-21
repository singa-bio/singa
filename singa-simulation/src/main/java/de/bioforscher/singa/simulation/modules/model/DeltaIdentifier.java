package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * @author cl
 */
class DeltaIdentifier {

    private final AutomatonNode node;
    private final CellSection section;
    private final ChemicalEntity<?> entity;

    public DeltaIdentifier(AutomatonNode node, CellSection section, ChemicalEntity<?> entity) {
        this.node = node;
        this.section = section;
        this.entity = entity;
    }

    public AutomatonNode getNode() {
        return node;
    }

    public CellSection getSection() {
        return section;
    }

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
}
