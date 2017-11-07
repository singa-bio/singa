package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * @author cl
 */
public class LocalError {

    public static final LocalError MINIMAL_EMPTY_ERROR = new LocalError(null, null, -Double.MAX_VALUE);

    private final AutomatonNode node;
    private final ChemicalEntity<?> entity;
    private final double value;

    public LocalError(AutomatonNode node, ChemicalEntity<?> entity, double value) {
        this.node = node;
        this.entity = entity;
        this.value = value;
    }

    public AutomatonNode getNode() {
        return node;
    }

    public ChemicalEntity<?> getEntity() {
        return entity;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LocalError{" +
                "node=" + node +
                ", entity=" + entity.getIdentifier() +
                ", value=" + value +
                '}';
    }
}
