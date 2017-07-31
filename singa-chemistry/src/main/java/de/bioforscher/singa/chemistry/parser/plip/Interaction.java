package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

/**
 * @author cl
 */
public abstract class Interaction {

    int plipIdentifier;

    LeafIdentifier source;
    LeafIdentifier target;

    Vector3D ligandCoordiante;
    Vector3D proteinCoordinate;

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

    public Vector3D getLigandCoordiante() {
        return ligandCoordiante;
    }

    public void setLigandCoordiante(Vector3D ligandCoordiante) {
        this.ligandCoordiante = ligandCoordiante;
    }

    public Vector3D getProteinCoordinate() {
        return proteinCoordinate;
    }

    public void setProteinCoordinate(Vector3D proteinCoordinate) {
        this.proteinCoordinate = proteinCoordinate;
    }

    public int getPlipIdentifier() {
        return plipIdentifier;
    }

    public void setPlipIdentifier(int plipIdentifier) {
        this.plipIdentifier = plipIdentifier;
    }
}
