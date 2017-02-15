package de.bioforscher.simulation.endocytosis;

import de.bioforscher.simulation.model.BioNode;

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
