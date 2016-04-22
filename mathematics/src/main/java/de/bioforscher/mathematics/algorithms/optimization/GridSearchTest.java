package de.bioforscher.mathematics.algorithms.optimization;

import de.bioforscher.core.parameters.BooleanParameter;
import de.bioforscher.core.parameters.IntegerParameter;
import de.bioforscher.core.parameters.ParameterSampler;
import de.bioforscher.core.parameters.UniqueParameterList;
import de.bioforscher.mathematics.combinatorics.Permutations;

import java.util.ArrayList;
import java.util.List;

public class GridSearchTest {

    public static void main(String[] args) {

        IntegerParameter testIntegerParameterA = new IntegerParameter("A", 1, 3);
        ParameterSampler.sample(testIntegerParameterA, 3).getValues().forEach(System.out::println);
        System.out.println();

        IntegerParameter testIntegerParameterB = new IntegerParameter("B", 4, 6);
        ParameterSampler.sample(testIntegerParameterB, 3).getValues().forEach(System.out::println);
        System.out.println();

        BooleanParameter testBooleanParameter = new BooleanParameter("C");
        ParameterSampler.sample(testBooleanParameter).getValues().forEach(System.out::println);
        System.out.println();

        List<UniqueParameterList<?>> inputList = new ArrayList<>();
        inputList.add(ParameterSampler.sample(testIntegerParameterA, 3));
        inputList.add(ParameterSampler.sample(testIntegerParameterB, 3));
        inputList.add(ParameterSampler.sample(testBooleanParameter));
        System.out.println(Permutations.generateAllCombinations(inputList));

    }

}
