package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;

/**
 * @author cl
 */
public class MotorPullDirection extends AbstractFeature<MotorPullDirection.Direction> {

    private static final String SYMBOL = "direction";

    public enum Direction {
        PLUS, MINUS
    }

    public MotorPullDirection(Direction direction, Evidence evidence) {
        super(direction, evidence);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
