package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ConcentrationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ConcentrationInitializer.class);

    private List<InitialConcentration> initialConcentrations;

    public ConcentrationInitializer() {
        initialConcentrations = new ArrayList<>();
    }

    public ConcentrationInitializer(List<InitialConcentration> initialConcentrations) {
        this.initialConcentrations = initialConcentrations;
    }

    public List<InitialConcentration> getInitialConcentrations() {
        return initialConcentrations;
    }

    public void setInitialConcentrations(List<InitialConcentration> initialConcentrations) {
        this.initialConcentrations = initialConcentrations;
    }

    public void addInitialConcentration(InitialConcentration initialConcentration) {
        int index = initialConcentrations.indexOf(initialConcentration);
        if (index != -1) {
            initialConcentrations.remove(initialConcentration);
            initialConcentrations.add(index,initialConcentration);
        } else {
            initialConcentrations.add(initialConcentration);
        }
    }


    public void addInitialConcentration(CellRegion region, CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        SectionConcentration initialConcentration = new SectionConcentration(region, subsection, entity, concentration);
        addInitialConcentration(initialConcentration);
    }

    public void addInitialConcentration(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        SectionConcentration initialConcentration = new SectionConcentration(subsection, entity, concentration);
        addInitialConcentration(initialConcentration);
    }

    public void initialize(Simulation simulation) {
        simulation.collectUpdatables();
        for (InitialConcentration initialConcentration : initialConcentrations) {
            logger.info("  {}", initialConcentration);
            initialConcentration.initialize(simulation);
        }
    }

}
