package de.bioforscher.singa.structure.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class HydrophobicInteraction extends Interaction {

    /**
     * The Atom ID of the source atom of the interaction - belongs to the protein.
     */
    private int atom1;

    /**
     * The Atom ID of the target atom of the interaction - belongs to the ligand.
     */
    private int atom2;

    /**
     * The distance between the interacting atoms or groups in Angstrom.
     */
    private double distance;

    public HydrophobicInteraction(int plipIdentifier) {
        super(plipIdentifier);
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
     * Returns the Atom ID of the source atom of the interaction, belonging to the protein.
     * @return The interaction's source atom ID.
     */
    public int getAtom1() {
        return atom1;
    }

    public void setAtom1(int atom1) {
        this.atom1 = atom1;
    }

    /**
     * Returns the Atom ID of the target atom of the interaction, belonging to the ligand.
     * @return The interaction's target atom ID.
     */
    public int getAtom2() {
        return atom2;
    }

    public void setAtom2(int atom2) {
        this.atom2 = atom2;
    }

    /**
     * Returns the Atom ID of the source atom of the interaction, belonging to the protein.
     * This is the same atom as atom1.
     * @return The interaction's source atom ID.
     */
    @Override
    public int getFirstSourceAtom() {
        return atom1;
    }

    /**
     * Returns the Atom ID of the target atom of the interaction, belonging to the ligand.
     * This is the same atom as atom2.
     * @return The interaction's target atom ID.
     */
    @Override
    public int getFirstTargetAtom() {
        return atom2;
    }

    @Override
    public String toString() {
        return "HydrophobicInteraction{" +
                "atom1=" + atom1 +
                ", atom2=" + atom2 +
                ", plipIdentifier=" + plipIdentifier +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordinate=" + Arrays.toString(ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(proteinCoordinate) +
                '}';
    }
}
