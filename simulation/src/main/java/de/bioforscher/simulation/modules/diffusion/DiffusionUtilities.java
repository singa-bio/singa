package de.bioforscher.simulation.modules.diffusion;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.quantities.Diffusivity;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static de.bioforscher.simulation.util.SystemDefaultConstants.WATER;
import static de.bioforscher.units.UnitProvider.GRAM_PER_MOLE;
import static de.bioforscher.units.UnitProvider.SQUARECENTIMETER_PER_SECOND;
import static java.lang.Math.log;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.AbstractUnit.ONE;

public final class DiffusionUtilities {

    private DiffusionUtilities() {
    }

    /**
     * The correlation threshold determines, whether to use Wikle correlation
     * (lighter than the threshold) or Young correlation (heavier than the
     * threshold).
     */
    public static final Quantity<MolarMass> CORRELATION_THRESHOLD = Quantities.getQuantity(10000, GRAM_PER_MOLE);

    /**
     * Diffusion calculation coefficient [dimensionless] (8.34e-8 =
     * 0.0000000834) <br>
     * From: Young, M., Carroad, P., and Bell, R. (1980). Estimation of
     * diffusion coefficients of proteins. Biotechnology and Bioengineering,
     * 22(5):947-955.
     */
    public static final Quantity<Dimensionless> YOUNG_DIFFUSION_COEFFICIENT_CONSTANT = Quantities.getQuantity(8.34e-8,
            ONE);

    /**
     * Diffusion calculation coefficient [dimensionless] (7.4e-8 = 0.000000074)
     * <br>
     * From: Wilke, C. and Chang, P. (1955). Correlation of diffusion
     * coefficients in dilute solutions. AIChE Journal, 1(2):264-270.
     */
    public static final Quantity<Dimensionless> WILKE_DIFFUSION_COEFFICIENT_CONSTANT = Quantities.getQuantity(7.4e-8,
            ONE);

    /**
     * Association parameter to define the effective molecular weight of the
     * solvent. <br>
     * From: Wilke, C. and Chang, P. (1955). Correlation of diffusion
     * coefficients in dilute solutions. AIChE Journal, 1(2):264-270.
     */
    public static final Quantity<Dimensionless> WILKE_ASSOCIATION_WATER = Quantities.getQuantity(2.26, ONE);

    /**
     * Solute transitional diffusion coefficient. Describes the ratio of D in
     * cells to D in water. <br>
     * From: Kao, H. P., Abney, J. R., and Verkman, A. (1993). Determinants of
     * the translational mobility of a small solute in cell cytoplasm. The
     * Journal of cell biology, 120(1):175-184.
     */
    public static final Quantity<Dimensionless> STDF_CELL_WATER = Quantities.getQuantity(0.27, ONE);

    /**
     * Estimates the diffusivity of the species using its features. Always
     * returns cm^2/s.
     *
     * @param species The species
     * @return The diffusivity of the species.
     */
    public static Quantity<Diffusivity> estimateDiffusivity(ChemicalEntity species) {
        // choose which correlation to take
        if (species.getMolarMass().getValue().doubleValue() < CORRELATION_THRESHOLD.getValue().doubleValue()) {
            return DiffusionUtilities.calculateWilkeCorrelation(species);
        }
        return DiffusionUtilities.calculateYoungCorrelation(species);
    }

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s. <br>
     * Paper: Young, M., Carroad, P., and Bell, R. (1980). Estimation of
     * diffusion coefficients of proteins. Biotechnology and Bioengineering,
     * 22(5):947-955.
     *
     * @param chemicalEntity The species.
     * @return The diffusivity of the species in cm^2/s.
     */
    public static Quantity<Diffusivity> calculateYoungCorrelation(ChemicalEntity chemicalEntity) {
        // D = c * (T/n*M^1/3)
        final double diffusivity = YOUNG_DIFFUSION_COEFFICIENT_CONSTANT.getValue().doubleValue()
                * (EnvironmentalVariables.getInstance().getSystemTemperature().getValue().doubleValue()
                / (EnvironmentalVariables.getInstance().getSystemViscosity().getValue().doubleValue()
                * Math.cbrt(chemicalEntity.getMolarMass().getValue().doubleValue())));
        return Quantities.getQuantity(diffusivity, SQUARECENTIMETER_PER_SECOND);
    }

    /**
     * Estimates the diffusion coefficient. Always returns cm^2/s. <br>
     * Paper: Wilke, C. and Chang, P. (1955). Correlation of diffusion
     * coefficients in dilute solutions. AIChE Journal, 1(2):264-270.
     *
     * @param chemicalEntity The species.
     * @return The diffusivity of the species in cm^2/s.
     */
    public static Quantity<Diffusivity> calculateWilkeCorrelation(ChemicalEntity chemicalEntity) {
        // a = c * (x * M(H2O))^0.5 * T
        final double dividend = WILKE_DIFFUSION_COEFFICIENT_CONSTANT.getValue().doubleValue()
                * Math.pow(WATER.getMolarMass().getValue().doubleValue() * WILKE_ASSOCIATION_WATER.getValue()
                .doubleValue(), 0.5)
                * EnvironmentalVariables.getInstance().getSystemTemperature().getValue().doubleValue();
        // b = n * M(Sp)^0.6
        final double divisor = EnvironmentalVariables.getInstance().getSystemViscosity().getValue().doubleValue()
                * Math.pow(estimateMolarVolume(chemicalEntity), 0.6);
        // D = a / b
        return Quantities.getQuantity(dividend / divisor, SQUARECENTIMETER_PER_SECOND);
    }

    /**
     * Estimate molar volume from weight.
     *
     * @param species The species.
     * @return The estimated molar volume.
     */
    public static double estimateMolarVolume(ChemicalEntity species) {
        // V = 0.968 * M + 13.8
        return 0.968 * species.getMolarMass().getValue().doubleValue() + 13.8;
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
