package de.bioforscher.singa.chemistry.parser.plip;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class PiStacking extends Interaction {

    private List<Integer> atoms1;
    private List<Integer> atoms2;
    private double distance;
    private double offset;
    private double angle;
    private String type;

    public PiStacking(int plipIdentifier) {
        super(plipIdentifier);
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

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void mergeWith(PiStacking other) {
        this.atoms1 = other.getAtoms2();
    }

    @Override
    public String toString() {
        return "PiStacking{" +
                "atoms1=" + atoms1 +
                ", atoms2=" + atoms2 +
                ", distance=" + distance +
                ", offset=" + offset +
                ", angle=" + angle +
                ", type='" + type + '\'' +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
