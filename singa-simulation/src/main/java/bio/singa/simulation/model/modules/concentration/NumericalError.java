package bio.singa.simulation.model.modules.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.simulation.Updatable;

/**
 * The LocalError object stores the error as an result of the currently applied time step. Additionally it stores in
 * which node and for which chemical entity it occurred.
 *
 * @author cl
 */
public class NumericalError {

    /**
     * The minimal possible error not assigned to any node or chemical entity.
     */
    public static final NumericalError MINIMAL_EMPTY_ERROR = new NumericalError(null, null, -Double.MAX_VALUE);

    /**
     * The node where the error occurred.
     */
    private Updatable updatable;

    /**
     * The chemical entity where the error occurred.
     */
    private final ChemicalEntity entity;

    /**
     * The actual value of the error.
     */
    private final double value;

    /**
     * Creates a new LocalError.
     *
     * @param updatable The node where the error occurred.
     * @param entity The chemical entity where the error occurred.
     * @param value The actual value of the error.
     */
    public NumericalError(Updatable updatable, ChemicalEntity entity, double value) {
        this.updatable = updatable;
        this.entity = entity;
        this.value = value;
    }

    public void setUpdatable(Updatable updatable) {
        this.updatable = updatable;
    }

    /**
     * Returns the node where the error occurred.
     *
     * @return The node where the error occurred.
     */
    public Updatable getUpdatable() {
        return updatable;
    }

    /**
     * Returns the chemical entity where the error occurred.
     *
     * @return The chemical entity where the error occurred.
     */
    public ChemicalEntity getChemicalEntity() {
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

    public boolean isLargerThan(NumericalError error) {
        return value > error.getValue();
    }

    public boolean isSmallerThan(NumericalError error) {
        return !isLargerThan(error);
    }

    @Override
    public String toString() {
        return equals(MINIMAL_EMPTY_ERROR) ? "Minimal" : "E(" + updatable.getStringIdentifier() + "," + entity.getIdentifier() + ") = " + value;
    }
}
