package de.bioforscher.singa.simulation.model.modules.displacement;

import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class DisplacementDeltaManager {

    private static final Logger logger = LoggerFactory.getLogger(DisplacementDeltaManager.class);

    private Vector2D currentPosition;
    private List<DisplacementDelta> potentialSpatialDeltas;
    private Vector2D nextPosition;

    public DisplacementDeltaManager(Vector2D currentPosition) {
        this.currentPosition = currentPosition;
        this.nextPosition = currentPosition;
        potentialSpatialDeltas = new ArrayList<>();
    }

    public Vector2D getCurrentPosition() {
        return currentPosition;
    }

    public List<DisplacementDelta> getPotentialSpatialDeltas() {
        return potentialSpatialDeltas;
    }

    public DisplacementDelta getPotentialSpatialDelta(DisplacementBasedModule module) {
        for (DisplacementDelta potentialSpatialDelta : potentialSpatialDeltas) {
            if (potentialSpatialDelta.getModule().equals(module)) {
                return potentialSpatialDelta;
            }
        }
        return null;
    }

    public void addPotentialSpatialDelta(DisplacementDelta delta) {
        potentialSpatialDeltas.add(delta);
    }

    public void clearPotentialDisplacementDeltas() {
        potentialSpatialDeltas.clear();
    }

    public Vector2D calculateTotalDisplacement() {
        // FIXME calculates total displacement AND sets next position
        Vector2D totalDisplacement = new Vector2D(0.0,0.0);
        for (DisplacementDelta potentialSpatialDelta : potentialSpatialDeltas) {
            totalDisplacement = totalDisplacement.add(potentialSpatialDelta.getDeltaVector());
        }
        nextPosition = currentPosition.add(totalDisplacement);
        return totalDisplacement;
    }

    public Vector2D getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(Vector2D nextPosition) {
        this.nextPosition = nextPosition;
    }

    public void resetNextPosition() {
        nextPosition = currentPosition;
    }

    public void updatePosition() {
        logger.trace("Moving vesicle from {} to {}.", currentPosition, nextPosition);
        currentPosition = nextPosition;
    }

}
