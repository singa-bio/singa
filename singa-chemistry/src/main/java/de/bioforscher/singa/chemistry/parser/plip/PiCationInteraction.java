package de.bioforscher.singa.chemistry.parser.plip;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class PiCationInteraction extends Interaction {

    private List<Integer> atoms1;
    private List<Integer> atoms2;
    private double distance;
    private double offset;
    private boolean protcharged;
    private String ligandGroup;

    public PiCationInteraction() {
        this.atoms1 = new ArrayList<>();
        this.atoms2 = new ArrayList<>();
    }

    public List<Integer> getAtoms1() {
        return atoms1;
    }

    public void setAtoms1(List<Integer> atoms1) {
        this.atoms1 = atoms1;
    }

    public List<Integer> getAtoms2() {
        return atoms2;
    }

    public void setAtoms2(List<Integer> atoms2) {
        this.atoms2 = atoms2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public boolean isProtcharged() {
        return protcharged;
    }

    public void setProtcharged(boolean protcharged) {
        this.protcharged = protcharged;
    }

    public String getLigandGroup() {
        return ligandGroup;
    }

    public void setLigandGroup(String ligandGroup) {
        this.ligandGroup = ligandGroup;
    }

    @Override
    public String toString() {
        return "PiCationInteraction{" +
                "atoms1=" + atoms1 +
                ", atoms2=" + atoms2 +
                ", distance=" + distance +
                ", offset=" + offset +
                ", protcharged=" + protcharged +
                ", ligandGroup='" + ligandGroup + '\'' +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
