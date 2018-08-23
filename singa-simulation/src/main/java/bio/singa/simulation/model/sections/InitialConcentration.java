package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;

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

    public void initialize(Simulation simulation) {
        for (Updatable updatable : simulation.getUpdatables()) {
            if (region == null || updatable.getCellRegion().equals(region)) {
                if (updatable.getCellRegion().getSubsections().contains(subsection)) {
                    updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
                }
            }
        }
    }


}
