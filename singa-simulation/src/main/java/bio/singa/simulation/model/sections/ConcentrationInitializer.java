package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ConcentrationInitializer {

    private List<InitialConcentration> initialConcentrations;

    public ConcentrationInitializer() {
        initialConcentrations = new ArrayList<>();
    }

    public List<InitialConcentration> getInitialConcentrations() {
        return initialConcentrations;
    }

    public void setInitialConcentrations(List<InitialConcentration> initialConcentrations) {
        this.initialConcentrations = initialConcentrations;
    }

    public void addInitialConcentration(InitialConcentration initialConcentration) {
        initialConcentrations.add(initialConcentration);
    }

    public void addInitialConcentration(CellRegion region, CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        initialConcentrations.add(new InitialConcentration(region, subsection, entity, concentration));
    }

    public void addInitialConcentration(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        initialConcentrations.add(new InitialConcentration(subsection, entity, concentration));
    }

    public void initialize(Simulation simulation) {
        simulation.collectUpdatables();
        for (InitialConcentration initialConcentration : initialConcentrations) {
            initialConcentration.initialize(simulation);
        }
    }

}
