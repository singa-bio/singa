package de.bioforscher.simulation.diffusion;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.core.parameters.*;
import de.bioforscher.mathematics.algorithms.optimization.AbstractGridSearch;
import de.bioforscher.mathematics.combinatorics.Permutations;
import de.bioforscher.simulation.model.GraphAutomata;
import de.bioforscher.simulation.util.AutomataFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.SECOND;

public class DiffusionParameterOptimization extends AbstractGridSearch {

    private static final List<Species> lookupSpecies = new ArrayList<>();

    static {
        // lookupSpecies.add(new Species("H2"));
        // lookupSpecies.add(new Species("NH3"));
        // lookupSpecies.add(new Species("Benzene"));
        // lookupSpecies.add(new Species("Methanol"));
        // lookupSpecies.add(new Species("Succinic acid"));
        // lookupSpecies.add(new Species("Ethane-1.2-diol"));
        // lookupSpecies.add(new Species("Raffinose"));
    }

    public DiffusionParameterOptimization(List<UniqueParameterList<?>> inputParameterList) {
        super(inputParameterList);
    }

    @Override
    public void search() {

        // generate Combinations
        List<MixedParameterList> parameterSets = Permutations.generateAllCombinations(getInputParameterList());

        // test for each parameter combination
        for (Species species : lookupSpecies) {
            for (MixedParameterList parameters : parameterSets) {
                searchWithParameters(parameters, species);
            }
        }

    }

    public void searchWithParameters(MixedParameterList parameters, Species species) {

        // get parameters back
        ParameterValue<Integer> numberOfNodesParameter = parameters.getValue(0, Integer.class);
        ParameterValue<Double> timeStepParameter = parameters.getValue(1, Double.class);
        Quantity<Time> timeStep = Quantities.getQuantity(timeStepParameter.getValue(), NANO(SECOND));
        int numberOfNodes = numberOfNodesParameter.getValue();

        // setup automaton
        GraphAutomata automata = AutomataFactory
                .buildDiffusionOptimizationTestAutomata(numberOfNodesParameter.getValue(), timeStep, species);

        int observedNode = numberOfNodes * numberOfNodes / 2 - 1;

        // set observed node
        automata.getGraph().getNode(observedNode).setObserved(true);

        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        long start_time = System.nanoTime();

        System.out.println("Testing : " + species.getName() + ", " + parameters);

        // simulation
        start_time = System.nanoTime();
        while (automata.getGraph().getNode(observedNode).getConcentration(species).getValue().doubleValue() < 0.25) {
            if (automata.epoch % 1000 == 0 && automata.epoch > 1) {

            }
            automata.next();
        }
        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e9;
        System.out.println("  Simulation finished after " + df.format(difference) + " s");
        System.out.println(
                "  Halflife time reached at " + automata.epoch * timeStep.getValue().doubleValue() / 1000 + " µs");
        writeResults(parameters, automata.epoch * timeStep.getValue().doubleValue() / 1000, species);
        this.getResultingValues().put(parameters, automata.epoch * timeStep.getValue().doubleValue() / 1000);
    }

    public void writeResults(MixedParameterList parameters, double time, Species species) {
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter("result_" + species.getName() + ".csv", true)) {
            writer.append(parameters.toString()).append(", ").append(String.valueOf(time)).append(eol);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

    }

    public static void main(String[] args) {

        // define parameters
        IntegerParameter numberOfNodes = new IntegerParameter("number of nodes", 100, 200);
        IntegerParameter timeStepSize = new IntegerParameter("time step size in ns", 2, 5);

        // sample parameters
        UniqueParameterList<Integer> numberOfNodesSample = ParameterSampler.sample(numberOfNodes, 1);
        System.out.println(numberOfNodesSample);
        UniqueParameterList<Integer> timeStepSizeSample = ParameterSampler.sample(timeStepSize, 1);
        System.out.println(timeStepSizeSample);

        // add input and
        DiffusionParameterOptimization dpo = new DiffusionParameterOptimization(
                Arrays.asList(numberOfNodesSample, timeStepSizeSample));
        dpo.search();

    }

}
