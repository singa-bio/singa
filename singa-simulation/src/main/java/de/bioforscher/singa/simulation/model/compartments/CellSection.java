package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.core.utility.Nameable;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

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
     * The qualified name.
     */
    private final String name;
    /**
     * Signifies if this compartment is a smaller part of another compartment (e.g. layer of a membrane)
     */
    private boolean isSubsection;
    /**
     * The nodes belonging to this section.
     */
    private Set<AutomatonNode> content;

    /**
     * Creates a new CellSection with the given identifier and name.
     *
     * @param identifier The identifier (should be unique).
     * @param name The qualified name.
     */
    public CellSection(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        content = new HashSet<>();
    }

    /**
     * Returns the identifier of this cell section.
     *
     * @return The identifier of this cell section.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the nodes belonging to this cell section.
     *
     * @return The nodes belonging to this section.
     */
    public Set<AutomatonNode> getContent() {
        return content;
    }

    /**
     * Sets the content of this cell section.
     *
     * @param content The nodes contained in this cell section.
     */
    public void setContent(Set<AutomatonNode> content) {
        this.content = content;
    }

    /**
     * Adds a node to the section, without altering the state of the node.
     *
     * @param node The node to add.
     */
    public void addNode(AutomatonNode node) {
        content.add(node);
    }

    /**
     * Returns {@code true} if this cell section is a subsection of another section.
     *
     * @return {@code true} if this cell section is a subsection of another section.
     */
    public boolean isSubsection() {
        return isSubsection;
    }

    /**
     * Set {@code true} if this cell section is a subsection of another section.
     *
     * @param subsection {@code true} if this cell section is a subsection of another section.
     */
    public void setSubsection(boolean subsection) {
        isSubsection = subsection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellSection that = (CellSection) o;

        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CellSection{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
