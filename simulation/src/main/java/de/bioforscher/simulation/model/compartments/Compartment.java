package de.bioforscher.simulation.model.compartments;

import de.bioforscher.simulation.model.graphs.BioNode;

import java.util.Set;

/**
 * @author cl
 */
public class Compartment {

    private String identifier;

    private Set<BioNode> border;
    private Set<BioNode> content;

    public Compartment(String identifier, Set<BioNode> content) {
        this.identifier = identifier;
        this.content = content;
    }
}
