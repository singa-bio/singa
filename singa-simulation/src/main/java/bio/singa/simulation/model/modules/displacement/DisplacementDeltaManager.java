package bio.singa.simulation.model.modules.displacement;

import bio.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The DisplacementDeltaManager handles the current position an updatable amd the updates to this position during
 * simulation.
 *
 * @author cl
 */
public class DisplacementDeltaManager {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DisplacementDeltaManager.class);

    /**
     * The list of potential deltas.
     */
    private List<DisplacementDelta> potentialSpatialDeltas;

    /**
     * The current position.
     */
    private Vector2D currentPosition;

    /**
     * The next position, after the potential deltas would be applied.
     */
    private Vector2D nextPosition;

    /**
     * Creates a new Displacement Delta Manager.
     *
     * @param initialPosition The initial position
     */
    public DisplacementDeltaManager(Vector2D initialPosition) {
        currentPosition = initialPosition;
        nextPosition = initialPosition;
        potentialSpatialDeltas = new ArrayList<>();
    }

    /**
     * Returns the current position.
     *
     * @return The current position.
     */
    public Vector2D getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Returns the potential displacement deltas.
     *
     * @return The current displacement deltas.
     */
    public List<DisplacementDelta> getPotentialDisplacementDeltas() {
        return potentialSpatialDeltas;
    }

    /**
     * Returns a specific delta applied by the given module.
     * @param module The module.
     * @return The displacement delta.
     */
    public DisplacementDelta getPotentialDisplacementDelta(DisplacementBasedModule module) {
        for (DisplacementDelta potentialSpatialDelta : potentialSpatialDeltas) {
            if (potentialSpatialDelta.getModule().equals(module)) {
                return potentialSpatialDelta;
            }
        }
        return null;
    }

    /**
     * Adds a displacement delta.
     * @param delta The displacement delta.
     */
    public void addPotentialDisplacementDelta(DisplacementDelta delta) {
        potentialSpatialDeltas.add(delta);
    }

    /**
     * Clears all potential displacement deltas.
     */
    public void clearPotentialDisplacementDeltas() {
        potentialSpatialDeltas.clear();
    }

    /**
     * Calculates the total displacement resulting from the potential deltas and sets the result as the next position.
     * @return The next position.
     */
    public Vector2D calculateTotalDisplacement() {
        // FIXME calculates total displacement AND sets next position
        Vector2D totalDisplacement = new Vector2D(0.0, 0.0);
        for (DisplacementDelta potentialSpatialDelta : potentialSpatialDeltas) {
            totalDisplacement = totalDisplacement.add(potentialSpatialDelta.getDeltaVector());
        }
        nextPosition = currentPosition.add(totalDisplacement);
        return totalDisplacement;
    }

    /**
     * Returns the next position.
     * @return The next position.
     */
    public Vector2D getNextPosition() {
        return nextPosition;
    }

    /**
     * Resets the next position to the current position.
     */
    public void resetNextPosition() {
        nextPosition = currentPosition;
    }

    /**
     * Sets the current position to the next position.
     */
    public void updatePosition() {
        logger.trace("Moving vesicle from {} to {}.", currentPosition, nextPosition);
        currentPosition = nextPosition;
    }

}
