package de.bioforscher.mathematics.concepts;

/**
 * In physics and mathematics, the dimension of a mathematical space (or object)
 * is informally defined as the minimum number of coordinates needed to specify
 * any point within it.
 * <p>
 * TODO: not really satisfied with this class.
 *
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public class Dimension {

    /**
     * The number of parameters of the system that may vary independently.
     */
    private final int degreesOfFreedom;

    /**
     * Creates a new {@code Dimension} object.
     *
     * @param degreesOfFreedom The degrees of freedom.
     */
    public Dimension(int degreesOfFreedom) {
        this.degreesOfFreedom = degreesOfFreedom;
    }

    /**
     * Returns the degrees of freedom.
     *
     * @return The degrees of freedom.
     */
    public int getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + degreesOfFreedom;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dimension other = (Dimension) obj;
        if (degreesOfFreedom != other.degreesOfFreedom)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return degreesOfFreedom + "D";
    }

}
