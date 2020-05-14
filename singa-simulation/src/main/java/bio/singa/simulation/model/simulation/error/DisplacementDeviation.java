package bio.singa.simulation.model.simulation.error;

import bio.singa.simulation.model.agents.pointlike.Vesicle;

/**
 * @author cl
 */
public class DisplacementDeviation {

    /**
     * If reference value is smaller then current displacement, the deviation is large (above zero).
     */
    public static final DisplacementDeviation MAXIMAL_POSITIVE_DEVIATION = new DisplacementDeviation(null, Double.MAX_VALUE);

    /**
     * If reference value is equal to the current displacement, the deviation is zero.
     */
    public static final DisplacementDeviation MINIMAL_DEVIATION = new DisplacementDeviation(null, 0);

    /**
     * If reference value is larger then current displacement, the deviation is small (below zero).
     */
    public static final DisplacementDeviation MAXIMAL_NEGATIVE_DEVIATION = new DisplacementDeviation(null, -Double.MAX_VALUE);

    private final Vesicle vesicle;

    private final double value;

    public DisplacementDeviation(Vesicle vesicle, double value) {
        this.vesicle = vesicle;
        this.value = value;
    }

    public Vesicle getVesicle() {
        return vesicle;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == MAXIMAL_POSITIVE_DEVIATION.getValue()) {
            return "Maximal Positive";
        }
        if (value == MAXIMAL_NEGATIVE_DEVIATION.getValue()) {
            return "Maximal Negative";
        }
        if (value == MINIMAL_DEVIATION.getValue()) {
            return "Minimal Deviation";
        }
        return String.format("D(%s, %6.3e)", vesicle.getStringIdentifier(), value);
    }

}
