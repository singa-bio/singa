package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * The LocalError object stores the error as an result of the currently applied time step. Additionally it stores in
 * which node and for which chemical entity it occurred.
 *
 * The local error is calculated according to the midpoint method:
 * E = abs(1 - (fullDelta / 2.0 * halfDelta))
 *
 * @author cl
 */
public class LocalError {

    /**
     * The minimal possible error not assigned to any node or chemical entity.
     */
    public static final LocalError MINIMAL_EMPTY_ERROR = new LocalError(null, null, -Double.MAX_VALUE);

    /**
     * The node where the error occurred.
     */
    private final AutomatonNode node;

    /**
     * The chemical entity where the error occurred.
     */
    private final ChemicalEntity<?> entity;

    /**
     * The actual value of the error.
     */
    private final double value;

    /**
     * Creates a new LocalError.
     *
     * @param node The node where the error occurred.
     * @param entity The chemical entity where the error occurred.
     * @param value The actual value of the error.
     */
    LocalError(AutomatonNode node, ChemicalEntity<?> entity, double value) {
        this.node = node;
        this.entity = entity;
        this.value = value;
    }

    /**
     * Returns the node where the error occurred.
     *
     * @return The node where the error occurred.
     */
    public AutomatonNode getNode() {
        return node;
    }

    /**
     * Returns the chemical entity where the error occurred.
     *
     * @return The chemical entity where the error occurred.
     */
    public ChemicalEntity<?> getChemicalEntity() {
        return entity;
    }

    /**
     * Returns the actual value of the error.
     *
     * @return The actual value of the error.
     */
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "E("+node.getIdentifier()+","+entity.getIdentifier()+") = "+value;
    }
}
