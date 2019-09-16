package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import java.util.Map;
import java.util.Objects;

import static bio.singa.simulation.model.sections.concentration.InitialConcentration.*;

/**
 * @author cl
 */
public class SectionConcentration implements InitialConcentration {

    private CellRegion region;

    private CellSubsection subsection;

    private ChemicalEntity entity;

    private Quantity<MolarConcentration> concentration;

    private Evidence evidence;

    SectionConcentration() {

    }

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
        // special treatment for cell cortex areas
        if (region != null && (region.equals(CellRegions.CELL_CORTEX) || region.getIdentifier().equals("perinuclear region of cytoplasm"))) {
            if (updatable instanceof AutomatonNode) {
                AutomatonNode node = (AutomatonNode) updatable;
                for (Map.Entry<CellSubsection, Polygon> entry : node.getSubsectionRepresentations().entrySet()) {
                    if (entry.getKey().equals(subsection)) {
                        if (entry.getValue().getCentroid().isInside(region.getAreaRepresentation())) {
                            updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
                        }
                    }
                }
            }
        }
        if (region == null || updatable.getCellRegion().equals(region)) {
            if (updatableContainsSubsection(updatable, subsection)) {
                updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
            }
        }
    }

    @Override
    public void initializeUnchecked(Updatable updatable, CellTopology topology) {
        updatable.getConcentrationContainer().initialize(topology, entity, concentration);
    }

    public CellRegion getCellRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "Concentration:" + (region == null ? "" : " R = " + region.getIdentifier()) +
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
