package de.bioforscher.singa.mathematics.forces;

/**
 * A force that repels two objects.
 *
 * @author Christoph Leberecht
 */
public class RepulsiveForce extends AbstractForce implements Force {

    public RepulsiveForce(double forceConstant) {
        super(forceConstant);
    }

    @Override
    public double calculateForce(double magnitude) {
        return (getForceConstant() * getForceConstant()) / magnitude;
    }

}
