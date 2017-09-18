package de.bioforscher.singa.chemistry.parser.plip;

import java.util.Arrays;

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

    public HydrogenBond(int plipIdentifier) {
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

    public boolean isSidechain() {
        return this.sidechain;
    }

    public void setSidechain(boolean sidechain) {
        this.sidechain = sidechain;
    }

    public boolean isProtIsDon() {
        return this.protIsDon;
    }

    public void setProtIsDon(boolean protIsDon) {
        this.protIsDon = protIsDon;
    }

    public double getDistanceHA() {
        return this.distanceHA;
    }

    public void setDistanceHA(double distanceHA) {
        this.distanceHA = distanceHA;
    }

    public double getDistanceDA() {
        return this.distanceDA;
    }

    public void setDistanceDA(double distanceDA) {
        this.distanceDA = distanceDA;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public int getFirstSourceAtom() {
        return this.protIsDon ? this.donor : this.acceptor;
    }

    @Override
    public int getFirstTargetAtom() {
        return this.protIsDon ? this.acceptor : this.donor;
    }

    @Override
    public String toString() {
        return "HydrogenBond{" +
                "donor=" + this.donor +
                ", acceptor=" + this.acceptor +
                ", protIsDon=" + this.protIsDon +
                ", plipIdentifier=" + this.plipIdentifier +
                ", source=" + this.source +
                ", target=" + this.target +
                ", ligandCoordinate=" + Arrays.toString(this.ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(this.proteinCoordinate) +
                '}';
    }
}
