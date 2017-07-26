package de.bioforscher.singa.chemistry.parser.plip;

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getRms() {
        return rms;
    }

    public void setRms(double rms) {
        this.rms = rms;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMetalType() {
        return metalType;
    }

    public void setMetalType(String metalType) {
        this.metalType = metalType;
    }

    public int getCoordination() {
        return coordination;
    }

    public void setCoordination(int coordination) {
        this.coordination = coordination;
    }

    public int getComplexnum() {
        return complexnum;
    }

    public void setComplexnum(int complexnum) {
        this.complexnum = complexnum;
    }

    @Override
    public String toString() {
        return "MetalComplex{" +
                "atom1=" + atom1 +
                ", atom2=" + atom2 +
                ", distance=" + distance +
                ", rms=" + rms +
                ", geometry='" + geometry + '\'' +
                ", location='" + location + '\'' +
                ", metalType='" + metalType + '\'' +
                ", coordination=" + coordination +
                ", complexnum=" + complexnum +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
