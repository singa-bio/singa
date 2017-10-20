package de.bioforscher.singa.structure.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class WaterBridge extends Interaction {

    private int donor;
    private int acceptor;
    private double distanceAW;
    private double distanceDW;
    private double donorAngle;
    private double waterAngle;
    private boolean protIsDon;

    public WaterBridge(int plipIdentifier) {
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

    public double getDistanceAW() {
        return this.distanceAW;
    }

    public void setDistanceAW(double distanceAW) {
        this.distanceAW = distanceAW;
    }

    public double getDistanceDW() {
        return this.distanceDW;
    }

    public void setDistanceDW(double distanceDW) {
        this.distanceDW = distanceDW;
    }

    public double getDonorAngle() {
        return this.donorAngle;
    }

    public void setDonorAngle(double donorAngle) {
        this.donorAngle = donorAngle;
    }

    public double getWaterAngle() {
        return this.waterAngle;
    }

    public void setWaterAngle(double waterAngle) {
        this.waterAngle = waterAngle;
    }

    public boolean isProtIsDon() {
        return this.protIsDon;
    }

    public void setProtIsDon(boolean protIsDon) {
        this.protIsDon = protIsDon;
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
        return "WaterBridge{" +
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
