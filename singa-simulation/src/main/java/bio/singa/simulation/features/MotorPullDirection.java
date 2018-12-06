package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;

/**
 * @author cl
 */
public class MotorPullDirection extends AbstractFeature<MotorPullDirection.Direction> {

    private static final String SYMBOL = "direction";

    public enum Direction {
        PLUS, MINUS
    }

    public MotorPullDirection(Direction direction, FeatureOrigin featureOrigin) {
        super(direction, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
