package de.bioforscher.singa.structure.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class HydrogenBond extends Interaction {

    /**
     * The Atom ID of the donor atom of the halogen bond.
     */
    private int donor;

    /**
     * The Atom ID of the acceptor atom of the halogen bond.
     */
    private int acceptor;

    /**
     * Is the H-Bond formed with the sidechain of the protein?
     */
    private boolean sidechain;

    /**
     * Is protein the donor?
     */
    private boolean protIsDon;

    /**
     * The distance between the hydrogen bond's hydrogen and the acceptor atom in Angstrom.
     */
    private double distanceHA;

    /**
     * The distance between the hydrogen bond's donor and acceptor atoms in Angstrom.
     */
    private double distanceDA;

    /**
     * The angle at the donor.
     */
    private double angle;

    public HydrogenBond(int plipIdentifier) {
        super(plipIdentifier);
    }

    /**
     * Returns the Atom ID of the donor atom of the interaction.
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
     * @return The interaction's acceptor atom ID.
     */
    public int getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(int acceptor) {
        this.acceptor = acceptor;
    }

    /**
     * Returns whether the hydrogen bond is formed with the sidechain of the protein.
     * @return Whether the hydrogen bond is formed with the sidechain of the protein.
     */
    public boolean isSidechain() {
        return sidechain;
    }

    public void setSidechain(boolean sidechain) {
        this.sidechain = sidechain;
    }

    /**
     * Returns whether the protein is the donor.
     * @return Whether the protein is the donor.
     */
    public boolean isProtIsDon() {
        return protIsDon;
    }

    public void setProtIsDon(boolean protIsDon) {
        this.protIsDon = protIsDon;
    }

    /**
     * Returns the distance between the hydrogen bond's hydrogen and the acceptor atom in Angstrom.
     * @return The distance between the hydrogen bond's hydrogen and acceptor atom.
     */
    public double getDistanceHA() {
        return distanceHA;
    }

    public void setDistanceHA(double distanceHA) {
        this.distanceHA = distanceHA;
    }

    /**
     * Returns the distance between the hydrogen bond's donor and acceptor atoms in Angstrom.
     * @return The distance between the hydrogen bond's donor and acceptor atoms.
     */
    public double getDistanceDA() {
        return distanceDA;
    }

    public void setDistanceDA(double distanceDA) {
        this.distanceDA = distanceDA;
    }

    /**
     * Returns the angle at the donor.
     * @return The angle at the donor.
     */
    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Returns the Atom ID of the source atom of the interaction.
     * Decides whether ...
     * @return The interaction's source atom ID.
     */
    @Override
    public int getFirstSourceAtom() {
        return protIsDon ? donor : acceptor;
    }

    /**
     * Returns the Atom ID of the target atom of the interaction.
     * Decides whether ...
     * @return The interaction's target atom ID.
     */
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
