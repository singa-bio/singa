package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.StringFeature;

import java.util.List;

/**
 * @author cl
 */
public class MotorPullDirection extends StringFeature {

    public static final String PLUS = "+";
    public static final String MINUS = "-";

    public MotorPullDirection(String direction, List<Evidence> evidence) {
        super(direction, evidence);
    }

    public MotorPullDirection(String direction, Evidence evidence) {
        super(direction, evidence);
    }

    public MotorPullDirection(String direction) {
        super(direction);
    }

}
