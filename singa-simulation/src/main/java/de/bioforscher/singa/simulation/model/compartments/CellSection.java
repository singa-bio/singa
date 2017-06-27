package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.core.utility.Nameable;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

import java.util.HashSet;
import java.util.Set;

/**
 * A CellSection is everything that can be considered a part of the cell. It is characterized by a
 * {@link SimpleStringIdentifier}. The content of a cell section is defined by a set of nodes.
 *
 * @author cl
 */
public abstract class CellSection implements Nameable {

    /**
     * The identifier (should be unique).
     */
    private final String identifier;

    /**
     * Signifies if this compartment is a smaller part of another compartment (e.g. layer of a membrane)
     */
    private boolean isSubsection;

    /**
     * The qualified name.
     */
    private final String name;

    /**
     * The nodes belonging to this section.
     */
    private Set<BioNode> content;

    /**
     * Creates a new CellSection with the given identifier and name.
     *
     * @param identifier The identifier (should be unique).
     * @param name       The qualified name.
     */
    public CellSection(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        this.content = new HashSet<>();
    }

    /**
     * Returns the identifier of this cell section.
     * @return The identifier of this cell section.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns the nodes belonging to this cell section.
     * @return The nodes belonging to this section.
     */
    public Set<BioNode> getContent() {
        return this.content;
    }

    /**
     * Sets the content of this cell section.
     * @param content
     */
    public void setContent(Set<BioNode> content) {
        this.content = content;
    }

    /**
     * Adds a node to the section, without altering the state of the node.
     * @param node The node to add.
     */
    public void addNode(BioNode node) {
        this.content.add(node);
    }

    public boolean isSubsection() {
        return isSubsection;
    }

    public void setSubsection(boolean subsection) {
        isSubsection = subsection;
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

    @Override
    public String toString() {
        return "CellSection{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
