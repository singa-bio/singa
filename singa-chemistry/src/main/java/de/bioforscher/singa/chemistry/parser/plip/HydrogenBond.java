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
    public int getFirstSourceAtom() {
        return protIsDon ? donor : acceptor;
    }

    @Override
    public int getFirstTargetAtom() {
        return protIsDon ? acceptor : donor;
    }

    @Override
    public String toString() {
        return "HydrogenBond{" +
                "donor=" + donor +
                ", acceptor=" + acceptor +
                ", protIsDon=" + protIsDon +
                ", plipIdentifier=" + plipIdentifier +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordinate=" + Arrays.toString(ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(proteinCoordinate) +
                '}';
    }
}
