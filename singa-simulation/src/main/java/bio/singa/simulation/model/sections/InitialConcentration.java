package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

import java.util.Objects;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class InitialConcentration {

    private CellRegion region;

    private CellSubsection subsection;

    private ChemicalEntity entity;

    private Quantity<MolarConcentration> concentration;

    public InitialConcentration(CellRegion region, CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.region = region;
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = concentration;
    }

    public InitialConcentration(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = concentration;
    }

    public InitialConcentration(CellRegion region, CellSubsection subsection, ChemicalEntity entity, double concentration) {
        this.region = region;
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = Quantities.getQuantity(concentration, MOLE_PER_LITRE);
    }

    public InitialConcentration(CellSubsection subsection, ChemicalEntity entity, double concentration) {
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = Quantities.getQuantity(concentration, MOLE_PER_LITRE);
    }

    public CellRegion getRegion() {
        return region;
    }

    public void setRegion(CellRegion region) {
        this.region = region;
    }

    public CellSubsection getSubsection() {
        return subsection;
    }

    public void setSubsection(CellSubsection subsection) {
        this.subsection = subsection;
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    public Quantity<MolarConcentration> getConcentration() {
        return concentration;
    }

    public void setConcentration(Quantity<MolarConcentration> concentration) {
        this.concentration = concentration;
    }

    public void initialize(Simulation simulation) {
        for (Updatable updatable : simulation.getUpdatables()) {
            if (region == null || updatable.getCellRegion().equals(region)) {
                if (updatable.getCellRegion().getSubsections().contains(subsection)) {
                    updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Concentration:" + (region == null ? " " : " R = "+region.getIdentifier()) +
                " S = " + subsection.getIdentifier() +
                " E = " + entity.getIdentifier() +
                " C = " + concentration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitialConcentration that = (InitialConcentration) o;
        return Objects.equals(region, that.region) &&
                Objects.equals(subsection, that.subsection) &&
                Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, subsection, entity);
    }

}
