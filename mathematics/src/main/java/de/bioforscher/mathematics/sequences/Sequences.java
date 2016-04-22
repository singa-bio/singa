package de.bioforscher.mathematics.sequences;

public class Sequences {

    /**
     * OEIS Identifier = A000225
     *
     * @param iteration
     * @return
     */
    public static int mersenneSeries(int iteration) {
        return (int) (Math.pow(2, iteration) - 1);
    }

}
