package bio.singa.simulation.model.simulation.error;

import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.simulation.UpdateScheduler;

import static bio.singa.simulation.model.simulation.error.DisplacementDeviation.MAXIMAL_POSITIVE_DEVIATION;

/**
 * @author cl
 */
public class GlobalDisplacementDeviationManager {

    private static final double DEFAULT_GLOBAL_DEVIATION_TOLERANCE = 0.8;

    private double tolerance = DEFAULT_GLOBAL_DEVIATION_TOLERANCE;
    private DisplacementDeviation deviation;
    private boolean errorAcceptable;
    private UpdateScheduler updateScheduler;
    private VesicleLayer vesicleLayer;

    public GlobalDisplacementDeviationManager(UpdateScheduler updateScheduler) {
        this.updateScheduler = updateScheduler;
        vesicleLayer = updateScheduler.getSimulation().getVesicleLayer();
        deviation = DisplacementDeviation.MINIMAL_DEVIATION;
        reset();
    }

    public void evaluateDeviation() {
        if (vesicleLayer.getVesicles().isEmpty()) {
            errorAcceptable = true;
            deviation = MAXIMAL_POSITIVE_DEVIATION;
            return;
        }
        DisplacementDeviation globalDeviation = determineGlobalDeviation();
        if (globalDeviation.getValue() < 0) {
            errorAcceptable = false;
        } else {
            errorAcceptable = true;
        }
    }

    private DisplacementDeviation determineGlobalDeviation() {
        DisplacementDeviation largestDeviation = MAXIMAL_POSITIVE_DEVIATION;
        for (Vesicle vesicle : vesicleLayer.getVesicles()) {
            Vector2D totalDisplacement = vesicle.calculateTotalDisplacement();
            // skip if total displacement is zero
            if (totalDisplacement.equals(Vector2D.ZERO)) {
                continue;
            }
            double length = totalDisplacement.getMagnitude();
            // determine fraction of maximal allowed error
            double deviation = 1 - (length / updateScheduler.getErrorManager().getDisplacementCutoff());
            if (deviation < largestDeviation.getValue()) {
                largestDeviation = new DisplacementDeviation(vesicle, deviation);
            }
        }
        return largestDeviation;
    }

    public DisplacementDeviation getDeviation() {
        return deviation;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public boolean deviationIsCritical() {
        return deviation.getValue() < tolerance;
    }

    public boolean deviationIsAcceptable() {
        return errorAcceptable;
    }

    public void reset() {
        errorAcceptable = false;
    }

}
