package de.bioforscher.singa.simulation.model.sections;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;

import javax.measure.Unit;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

/**
 * @author cl
 */
public class CellSubsection implements Observer {

    // default subsections (scale automatically with system dimension)
    // all other subsections are initialized with the transformed environmental concentration
    public static CellSubsection MEMBRANE = new CellSubsection("MEM");
    public static CellSubsection SECTION_A = new CellSubsection("SA");
    public static CellSubsection SECTION_B = new CellSubsection("SB");

    private String identifier;
    private Unit<MolarConcentration> preferredConcentrationUnit;

    public CellSubsection(String identifier) {
        this(identifier, true);
    }

    public CellSubsection(String identifier, boolean dynamicConcentration) {
        this.identifier = identifier;
        preferredConcentrationUnit = Environment.getConcentrationUnit();
        if (dynamicConcentration) {
            Environment.attachObserver(this);
        }
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
