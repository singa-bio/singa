package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.core.utility.Nameable;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class CellSection implements Nameable {

    private final String identifier;
    private final String name;

    private Set<BioNode> content;

    public CellSection(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        this.content = new HashSet<>();
    }

    public CellSection(String identifier, String name, Set<BioNode> content) {
        this(identifier, name);
        this.content = content;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Set<BioNode> getContent() {
        return this.content;
    }

    public void setContent(Set<BioNode> content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellSection that = (CellSection) o;

        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return this.identifier != null ? this.identifier.hashCode() : 0;
    }
}
