package de.bioforscher.singa.chemistry.descriptive.estimations;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBond;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.chemistry.parser.smiles.SmilesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider.*;

/**
 * @author cl
 */
public class OctanolWaterPartition {

    private static final Logger logger = LoggerFactory.getLogger(OctanolWaterPartition.class);

    public enum Method {

        MLOGP_1,
        MLOGP_2,
        NC_NHET

    }

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
        // MLOGP Equation 1
        parameterCoefficients.put(new FactorIdentifier(1, "MLOGP_CX"), 0.246);
        parameterCoefficients.put(new FactorIdentifier(1, "MLOGP_NO"), -0.386);
        parameterCoefficients.put(new FactorIdentifier(1, "MLOGP_C"), 0.466);
        // MLOGP Equation 2
        parameterCoefficients.put(new FactorIdentifier(2, "MLOGP_CX"), 1.001);
        parameterCoefficients.put(new FactorIdentifier(2, "MLOGP_ECX"), 0.6); // Exponent
        parameterCoefficients.put(new FactorIdentifier(2, "MLOGP_NO"), -0.479);
        parameterCoefficients.put(new FactorIdentifier(2, "MLOGP_ENO"), 0.9); // Exponent
        parameterCoefficients.put(new FactorIdentifier(2, "MLOGP_C"), 0.7554);
        // NC+NHET
        parameterCoefficients.put(new FactorIdentifier(1, "NC_NC"), 0.11);
        parameterCoefficients.put(new FactorIdentifier(1, "NC_NHET"), -0.11);
        parameterCoefficients.put(new FactorIdentifier(1, "NC_C"), 1.46);
    }

    private static double getFactor(int equation, String parameter) {
        return parameterCoefficients.get(new FactorIdentifier(equation, parameter));
    }


    private static LinkedList<Set<Element>> xyPath = new LinkedList<>(Arrays.asList(
            new HashSet<>(Arrays.asList(NITROGEN, OXYGEN)),
            new HashSet<>(Arrays.asList(NITROGEN, OXYGEN))
    ));


    private static LinkedList<Set<Element>> xayPath = new LinkedList<>(Arrays.asList(
            new HashSet<>(Arrays.asList(NITROGEN, OXYGEN)),
            new HashSet<>(Arrays.asList(CARBON, SULFUR, PHOSPHORUS)),
            new HashSet<>(Arrays.asList(NITROGEN, OXYGEN))
    ));


    private MoleculeGraph moleculeGraph;

    public OctanolWaterPartition(MoleculeGraph moleculeGraph) {
        this.moleculeGraph = moleculeGraph;
    }

    public static double calculateOctanolWaterPartitionCoefficient(MoleculeGraph moleculeGraph, Method method) {
        logger.info("calculating octanol/water partition coefficient using {}", method.toString());
        OctanolWaterPartition partition = new OctanolWaterPartition(moleculeGraph);
        switch (method) {
            case MLOGP_1:
                return partition.calculateCoefficientUsingMLOGP1();
            case MLOGP_2:
                return partition.calculateCoefficientUsingMLOGP2();
            default:
                return partition.calculateCoefficientUsingNCAndNHET();
        }
    }

    /**
     * Calculates the Octanol/Water partition coefficient using the NC + NHET Method presented in "Calculation of
     * Molecular Lipophilicity: State-of-the-Art and Comparison of Log P Methods on More Than 96,000 Compounds" by
     * Mannhold et al. 2008
     *
     * @return The octanol water partition coefficient.
     */
    private double calculateCoefficientUsingNCAndNHET() {
        final double carbons = calculateNumberOfCarbonAtoms();
        final double nonCarbon = this.moleculeGraph.getNodes().size() - carbons;
        logger.debug("number of hetero atoms is: {}", nonCarbon);
        final double result = getFactor(1, "NC_NC") * carbons +
                getFactor(1, "NC_NHET") * nonCarbon +
                getFactor(1, "NC_C");
        logger.debug("calculated log p using NC + NHET (Mannhold 2008): {}", result);
        return result;
    }

    private double calculateNumberOfCarbonAtoms() {
        final double carbonAtoms = this.moleculeGraph.countAtomsOfElement(CARBON);
        logger.debug("number of carbon atoms is: {}", carbonAtoms);
        return carbonAtoms;
    }

    /**
     * Calculates the Octanol/Water partition coefficient using Equation 1 from "Simple Method of Calculating
     * Octanol/Water Partition Coefficient" by Moriguchi et al. 1992.
     *
     * @return log P
     */
    private double calculateCoefficientUsingMLOGP1() {
        final double result = getFactor(1, "MLOGP_CX") * calculateCX() +
                getFactor(1, "MLOGP_NO") * calcualteNO() +
                getFactor(1, "MLOGP_C");
        logger.debug("calculated log p using MLOGP1 (Moriguchi 1992): {}", result);
        return result;
    }

    /**
     * Calculates the Octanol/Water partition coefficient using Equation 2 from "Simple Method of Calculating
     * Octanol/Water Partition Coefficient" by Moriguchi et al. 1992.
     *
     * @return log P
     */
    private double calculateCoefficientUsingMLOGP2() {
        final double result = getFactor(2, "MLOGP_CX") * Math.pow(calculateCX(), getFactor(2, "MLOGP_ECX")) +
                getFactor(2, "MLOGP_NO") * Math.pow(calcualteNO(), getFactor(2, "MLOGP_ENO")) +
                getFactor(2, "MLOGP_C");
        logger.debug("calculated log p using MLOGP2 Moriguchi 1992: {}", result);
        return result;
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
        logger.debug("CX parameter scored: {}", cx);
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
        logger.debug("NO parameter scored: {}", no);
        return no;
    }

    /**
     * Returns the proximity effect resulting from N/O; X-Y: 1.0, X-A-Y: 2.0 (X, Y: N/O, A: C,S, or P) with a correction
     * (-1.0) for carboxoamide (RC(=O)NR2) and sulfonamide (RS(=O)2NR).
     *
     * @return Thr PRX parameter.
     */
    private double calculatePRX() {
        double prx = 0;
        // score for N/O; X-Y: 1.0
        final List<LinkedList<MoleculeAtom>> xy = this.moleculeGraph.findMultiPathOfElements(xyPath);
        prx += xy.size();
        // score for X-A-Y: 2.0 (X, Y: N/O, A: C,S, or P)
        final List<LinkedList<MoleculeAtom>> xay = this.moleculeGraph.findMultiPathOfElements(xayPath);
        for (LinkedList<MoleculeAtom> path : xay) {
            boolean isReduced = false;
            final MoleculeAtom firstAtom = path.get(0);
            final MoleculeAtom centralAtom = path.get(1);
            final MoleculeAtom lastAtom = path.get(2);
            if (centralAtom.getElement().equals(CARBON)) {
                // middle is carbon
                if (firstAtom.getElement().equals(OXYGEN)) {
                    // first is oxygen
                    if (lastAtom.getElement().equals(NITROGEN)) {
                        // last is nitrogen
                        if (this.moleculeGraph.getEdgeBetween(centralAtom, firstAtom).getType().equals(MoleculeBondType.DOUBLE_BOND)) {
                            isReduced = true;
                        }
                    }
                } else if (lastAtom.getElement().equals(OXYGEN)) {
                    // first can only be nitrogen if it is not oxygen
                    // last is oxygen
                    if (this.moleculeGraph.getEdgeBetween(centralAtom, lastAtom).getType().equals(MoleculeBondType.DOUBLE_BOND)) {
                        isReduced = true;
                    }
                }
            }

            if (isReduced) {
                logger.trace("{} scored: 1", path);
                prx += 1;
            } else {
                logger.trace("{} scored: 2", path);
                prx += 2;
            }
        }
        logger.debug("PRX parameter scored: {}", prx);
        return prx;
    }

    /**
     * Returns the total number of unsaturated ({@link MoleculeBondType#DOUBLE_BOND double} or
     * {@link MoleculeBondType#TRIPLE_BOND triple}) bonds except those in NO2.
     *
     * @return The UB parameter.
     */
    private double calculateUB() {
        // TODO assemble test case
        double ub = 0;
        for (MoleculeBond bond : this.moleculeGraph.getEdges()) {
            if (bond.getType() == MoleculeBondType.DOUBLE_BOND || bond.getType() == MoleculeBondType.TRIPLE_BOND) {
                MoleculeAtom source = bond.getSource();
                MoleculeAtom target = bond.getTarget();
                boolean isIgnored = false;
                if (source.getElement().equals(NITROGEN)) {
                    long oxygenCount = source.getNeighbours().stream()
                            .filter(moleculeAtom -> moleculeAtom.getElement().equals(OXYGEN))
                            .count();
                    if (oxygenCount == 2) {
                        isIgnored = true;
                    }
                }
                if (target.getElement().equals(NITROGEN)) {
                    long oxygenCount = target.getNeighbours().stream()
                            .filter(moleculeAtom -> moleculeAtom.getElement().equals(OXYGEN))
                            .count();
                    if (oxygenCount == 2) {
                        isIgnored = true;
                    }
                }

                if (!isIgnored) {
                    ub += 1;
                }

            }
        }
        logger.debug("UB parameter scored: {}", ub);
        return ub;
    }

    public static void main(String[] args) {

        String ampicilin = "[H][C@]12SC(C)(C)[C@@H](N1C(=O)[C@H]2NC(=O)[C@H](N)c1ccccc1)C(O)=O";
        String valerolactone = "O=C1CCCCO1";
        String oxazepam = "OC1N=C(C2=CC=CC=C2)C2=C(NC1=O)C=CC(Cl)=C2";

        MoleculeGraph molecule = SmilesParser.parse(oxazepam);
        molecule.replaceAromaticsWithDoubleBonds();

        OctanolWaterPartition.calculateOctanolWaterPartitionCoefficient(molecule, Method.NC_NHET);


    }

}
