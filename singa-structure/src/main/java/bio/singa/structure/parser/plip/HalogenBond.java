package bio.singa.structure.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class HalogenBond extends Interaction {

    /**
     * The Atom ID of the donor atom of the halogen bond.
     */
    private int donor;

    /**
     * The Atom ID of the acceptor atom of the halogen bond.
     */
    private int acceptor;

    /**
     * The distance between the interacting atoms or groups in Angstrom.
     */
    private double distance;

    /**
     * The angle at the donor.
     */
    private double donorAngle;

    /**
     * The angle at the acceptor.
     */
    private double acceptorAngle;

    public HalogenBond(int plipIdentifier) {
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
     * Returns the distance between both interacting atoms in Angstrom.
     * @return The distance between the interacting atoms.
     */
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the angle at the donor.
     * @return The angle at the donor.
     */
    public double getDonorAngle() {
        return donorAngle;
    }

    public void setDonorAngle(double donorAngle) {
        this.donorAngle = donorAngle;
    }

    /**
     * Returns the angle at the acceptor.
     * @return The angle at the acceptor.
     */
    public double getAcceptorAngle() {
        return acceptorAngle;
    }

    public void setAcceptorAngle(double acceptorAngle) {
        this.acceptorAngle = acceptorAngle;
    }

    /**
     * Returns the Atom ID of the source atom of the interaction.
     * Return is the same atom as donor.
     * @return The interaction's source atom ID.
     */
    @Override
    public int getFirstSourceAtom() {
        return donor;
    }

    /**
     * Returns the Atom ID of the target atom of the interaction.
     * Return is the same atom as acceptor.
     * @return The interaction's target atom ID.
     */
    @Override
    public int getFirstTargetAtom() {
        return acceptor;
    }

    @Override
    public String toString() {
        return "HalogenBond{" +
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
