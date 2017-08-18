package de.bioforscher.singa.chemistry.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class HydrophobicInteraction extends Interaction {

    private int atom1;
    private int atom2;
    private double distance;

    public HydrophobicInteraction(int plipIdentifier) {
        super(plipIdentifier);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getAtom1() {
        return atom1;
    }

    public void setAtom1(int atom1) {
        this.atom1 = atom1;
    }

    public int getAtom2() {
        return atom2;
    }

    public void setAtom2(int atom2) {
        this.atom2 = atom2;
    }

    @Override
    public int getFirstSourceAtom() {
        return atom1;
    }

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
                ", ligandCoordiante=" + Arrays.toString(ligandCoordiante) +
                ", proteinCoordinate=" + Arrays.toString(proteinCoordinate) +
                '}';
    }
}
