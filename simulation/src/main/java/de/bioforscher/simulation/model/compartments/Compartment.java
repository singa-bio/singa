package de.bioforscher.simulation.model.compartments;

import de.bioforscher.core.utility.Nameable;
import de.bioforscher.mathematics.algorithms.graphs.ShortestPathFinder;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioEdge;
import de.bioforscher.simulation.model.graphs.BioNode;

import java.util.*;

/**
 * @author cl
 */
public class Compartment implements Nameable {

    private final String identifier;
    private final String name;

    private LinkedList<BioNode> border;
    private Set<BioNode> content;

    public Compartment(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        this.content = new HashSet<>();
        this.border = new LinkedList<>();
    }

    public Compartment(String identifier, String name, Set<BioNode> content) {
        this(identifier, name);
        this.content = content;
    }

    public void generateBorder(AutomatonGraph automatonGraph) {
        this.border = new LinkedList<>();
        this.content.forEach(node -> node.setState(NodeState.CYTOSOL));

        // TODO fix placement of borders along graph borders
        // TODO fix all the other problems :(

        // find starting point
        BioNode first = this.content.stream()
                .filter(bioNode -> bioNode.getNeighbours().stream()
                        .anyMatch(neighbour -> neighbour.getContainingCompartment().equals(this.getIdentifier())))
                .findAny().get();


        this.border.add(first);
        BioNode step = first;
        boolean notConnected = true;
        ShortestPathFinder<BioNode> pathFinder = new ShortestPathFinder<>();

        while (notConnected) {

            boolean foundNeighbour = false;
            // search neighbours
            for (BioNode neighbour : step.getNeighbours()) {
                if (isNewBorder(neighbour)) {
                    foundNeighbour = true;
                    this.border.add(neighbour);
                    neighbour.setState(NodeState.MEMBRANE);
                    step = neighbour;
                    break;
                }
            }

            // check if border can be closed
            if (!foundNeighbour) {
                for (BioNode neighbour : step.getNeighbours()) {
                    if (this.border.getFirst().equals(neighbour)) {
                        notConnected = false;
                        foundNeighbour = true;
                        this.border.add(neighbour);
                        neighbour.setState(NodeState.MEMBRANE);
                    }
                }
            }

            // try to traverse bridge
            if (!foundNeighbour) {
                LinkedList<BioNode> nextBest = pathFinder.trackBasedOnPredicates(step, this::isNewBorder, this::isInThisCompartment);
                if (nextBest != null) {
                    for (BioNode node : nextBest) {
                        if (!this.border.contains(node)) {
                            this.border.add(node);
                            node.setState(NodeState.MEMBRANE);
                        }
                    }
                    step = nextBest.getLast();
                } else {
                    System.out.println("could not finish compartment border");
                    return;
                }

            }

        }

    }

    private boolean isInThisCompartment(BioNode node) {
        return node.getContainingCompartment().equals(this.getIdentifier());
    }

    private boolean hasNeighbourInOtherCompartment(BioNode node) {
        for (BioNode neighbour : node.getNeighbours()) {
            if (!isInThisCompartment(neighbour)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewBorder(BioNode node) {
        return isInThisCompartment(node) &&
                !this.border.contains(node) &&
                hasNeighbourInOtherCompartment(node);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public LinkedList<BioNode> getBorder() {
        return this.border;
    }

    public void setBorder(LinkedList<BioNode> border) {
        this.border = border;
    }

    public Set<BioNode> getContent() {
        return this.content;
    }

    public void setContent(Set<BioNode> content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Compartment that = (Compartment) o;

        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return this.identifier != null ? this.identifier.hashCode() : 0;
    }

}
