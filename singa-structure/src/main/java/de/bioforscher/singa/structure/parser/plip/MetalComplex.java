package de.bioforscher.singa.structure.parser.plip;

import java.util.Arrays;

/**
 * @author cl
 */
public class MetalComplex extends Interaction {

    private int atom1;
    private int atom2;
    private double distance;
    private double rms;
    private String geometry;
    private String location;
    private String metalType;
    private int coordination;
    private int complexnum;

    public MetalComplex(int plipIdentifier) {
        super(plipIdentifier);
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

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getRms() {
        return this.rms;
    }

    public void setRms(double rms) {
        this.rms = rms;
    }

    public String getGeometry() {
        return this.geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMetalType() {
        return this.metalType;
    }

    public void setMetalType(String metalType) {
        this.metalType = metalType;
    }

    public int getCoordination() {
        return this.coordination;
    }

    public void setCoordination(int coordination) {
        this.coordination = coordination;
    }

    public int getComplexnum() {
        return this.complexnum;
    }

    public void setComplexnum(int complexnum) {
        this.complexnum = complexnum;
    }

    @Override
    public int getFirstSourceAtom() {
        return this.atom2;
    }
    // take care here source is the metal ion

    @Override
    public int getFirstTargetAtom() {
        return this.atom1;
    }
    // and target is the partner

    @Override
    public String toString() {
        return "MetalComplex{" +
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
