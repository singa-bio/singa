package de.bioforscher.singa.structure.parser.plip;


import de.bioforscher.singa.structure.model.graph.model.LeafIdentifier;

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
        return this.source;
    }

    public void setSource(LeafIdentifier source) {
        this.source = source;
    }

    public LeafIdentifier getTarget() {
        return this.target;
    }

    public void setTarget(LeafIdentifier target) {
        this.target = target;
    }

    public double[] getLigandCoordinate() {
        return this.ligandCoordinate;
    }

    public void setLigandCoordinate(double[] ligandCoordinate) {
        this.ligandCoordinate = ligandCoordinate;
    }

    public double[] getProteinCoordinate() {
        return this.proteinCoordinate;
    }

    public void setProteinCoordinate(double[] proteinCoordinate) {
        this.proteinCoordinate = proteinCoordinate;
    }

    public int getPlipIdentifier() {
        return this.plipIdentifier;
    }

    public void setPlipIdentifier(int plipIdentifier) {
        this.plipIdentifier = plipIdentifier;
    }

    abstract public int getFirstSourceAtom();

    abstract public int getFirstTargetAtom();

}
