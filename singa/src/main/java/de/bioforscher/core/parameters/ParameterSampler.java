package de.bioforscher.core.parameters;

import java.math.BigDecimal;

public class ParameterSampler {

    public static UniqueParameterList<Boolean> sample(BooleanParameter parameter) {
        UniqueParameterList<Boolean> parameterSet = new UniqueParameterList<Boolean>();
        parameterSet.add(new ParameterValue<Boolean>(parameter, true));
        parameterSet.add(new ParameterValue<Boolean>(parameter, false));
        return parameterSet;
    }

    public static UniqueParameterList<Double> sample(DoubleParameter parameter, int numberOfSamples) {
        UniqueParameterList<Double> parameterSet = new UniqueParameterList<Double>();
        BigDecimal stepSize = calculateStepSize(parameter.getLowerBound(), parameter.getUpperBound(), numberOfSamples);
        BigDecimal nextStep = new BigDecimal(parameter.getLowerBound().toString());
        for (int i = 0; i < numberOfSamples; i++) {
            parameterSet.add(new ParameterValue<Double>(parameter, nextStep.doubleValue()));
            nextStep = calculateNextStep(nextStep, stepSize);
        }
        return parameterSet;
    }

    public static UniqueParameterList<Integer> sample(IntegerParameter parameter, int numberOfSamples) {
        while ((parameter.getUpperBound() - parameter.getLowerBound()) % numberOfSamples != 0) {
            numberOfSamples--;
        }
        numberOfSamples++;
        UniqueParameterList<Integer> parameterSet = new UniqueParameterList<Integer>();
        BigDecimal nextStep = new BigDecimal(parameter.getLowerBound().toString());
        BigDecimal stepSize = calculateStepSize(parameter.getLowerBound(), parameter.getUpperBound(), numberOfSamples);
        for (int i = 0; i < numberOfSamples; i++) {
            parameterSet.add(new ParameterValue<Integer>(parameter, nextStep.intValue()));
            nextStep = calculateNextStep(nextStep, stepSize);
        }
        return parameterSet;
    }

    private static <T> BigDecimal calculateStepSize(T lowerBound, T upperBound, int numberOfSamples) {
        return calculateStepSize(new BigDecimal(lowerBound.toString()), new BigDecimal(upperBound.toString()), numberOfSamples);
    }

    private static BigDecimal calculateStepSize(BigDecimal lowerBound, BigDecimal upperBound, int numberOfSamples) {
        return (upperBound.subtract(lowerBound)).divide(new BigDecimal(String.valueOf(numberOfSamples - 1)), 12, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal calculateNextStep(BigDecimal lastStep, BigDecimal stepSize) {
        return (lastStep.add(stepSize));
    }


}
