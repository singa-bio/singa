package de.bioforscher.singa.mathematics.forces;

/**
 * A force that attracts two objects.
 *
 * @author Christoph Leberecht
 */
public class AttractiveForce extends AbstractForce implements Force {

    public AttractiveForce(double forceConstant) {
        super(forceConstant);
    }

    @Override
    public double calculateForce(double magnitude) {
        return (magnitude * magnitude) / getForceConstant();
    }

}
