package bio.singa.mathematics.forces;

public abstract class AbstractForce {

    private double forceConstant;

    public AbstractForce(double forceConstant) {
        this.forceConstant = forceConstant;
    }

    public double getForceConstant() {
        return forceConstant;
    }

    public void setForceConstant(double forceConstant) {
        this.forceConstant = forceConstant;
    }

}
