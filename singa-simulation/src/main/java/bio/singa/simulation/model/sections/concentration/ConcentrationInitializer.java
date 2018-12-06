package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Quantity;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class ConcentrationInitializer {

    private Set<SectionConcentration> initialConcentrations;

    public ConcentrationInitializer() {
        initialConcentrations = new HashSet<>();
    }

    public ConcentrationInitializer(Set<SectionConcentration> initialConcentrations) {
        this.initialConcentrations = initialConcentrations;
    }

    public Set<SectionConcentration> getInitialConcentrations() {
        return initialConcentrations;
    }

    public void setInitialConcentrations(Set<SectionConcentration> initialConcentrations) {
        this.initialConcentrations = initialConcentrations;
    }

    public void addInitialConcentration(SectionConcentration initialConcentration) {
        initialConcentrations.remove(initialConcentration);
        initialConcentrations.add(initialConcentration);
    }

    public void addInitialConcentration(CellRegion region, CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        SectionConcentration initialConcentration = new SectionConcentration(region, subsection, entity, concentration);
        initialConcentrations.remove(initialConcentration);
        initialConcentrations.add(initialConcentration);
    }

    public void addInitialConcentration(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        SectionConcentration initialConcentration = new SectionConcentration(subsection, entity, concentration);
        initialConcentrations.remove(initialConcentration);
        initialConcentrations.add(initialConcentration);
    }

    public void initialize(Simulation simulation) {
        simulation.collectUpdatables();
        for (SectionConcentration initialConcentration : initialConcentrations) {
            initialConcentration.initialize(simulation);
        }
    }

}
