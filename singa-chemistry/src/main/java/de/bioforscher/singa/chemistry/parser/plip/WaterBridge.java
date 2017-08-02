package de.bioforscher.singa.chemistry.parser.plip;

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

    public double getDistanceAW() {
        return distanceAW;
    }

    public void setDistanceAW(double distanceAW) {
        this.distanceAW = distanceAW;
    }

    public double getDistanceDW() {
        return distanceDW;
    }

    public void setDistanceDW(double distanceDW) {
        this.distanceDW = distanceDW;
    }

    public double getDonorAngle() {
        return donorAngle;
    }

    public void setDonorAngle(double donorAngle) {
        this.donorAngle = donorAngle;
    }

    public double getWaterAngle() {
        return waterAngle;
    }

    public void setWaterAngle(double waterAngle) {
        this.waterAngle = waterAngle;
    }

    public boolean isProtIsDon() {
        return protIsDon;
    }

    public void setProtIsDon(boolean protIsDon) {
        this.protIsDon = protIsDon;
    }

    @Override
    public String toString() {
        return "WaterBridge{" +
                "donor=" + donor +
                ", acceptor=" + acceptor +
                ", distanceAW=" + distanceAW +
                ", distanceDW=" + distanceDW +
                ", donorAngle=" + donorAngle +
                ", waterAngle=" + waterAngle +
                ", protIsDon=" + protIsDon +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
