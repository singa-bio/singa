package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;

public interface Node<NodeType extends Node<NodeType, VectorType>, VectorType extends Vector> {

    int getIdentifier();

    VectorType getPosition();

    void setPosition(VectorType position);

    void addNeighbour(NodeType node);

    List<NodeType> getNeighbours();

    int getDegree();

}
