package de.bioforscher.singa.simulation.model.sections;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

/**
 * A cell subsection organizes the contents of a {@link Updatable}. Each subsection has its own {@link ConcentrationPool}
 * and behaves independently of other subsections in the same {@link CellRegion}.
 *
 * @author cl
 */
public class CellSubsection implements Observer {

    /**
     * A general membrane subsection.
     */
    public static CellSubsection MEMBRANE = new CellSubsection("MEM");

    /**
     * A cellular subsection "A".
     */
    public static CellSubsection SECTION_A = new CellSubsection("SA");

    /**
     * A cellular subsection "B".
     */
    public static CellSubsection SECTION_B = new CellSubsection("SB");

    /**
     * The identifier.
     */
    private String identifier;

    /**
     * The preferred concentration unit.
     */
    private Unit<MolarConcentration> preferredConcentrationUnit;

    /**
     * Creates a new cell subsection with the given identifier and dynamic preferred concentration unit.
     *
     * @param identifier The identifier.
     */
    public CellSubsection(String identifier) {
        this(identifier, true);
    }

    /**
     * Creates a new cell subsection.
     *
     * @param identifier The identifier.
     * @param dynamicConcentration True if the preferred concentration unit should be updated if subsection volume
     * changes.
     */
    public CellSubsection(String identifier, boolean dynamicConcentration) {
        this.identifier = identifier;
        preferredConcentrationUnit = Environment.getConcentrationUnit();
        if (dynamicConcentration) {
            Environment.attachObserver(this);
        }
    }

    /**
     * Returns the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the preferred concentration unit for this subsection.
     *
     * @return The preferred concentration unit.
     */
    public Unit<MolarConcentration> getPreferredConcentrationUnit() {
        return preferredConcentrationUnit;
    }

    /**
     * Sets the preferred concentration unit for this subsection.
     *
     * @param preferredConcentrationUnit The preferred concentration unit.
     */
    public void setPreferredConcentrationUnit(Unit<MolarConcentration> preferredConcentrationUnit) {
        this.preferredConcentrationUnit = preferredConcentrationUnit;
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

    @Override
    public void update(Observable o, Object arg) {
        setPreferredConcentrationUnit(Environment.getConcentrationUnit());
    }
}
