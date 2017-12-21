package de.bioforscher.singa.structure.parser.plip;


import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;

/**
 * @author cl
 */
public abstract class Interaction {

    int plipIdentifier;

    LeafIdentifier source;
    LeafIdentifier target;

    double[] ligandCoordinate;
    double[] proteinCoordinate;

    public Interaction(int plipIdentifier) {
        this.plipIdentifier = plipIdentifier;
    }

    public LeafIdentifier getSource() {
        return source;
    }

    public void setSource(LeafIdentifier source) {
        this.source = source;
    }

    public LeafIdentifier getTarget() {
        return target;
    }

    public void setTarget(LeafIdentifier target) {
        this.target = target;
    }

    public double[] getLigandCoordinate() {
        return ligandCoordinate;
    }

    public void setLigandCoordinate(double[] ligandCoordinate) {
        this.ligandCoordinate = ligandCoordinate;
    }

    public double[] getProteinCoordinate() {
        return proteinCoordinate;
    }

    public void setProteinCoordinate(double[] proteinCoordinate) {
        this.proteinCoordinate = proteinCoordinate;
    }

    public int getPlipIdentifier() {
        return plipIdentifier;
    }

    public void setPlipIdentifier(int plipIdentifier) {
        this.plipIdentifier = plipIdentifier;
    }

    abstract public int getFirstSourceAtom();

    abstract public int getFirstTargetAtom();

}
