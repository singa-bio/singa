package bio.singa.mathematics.intervals;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class Sampler {

    /**
     * Samples multiplicative evenly distributed between the lower bound and upper bound.
     * E.g. Sampler.sampleMultiplicative(1, 10, 10) results in values from 1.0 to 10.0 with step width of 1.
     *
     * @param lowerBound The minimal sample.
     * @param upperBound The maximal sample.
     * @param numberOfSamples The number of total samples.
     * @return The samples.
     */
    public static List<Double> sampleMultiplicative(double lowerBound, double upperBound, int numberOfSamples) {
        List<Double> samples = new ArrayList<>();
        double stepSize = (upperBound - lowerBound) / (numberOfSamples - 1);
        double nextStep = lowerBound;
        for (int i = 0; i < numberOfSamples; i++) {
            samples.add(nextStep);
            nextStep += stepSize;
        }
        return samples;
    }

    /**
     * Samples logarithmic evenly distributed values between the lower and upper bound.
     * The sharpness determines the distribution towards the lower bound (values larger than 1) or upper bound (smaller than 1).
     *
     * @param lowerBound The minimal sample.
     * @param upperBound The maximal sample.
     * @param sharpness The sharpness determines the distribution towards the lower bound (values larger than 1) or upper bound (smaller than 1).
     * @param numberOfSamples The number of total samples.
     * @return The samples
     */
    public static List<Double> sampleLogarithmic(double lowerBound, double upperBound, double sharpness, int numberOfSamples) {
        List<Double> samples = new ArrayList<>();
        for (Double samplingPoint : sampleMultiplicative(0.0, 1.0, numberOfSamples)) {
            samples.add(Math.pow(samplingPoint, sharpness) * (upperBound - lowerBound) + lowerBound);
        }
        return samples;
    }

    /**
     * Returns exponentially evenly distributed values with base 10.
     * E.g. Sampler.sampleExponentially(-4, 2, 1) returns values from 1e-4, 1e-3, ... 1e1, 1e2
     *
     * @param smallestExponent The smallest exponent.
     * @param largestExponent The largest exponent.
     * @param baseMultiplier The value the exponent is multiplied with.
     * @return
     */
    public static List<Double> sampleExponentially(int smallestExponent, int largestExponent, double baseMultiplier) {
        List<Double> samples = new ArrayList<>();
        int numberOfSamples = largestExponent - smallestExponent + 1;
        for (Double samplingPoint : sampleMultiplicative(smallestExponent, largestExponent, numberOfSamples)) {
            samples.add(baseMultiplier * Math.pow(10, samplingPoint));
        }
        return samples;
    }


}
