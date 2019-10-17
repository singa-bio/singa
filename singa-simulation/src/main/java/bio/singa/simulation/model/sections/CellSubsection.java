package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.Objects;

/**
 * A cell subsection organizes the contents of a {@link Updatable}. Each subsection has its own {@link ConcentrationPool}
 * and behaves independently of other subsections in the same {@link CellRegion}.
 *
 * @author cl
 */
public class CellSubsection {

    /**
     * The identifier.
     */
    private String identifier;

    private GoTerm goTerm;

    private boolean membrane;

    /**
     * Creates a new cell subsection with the given identifier and dynamic preferred concentration unit.
     *
     * @param identifier The identifier.
     */
    public CellSubsection(String identifier) {
        this.identifier = identifier;
    }

    public CellSubsection(String identifier, GoTerm goTerm) {
        this.identifier = identifier;
        this.goTerm = goTerm;
    }

    public CellSubsection(String identifier, GoTerm goTerm, boolean membrane) {
        this.identifier = identifier;
        this.goTerm = goTerm;
        this.membrane = membrane;
    }

    /**
     * Returns the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public GoTerm getGoTerm() {
        return goTerm;
    }

    public void setGoTerm(GoTerm goTerm) {
        this.goTerm = goTerm;
    }

    public boolean isMembrane() {
        return membrane;
    }

    public void setMembrane(boolean membrane) {
        this.membrane = membrane;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellSubsection that = (CellSubsection) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

}
