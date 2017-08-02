package de.bioforscher.singa.chemistry.parser.plip;

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
    public String toString() {
        return "HydrophobicInteraction{" +
                "atom1=" + atom1 +
                ", atom2=" + atom2 +
                ", distance=" + distance +
                ", source=" + source +
                ", target=" + target +
                ", ligandCoordiante=" + ligandCoordiante +
                ", proteinCoordinate=" + proteinCoordinate +
                '}';
    }
}
