package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.mathematics.algorithms.graphs.ShortestPathFinder;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * An EnclosedCompartment is a {@link CellSection} that is bordered or enclosed by a {@link Membrane}.
 *
 * @author cl
 */
public class EnclosedCompartment extends CellSection {

    private static final Logger logger = LoggerFactory.getLogger(EnclosedCompartment.class);

    /**
     * The enclosing membrane.
     */
    private Membrane enclosingMembrane;

     /**
     * Creates a new EnclosedCompartment with the given identifier and name.
     *
     * @param identifier The identifier (should be unique).
     * @param name       The qualified name.
     */
    public EnclosedCompartment(String identifier, String name) {
        super(identifier, name);
    }

    /**
     * Tries to generate a {@link Membrane} around the contents of this compartment. This methods looks for neighbours
     * that are not part of this compartment and generates the membrane following this border.
     *
     * @return The generated membrane
     */
    public Membrane generateMembrane() {
        // TODO fix placement of borders along graph borders
        // TODO fix all the other problems :(

        // the nodes of the membrane
        LinkedList<AutomatonNode> nodes = new LinkedList<>();
        // set the internal node state to cytosol
        getContent().forEach(node -> node.setState(NodeState.CYTOSOL));
        // find starting point
        AutomatonNode first = getContent().stream()
                .filter(bioNode -> bioNode.getNeighbours().stream()
                        .anyMatch(neighbour -> neighbour.getCellSection().getIdentifier().equals(getIdentifier())))
                .findAny().get();
        // add first node
        nodes.add(first);
        // the iterating node
        AutomatonNode step = first;
        // remembers if a connection around the compartment could be made
        boolean notConnected = true;
        // as lon as no connection could be found
        while (notConnected) {

            boolean foundNeighbour = false;
            // search neighbours
            for (AutomatonNode neighbour : step.getNeighbours()) {
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
                for (AutomatonNode neighbour : step.getNeighbours()) {
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
                LinkedList<AutomatonNode> nextBest = ShortestPathFinder.trackBasedOnPredicates(step,
                        currentNode -> isNewBorder(nodes, currentNode), this::isInThisCompartment);
                if (nextBest != null) {
                    for (AutomatonNode node : nextBest) {
                        if (!nodes.contains(node)) {
                            nodes.add(node);
                            node.setState(NodeState.MEMBRANE);
                        }
                    }
                    step = nextBest.getLast();
                } else {
                    logger.error("Could not finish compartment membrane.");
                    break;
                }

            }

        }

        enclosingMembrane = Membrane.forCompartment(this);
        enclosingMembrane.setContent(new HashSet<>(nodes));
        return enclosingMembrane;
    }

    private boolean isInThisCompartment(AutomatonNode node) {
        return node.getCellSection().getIdentifier().equals(getIdentifier());
    }

    private boolean hasNeighbourInOtherCompartment(AutomatonNode node) {
        for (AutomatonNode neighbour : node.getNeighbours()) {
            if (!isInThisCompartment(neighbour)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewBorder(LinkedList<AutomatonNode> oldNodes, AutomatonNode currentNode) {
        return isInThisCompartment(currentNode) &&
                !oldNodes.contains(currentNode) &&
                hasNeighbourInOtherCompartment(currentNode);
    }

    public Membrane getEnclosingMembrane() {
        return enclosingMembrane;
    }

    public void setEnclosingMembrane(Membrane enclosingMembrane) {
        this.enclosingMembrane = enclosingMembrane;
    }

}
