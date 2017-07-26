package de.bioforscher.singa.chemistry.parser.plip;

/**
 * @author cl
 */
public class HalogenBond extends Interaction {

    private int donor;
    private int acceptor;
    private double distance;
    private double donorAngle;
    private double acceptorAngle;

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDonorAngle() {
        return donorAngle;
    }

    public void setDonorAngle(double donorAngle) {
        this.donorAngle = donorAngle;
    }

    public double getAcceptorAngle() {
        return acceptorAngle;
    }

    public void setAcceptorAngle(double acceptorAngle) {
        this.acceptorAngle = acceptorAngle;
    }

    @Override
    public String toString() {
        return "HalogenBond{" +
                "donor=" + donor +
                ", acceptor=" + acceptor +
                ", distance=" + distance +
                ", donorAngle=" + donorAngle +
                ", acceptorAngle=" + acceptorAngle +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
