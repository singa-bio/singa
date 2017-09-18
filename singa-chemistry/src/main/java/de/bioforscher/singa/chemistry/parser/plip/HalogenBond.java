package de.bioforscher.singa.chemistry.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class HalogenBond extends Interaction {

    private int donor;
    private int acceptor;
    private double distance;
    private double donorAngle;
    private double acceptorAngle;

    public HalogenBond(int plipIdentifier) {
        super(plipIdentifier);
    }

    public int getDonor() {
        return this.donor;
    }

    public void setDonor(int donor) {
        this.donor = donor;
    }

    public int getAcceptor() {
        return this.acceptor;
    }

    public void setAcceptor(int acceptor) {
        this.acceptor = acceptor;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDonorAngle() {
        return this.donorAngle;
    }

    public void setDonorAngle(double donorAngle) {
        this.donorAngle = donorAngle;
    }

    public double getAcceptorAngle() {
        return this.acceptorAngle;
    }

    public void setAcceptorAngle(double acceptorAngle) {
        this.acceptorAngle = acceptorAngle;
    }

    @Override
    public int getFirstSourceAtom() {
        return this.donor;
    }

    @Override
    public int getFirstTargetAtom() {
        return this.acceptor;
    }

    @Override
    public String toString() {
        return "HalogenBond{" +
                "donor=" + this.donor +
                ", acceptor=" + this.acceptor +
                ", plipIdentifier=" + this.plipIdentifier +
                ", source=" + this.source +
                ", target=" + this.target +
                ", ligandCoordinate=" + Arrays.toString(this.ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(this.proteinCoordinate) +
                '}';
    }
}
