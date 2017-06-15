package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static java.lang.Math.log;
import static tec.units.ri.unit.Units.METRE;

public final class DiffusionUtilities {

    private DiffusionUtilities() {
    }
    
    public static Quantity<Length> calculateThresholdForDistance(Quantity<Time> timeStep, int maximalDegree,
                                                                 Quantity<MolarConcentration> maximalConcentration,
                                                                 Quantity<Diffusivity> maximalDiffusivity) {

        double time = timeStep.getValue().doubleValue();
        double concentration = maximalConcentration.getValue().doubleValue();
        double diffusivity = maximalDiffusivity.getValue().doubleValue();

        double length = Math.sqrt((maximalDegree * diffusivity - concentration) * time);
        Unit<Length> lengthUnit = (Unit<Length>) maximalDiffusivity.getUnit().getBaseUnits().keySet().stream()
                                                    .filter(unit -> unit.getSystemUnit().equals(METRE))
                                                    .findFirst().get();

        return Quantities.getQuantity(length, lengthUnit);

    }

    public static boolean areViableParametersForDiffusion(double timeStep, double nodeDistance,
                                                   int maximalDegree, Quantity<MolarConcentration> maximalConcentration,
                                                   Quantity<Diffusivity> maximalDiffusivity) {

        double concentration = maximalConcentration.getValue().doubleValue();
        double diffusivity = maximalDiffusivity.getValue().doubleValue();
        // left side of the inequality
        double left = (nodeDistance*nodeDistance)/timeStep;
        // right side of the inequality
        double right = maximalDegree * diffusivity - concentration;
        return left > right;
    }

    public static int estimateSimulationSpeed(double timeStep, int numberOfNodes) {
        // estimates the time needed for 1000 time step units
        return (int)(1000.0 / timeStep * numberOfNodes);
    }

    public static double estimateSimulationAccuracy(double timeStep, double nodeDistance) {
        return log(timeStep * nodeDistance * nodeDistance);
    }

}
