package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

/**
 * @author cl
 */
public abstract class Interaction {

    int plipIdentifier;

    LeafIdentifier source;
    LeafIdentifier target;

    double[] ligandCoordiante;
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

    public double[] getLigandCoordiante() {
        return ligandCoordiante;
    }

    public void setLigandCoordiante(double[] ligandCoordiante) {
        this.ligandCoordiante = ligandCoordiante;
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
}
