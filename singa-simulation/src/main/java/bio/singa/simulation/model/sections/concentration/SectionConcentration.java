package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import java.util.Objects;

/**
 * @author cl
 */
public class SectionConcentration implements InitialConcentration {

    private CellRegion region;

    private CellSubsection subsection;

    private ChemicalEntity entity;

    private Quantity<MolarConcentration> concentration;

    private Evidence evidence;

    public SectionConcentration(CellRegion region, CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.region = region;
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = concentration;
    }

    public SectionConcentration(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = concentration;
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

    @Override
    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    @Override
    public void initialize(Updatable updatable) {
        if (region == null || updatable.getCellRegion().equals(region)) {
            if (updatable.getCellRegion().getSubsections().contains(subsection)) {
                updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
            }
        }
    }

    @Override
    public String toString() {
        return "Concentration:" + (region == null ? " " : " R = " + region.getIdentifier()) +
                " S = " + subsection.getIdentifier() +
                " E = " + entity.getIdentifier() +
                " C = " + concentration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionConcentration that = (SectionConcentration) o;
        return Objects.equals(region, that.region) &&
                Objects.equals(subsection, that.subsection) &&
                Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, subsection, entity);
    }

}
