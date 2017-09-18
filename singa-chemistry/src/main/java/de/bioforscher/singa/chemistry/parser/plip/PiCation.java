package de.bioforscher.singa.chemistry.parser.plip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class PiCation extends Interaction {

    private List<Integer> atoms1;
    private List<Integer> atoms2;
    private double distance;
    private double offset;
    private boolean protcharged;
    private String ligandGroup;

    public PiCation(int plipIdentifier) {
        super(plipIdentifier);
        this.atoms1 = new ArrayList<>();
        this.atoms2 = new ArrayList<>();
    }

    public List<Integer> getAtoms1() {
        return this.atoms1;
    }

    public void setAtoms1(List<Integer> atoms1) {
        this.atoms1 = atoms1;
    }

    public List<Integer> getAtoms2() {
        return this.atoms2;
    }

    public void setAtoms2(List<Integer> atoms2) {
        this.atoms2 = atoms2;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public boolean isProtcharged() {
        return this.protcharged;
    }

    public void setProtcharged(boolean protcharged) {
        this.protcharged = protcharged;
    }

    public String getLigandGroup() {
        return this.ligandGroup;
    }

    public void setLigandGroup(String ligandGroup) {
        this.ligandGroup = ligandGroup;
    }

    @Override
    public int getFirstSourceAtom() {
        if (this.atoms1.iterator().hasNext()) {
            return this.atoms1.iterator().next();
        } else {
            return -1;
        }
    }

    @Override
    public int getFirstTargetAtom() {
        if (this.atoms2.iterator().hasNext()) {
            return this.atoms2.iterator().next();
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "PiCation{" +
                "atoms1=" + this.atoms1 +
                ", atoms2=" + this.atoms2 +
                ", plipIdentifier=" + this.plipIdentifier +
                ", source=" + this.source +
                ", target=" + this.target +
                ", ligandCoordinate=" + Arrays.toString(this.ligandCoordinate) +
                ", proteinCoordinate=" + Arrays.toString(this.proteinCoordinate) +
                '}';
    }
}
