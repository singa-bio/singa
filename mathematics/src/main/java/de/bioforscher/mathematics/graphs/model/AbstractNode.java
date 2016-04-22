package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode<NodeType extends Node<NodeType, VectorType>, VectorType extends Vector>
        implements Node<NodeType, VectorType> {

    private int identifier;
    private List<NodeType> neighbours;
    private VectorType position;

    public AbstractNode(int identifier) {
        this.identifier = identifier;
        this.neighbours = new ArrayList<NodeType>();
    }

    public AbstractNode(int identifier, VectorType position) {
        this.identifier = identifier;
        this.position = position;
        this.neighbours = new ArrayList<NodeType>();
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<NodeType> getNeighbours() {
        return this.neighbours;
    }

    public void setNeighbours(List<NodeType> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public void addNeighbour(NodeType node) {
        this.neighbours.add(node);
    }

    public void removeNeighbour(RegularNode node) {
        this.neighbours.remove(node);
    }

    public boolean hasNeighbour(RegularNode node) {
        return this.neighbours.contains(node);
    }

    @Override
    public VectorType getPosition() {
        return this.position;
    }

    public void setPosition(VectorType position) {
        this.position = position;
    }

    public int getDegree() {
        return this.neighbours.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.identifier;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        // TODO maybe (surely) this should not happen here
        NodeType other = (NodeType) obj;
        if (this.identifier != other.getIdentifier()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Node " + this.identifier;
    }

}
