package de.bioforscher.singa.structure.parser.plip;

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
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getAtom1() {
        return this.atom1;
    }

    public void setAtom1(int atom1) {
        this.atom1 = atom1;
    }

    public int getAtom2() {
        return this.atom2;
    }

    public void setAtom2(int atom2) {
        this.atom2 = atom2;
    }

    @Override
    public int getFirstSourceAtom() {
        return this.atom1;
    }

    @Override
    public int getFirstTargetAtom() {
        return this.atom2;
    }

    @Override
    public String toString() {
        return "HydrophobicInteraction{" +
                "atom1=" + this.atom1 +
                ", atom2=" + this.atom2 +
                ", plipIdentifier=" + this.plipIdentifier +
                ", source=" + this.source +
                ", target=" + this.target +
                ", ligandCoordinate=" + Arrays.toString(this.ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(this.proteinCoordinate) +
                '}';
    }
}
