package de.bioforscher.mathematics.functions;

public class DecayFunctions {

    public static double exponential(int timeStep, int maximalLifeTime, double initialValue) {
        final double denominator = 0.15 * maximalLifeTime * maximalLifeTime;
        return (initialValue * Math.exp(-(timeStep * timeStep) / denominator));
    }

    public static double linear(int timeStep, int maximalLifeTime, double initialValue) {
        final double slope = -1.0 * (initialValue / maximalLifeTime);
        return slope * timeStep + initialValue;
    }

}
