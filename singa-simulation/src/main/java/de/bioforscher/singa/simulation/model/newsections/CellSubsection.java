package de.bioforscher.singa.simulation.model.newsections;

import de.bioforscher.singa.features.quantities.MolarConcentration;

import javax.measure.Unit;
import java.util.Objects;

/**
 * @author cl
 */
public class CellSubsection {

    // one default membrane
    public static CellSubsection MEMBRANE = new CellSubsection("MEM");
    public static CellSubsection SECTION_A = new CellSubsection( "S_A");
    public static CellSubsection SECTION_B = new CellSubsection("S_B");

    private String identifier;
    private Unit<MolarConcentration> preferredConcentrationUnit;

    public CellSubsection(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Unit<MolarConcentration> getPreferredConcentrationUnit() {
        return preferredConcentrationUnit;
    }

    public void setPreferredConcentrationUnit(Unit<MolarConcentration> preferredConcentrationUnit) {
        this.preferredConcentrationUnit = preferredConcentrationUnit;
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
