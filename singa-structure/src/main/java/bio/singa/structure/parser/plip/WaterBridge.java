package bio.singa.structure.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class WaterBridge extends Interaction {

    /**
     * The Atom ID of the donor atom of the water bridge.
     */
    private int donor;

    /**
     * The Atom ID of the acceptor atom of the water bridge.
     */
    private int acceptor;

    /**
     * The distance between the acceptor and interacting atom from the water in Angstrom.
     */
    private double distanceAW;

    /**
     * The distance between the donor and water interacting atom from the water in Angstrom.
     */
    private double distanceDW;

    /**
     * The angle at the donor.
     */
    private double donorAngle;

    /**
     * The angle at the interacting water atoms.
     */
    private double waterAngle;

    /**
     * Is protein the donor?
     */
    private boolean protIsDon;

    public WaterBridge(int plipIdentifier) {
        super(plipIdentifier, InteractionType.WATER_BRIDGE);
    }

    /**
     * Returns the Atom ID of the donor atom of the interaction.
     *
     * @return The interaction's donor atom ID.
     */
    public int getDonor() {
        return donor;
    }

    public void setDonor(int donor) {
        this.donor = donor;
    }

    /**
     * Returns the Atom ID of the acceptor atom of the interaction.
     *
     * @return The interaction's acceptor atom ID.
     */
    public int getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(int acceptor) {
        this.acceptor = acceptor;
    }

    /**
     * Returns the distance between the acceptor and interacting atom from the water in Angstrom.
     *
     * @return Distance between the acceptor and interacting atom from the water.
     */
    public double getDistanceAW() {
        return distanceAW;
    }

    public void setDistanceAW(double distanceAW) {
        this.distanceAW = distanceAW;
    }

    /**
     * Returns the distance between the donor and water interacting atom from the water in Angstrom.
     *
     * @return Distance between the donor and water interacting atom from the water.
     */
    public double getDistanceDW() {
        return distanceDW;
    }

    public void setDistanceDW(double distanceDW) {
        this.distanceDW = distanceDW;
    }

    /**
     * Returns the angle at the donor.
     *
     * @return Angle at the donor.
     */
    public double getDonorAngle() {
        return donorAngle;
    }

    public void setDonorAngle(double donorAngle) {
        this.donorAngle = donorAngle;
    }

    /**
     * Returns the angle at the interacting water atoms.
     *
     * @return Angle at the interacting water atoms.
     */
    public double getWaterAngle() {
        return waterAngle;
    }

    public void setWaterAngle(double waterAngle) {
        this.waterAngle = waterAngle;
    }

    /**
     * Returns whether the protein is the donor.
     *
     * @return Whether the protein is the donor.
     */
    public boolean isProtIsDon() {
        return protIsDon;
    }

    public void setProtIsDon(boolean protIsDon) {
        this.protIsDon = protIsDon;
    }

    /**
     * Returns the Atom ID of the source atom of the interaction. Return is the same atom as donor.
     *
     * @return The interaction's source atom ID.
     */
    @Override
    public int getFirstSourceAtom() {
        return donor;
    }

    /**
     * Returns the Atom ID of the target atom of the interaction. Return is the same atom as acceptor.
     *
     * @return The interaction's target atom ID.
     */
    @Override
    public int getFirstTargetAtom() {
        return acceptor;
    }

    @Override
    public String toString() {
        return "WaterBridge{" +
                "donor=" + donor +
                ", acceptor=" + acceptor +
                ", plipIdentifier=" + plipIdentifier +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordinate=" + Arrays.toString(ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(proteinCoordinate) +
                '}';
    }
}
