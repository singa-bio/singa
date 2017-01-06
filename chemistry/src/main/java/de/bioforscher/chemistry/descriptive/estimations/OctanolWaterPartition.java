package de.bioforscher.chemistry.descriptive.estimations;

import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.descriptive.molecules.MoleculeGraph;

import java.util.HashMap;
import java.util.Map;

import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.*;

/**
 * @author leberech
 */
public class OctanolWaterPartition {

    /**
     * The class encapsulates a identifer for each equation and parameter to assign a specific value to.
     */
    private static class FactorIdentifier {

        private final int equation;
        private final String parameter;

        public FactorIdentifier(int equation, String parameter) {
            this.equation = equation;
            this.parameter = parameter;
        }

        public int getEquation() {
            return this.equation;
        }

        public String getParameter() {
            return this.parameter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FactorIdentifier that = (FactorIdentifier) o;

            if (this.equation != that.equation) return false;
            return this.parameter != null ? this.parameter.equals(that.parameter) : that.parameter == null;
        }

        @Override
        public int hashCode() {
            int result = this.equation;
            result = 31 * result + (this.parameter != null ? this.parameter.hashCode() : 0);
            return result;
        }
    }

    private static Map<FactorIdentifier, Double> parameterCoefficients = new HashMap<>();

    static {
        parameterCoefficients.put(new FactorIdentifier(1, "CX"), 0.246);
        parameterCoefficients.put(new FactorIdentifier(1, "NO"), -0.386);
        parameterCoefficients.put(new FactorIdentifier(1, "C"), 0.466);

        parameterCoefficients.put(new FactorIdentifier(2, "CX"), 1.001);
        parameterCoefficients.put(new FactorIdentifier(2, "ECX"), 0.6); // Exponent
        parameterCoefficients.put(new FactorIdentifier(2, "NO"), -0.479);
        parameterCoefficients.put(new FactorIdentifier(2, "ENO"), 0.9); // Exponent
        parameterCoefficients.put(new FactorIdentifier(2, "C"), 0.7554);

    }

    private static double getFactor(int equation, String parameter) {
        return parameterCoefficients.get(new FactorIdentifier(1, "CX"));
    }

    private MoleculeGraph moleculeGraph;

    public OctanolWaterPartition(MoleculeGraph moleculeGraph) {
        this.moleculeGraph = moleculeGraph;
    }

    public static void calculateOctanolWaterPartitionCoefficient(MoleculeGraph moleculeGraph) {
        OctanolWaterPartition partition = new OctanolWaterPartition(moleculeGraph);


    }

    /**
     * Calculates the Octanol/Water partition coefficient using Equation 1 from "Simple Method of Calculating
     * Octanol/Water Partition Coefficient".
     *
     * @return log P
     */
    private double calculateCoefficientUsingEq1() {
        return getFactor(1, "CX") * calculateCX() +
                getFactor(1, "NO") * calcualteNO() +
                getFactor(1, "C");
    }

    /**
     * Calculates the Octanol/Water partition coefficient using Equation 2 from "Simple Method of Calculating
     * Octanol/Water Partition Coefficient".
     *
     * @return log P
     */
    private double calculateCoefficientUsingEq2() {
        return getFactor(2, "CX") * Math.pow(calculateCX(), getFactor(2, "ECX")) +
                getFactor(2, "NO") * Math.pow(calcualteNO(), getFactor(2, "ENO")) +
                getFactor(2, "C");
    }



    /**
     * Returns the summation of carbon and halogen atoms weighted by {@link ElementProvider#CARBON Carbon}: 1.0,
     * {@link ElementProvider#FLUORINE Flourine}: 0.5, {@link ElementProvider#CHLORINE Chlorine}: 1.0,
     * {@link ElementProvider#BROMINE Bromine}: 1.5 and {@link ElementProvider#IODINE Iodine}: 2.0.
     *
     * @return The CX Parameter.
     */
    private double calculateCX() {
        double cx = 0;
        cx += this.moleculeGraph.countAtomsOfElement(CARBON);
        cx += this.moleculeGraph.countAtomsOfElement(FLUORINE) * 0.5;
        cx += this.moleculeGraph.countAtomsOfElement(CHLORINE);
        cx += this.moleculeGraph.countAtomsOfElement(BROMINE) * 1.5;
        cx += this.moleculeGraph.countAtomsOfElement(IODINE) * 2.0;
        return cx;
    }

    /**
     * Returns the total number of {@link ElementProvider#OXYGEN Oxigen} and {@link ElementProvider#NITROGEN Nitrogen}
     * atoms.
     *
     * @return The NO parameter.
     */
    private double calcualteNO() {
        double no = 0;
        no += this.moleculeGraph.countAtomsOfElement(OXYGEN);
        no += this.moleculeGraph.countAtomsOfElement(NITROGEN);
        return no;
    }

    private double calculatePRX() {
        return 0;
    }

}
