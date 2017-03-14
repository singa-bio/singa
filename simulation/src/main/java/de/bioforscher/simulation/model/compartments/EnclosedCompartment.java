package de.bioforscher.simulation.model.compartments;

import de.bioforscher.mathematics.algorithms.graphs.ShortestPathFinder;
import de.bioforscher.simulation.model.graphs.BioNode;

import java.util.*;

/**
 * @author cl
 */
public class EnclosedCompartment extends CellSection {

    private Membrane enclosingMembrane;

    public EnclosedCompartment(String identifier, String name) {
        super(identifier, name);
    }

    public EnclosedCompartment(String identifier, String name, Set<BioNode> content) {
        super(identifier, name, content);
    }

    public Membrane generateMembrane() {
        LinkedList<BioNode> nodes = new LinkedList<>();
        getContent().forEach(node -> node.setState(NodeState.CYTOSOL));

        // TODO fix placement of borders along graph borders
        // TODO fix all the other problems :(

        // find starting point
        BioNode first = getContent().stream()
                .filter(bioNode -> bioNode.getNeighbours().stream()
                        .anyMatch(neighbour -> neighbour.getCellSection().getIdentifier().equals(this.getIdentifier())))
                .findAny().get();

        nodes.add(first);
        BioNode step = first;
        boolean notConnected = true;

        while (notConnected) {

            boolean foundNeighbour = false;
            // search neighbours
            for (BioNode neighbour : step.getNeighbours()) {
                if (isNewBorder(nodes, neighbour)) {
                    foundNeighbour = true;
                    nodes.add(neighbour);
                    neighbour.setState(NodeState.MEMBRANE);
                    step = neighbour;
                    break;
                }
            }

            // check if border can be closed
            if (!foundNeighbour) {
                for (BioNode neighbour : step.getNeighbours()) {
                    if (nodes.getFirst().equals(neighbour)) {
                        notConnected = false;
                        foundNeighbour = true;
                        nodes.add(neighbour);
                        neighbour.setState(NodeState.MEMBRANE);
                    }
                }
            }

            // try to traverse bridge
            if (!foundNeighbour) {
                LinkedList<BioNode> nextBest = ShortestPathFinder.trackBasedOnPredicates(step, currentNode -> this.isNewBorder(nodes, currentNode), this::isInThisCompartment);
                if (nextBest != null) {
                    for (BioNode node : nextBest) {
                        if (!nodes.contains(node)) {
                            nodes.add(node);
                            node.setState(NodeState.MEMBRANE);
                        }
                    }
                    step = nextBest.getLast();
                } else {
                    System.out.println("could not finish compartment border");
                    break;
                }

            }

        }

        this.enclosingMembrane = Membrane.forCompartment(this);
        this.enclosingMembrane.setContent(new HashSet<>(nodes));
        return this.enclosingMembrane;
    }

    private boolean isInThisCompartment(BioNode node) {
        return node.getCellSection().getIdentifier().equals(this.getIdentifier());
    }

    private boolean hasNeighbourInOtherCompartment(BioNode node) {
        for (BioNode neighbour : node.getNeighbours()) {
            if (!isInThisCompartment(neighbour)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewBorder(LinkedList<BioNode> oldNodes, BioNode currentNode) {
        return isInThisCompartment(currentNode) &&
                !oldNodes.contains(currentNode) &&
                hasNeighbourInOtherCompartment(currentNode);
    }

    public Membrane getEnclosingMembrane() {
        return this.enclosingMembrane;
    }

    public void setEnclosingMembrane(Membrane enclosingMembrane) {
        this.enclosingMembrane = enclosingMembrane;
    }

}
