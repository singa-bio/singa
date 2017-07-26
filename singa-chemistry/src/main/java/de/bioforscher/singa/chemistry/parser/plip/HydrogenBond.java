package de.bioforscher.singa.chemistry.parser.plip;

/**
 * @author cl
 */
public class HydrogenBond extends Interaction {

    private int donor;
    private int acceptor;
    private boolean sidechain;
    private boolean protIsDon;
    private double distanceHA;
    private double distanceDA;
    private double angle;

    public int getDonor() {
        return donor;
    }

    public void setDonor(int donor) {
        this.donor = donor;
    }

    public int getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(int acceptor) {
        this.acceptor = acceptor;
    }

    public boolean isSidechain() {
        return sidechain;
    }

    public void setSidechain(boolean sidechain) {
        this.sidechain = sidechain;
    }

    public boolean isProtIsDon() {
        return protIsDon;
    }

    public void setProtIsDon(boolean protIsDon) {
        this.protIsDon = protIsDon;
    }

    public double getDistanceHA() {
        return distanceHA;
    }

    public void setDistanceHA(double distanceHA) {
        this.distanceHA = distanceHA;
    }

    public double getDistanceDA() {
        return distanceDA;
    }

    public void setDistanceDA(double distanceDA) {
        this.distanceDA = distanceDA;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "HydrogenBond{" +
                "donor=" + donor +
                ", acceptor=" + acceptor +
                ", sidechain=" + sidechain +
                ", protIsDon=" + protIsDon +
                ", distanceHA=" + distanceHA +
                ", distanceDA=" + distanceDA +
                ", angle=" + angle +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
