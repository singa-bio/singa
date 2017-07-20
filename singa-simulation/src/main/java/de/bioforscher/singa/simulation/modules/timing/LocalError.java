package de.bioforscher.singa.simulation.modules.timing;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

/**
 * @author cl
 */
public class LocalError {

    public static final LocalError MINIMAL_EMPTY_ERROR = new LocalError(null, null, -Double.MAX_VALUE);

    private final BioNode node;
    private final ChemicalEntity<?> entity;
    private final double value;

    public LocalError(BioNode node, ChemicalEntity<?> entity, double value) {
        this.node = node;
        this.entity = entity;
        this.value = value;
    }

    public BioNode getNode() {
        return this.node;
    }

    public ChemicalEntity<?> getEntity() {
        return this.entity;
    }

    public double getValue() {
        return this.value;
    }

}
