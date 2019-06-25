package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author cl
 */
public class ConcentrationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ConcentrationInitializer.class);

    private List<InitialConcentration> initialConcentrations;
    private List<FixedConcentration> timedConcentrations;

    public ConcentrationInitializer() {
        initialConcentrations = new ArrayList<>();
        timedConcentrations = new ArrayList<>();
    }

    public ConcentrationInitializer(List<InitialConcentration> initialConcentrations) {
        this.initialConcentrations = initialConcentrations;
    }

    public List<InitialConcentration> getInitialConcentrations() {
        return initialConcentrations;
    }

    public List<FixedConcentration> getTimedConcentrations() {
        return timedConcentrations;
    }

    public void addInitialConcentration(InitialConcentration initialConcentration) {
        if (initialConcentration instanceof FixedConcentration) {
            FixedConcentration fixedConcentration = (FixedConcentration) initialConcentration;
            Quantity<Time> time = fixedConcentration.getTime();
            if (time != null) {
                timedConcentrations.add(fixedConcentration);
                return;
            }
        }
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

    public void initializeTimed(Simulation simulation) {
        ListIterator<FixedConcentration> iterator = timedConcentrations.listIterator();
        while (iterator.hasNext()) {
            FixedConcentration timedConcentration = iterator.next();
            if (timedConcentration.getTime().isLessThanOrEqualTo(simulation.getElapsedTime())) {
                logger.info("Initialized timed concentration {}.", timedConcentration);
                timedConcentration.initialize(simulation);
                iterator.remove();
            }
        }
    }

}
