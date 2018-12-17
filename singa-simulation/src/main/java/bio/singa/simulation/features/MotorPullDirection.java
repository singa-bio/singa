package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

import static bio.singa.simulation.features.MotorPullDirection.*;

/**
 * @author cl
 */
public class MotorPullDirection extends QualitativeFeature<Direction> {

    public enum Direction {
        PLUS, MINUS
    }

    public MotorPullDirection(Direction direction, List<Evidence> evidence) {
        super(direction, evidence);
    }

    public MotorPullDirection(Direction direction, Evidence evidence) {
        super(direction, evidence);
    }

    public MotorPullDirection(Direction direction) {
        super(direction);
    }

}
