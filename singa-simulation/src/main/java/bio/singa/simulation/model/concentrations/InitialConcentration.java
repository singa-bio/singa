package bio.singa.simulation.model.concentrations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.TreeMap;

import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class InitialConcentration {

    private static final Logger logger = LoggerFactory.getLogger(InitialConcentration.class);

    private TreeMap<Integer, ConcentrationCondition> conditions;
    private CellSubsection subsection;
    private CellTopology topology;
    private ChemicalEntity entity;
    private Quantity<MolarConcentration> concentration;
    private ComparableQuantity<Time> time;
    private boolean fix;

    private Evidence evidence;

    public InitialConcentration() {
        conditions = new TreeMap<>();
        time = Quantities.getQuantity(0, SECOND);
        fix = false;
    }

    public CellSubsection getSubsection() {
        return subsection;
    }

    public void setSubsection(CellSubsection subsection) {
        this.subsection = subsection;
    }

    public CellTopology getTopology() {
        return topology;
    }

    public void setTopology(CellTopology topology) {
        this.topology = topology;
    }

    public TreeMap<Integer, ConcentrationCondition> getConditions() {
        return conditions;
    }

    public void setConditions(TreeMap<Integer, ConcentrationCondition> conditions) {
        this.conditions = conditions;
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

    public ComparableQuantity<Time> getTime() {
        return time;
    }

    public void setTime(ComparableQuantity<Time> time) {
        this.time = time;
    }

    public boolean isFix() {
        return fix;
    }

    public void setFix(boolean fix) {
        this.fix = fix;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public void addCondition(ConcentrationCondition condition) {
        conditions.put(condition.getPriority(), condition);
        if (condition instanceof TopologyCondition) {
            topology = ((TopologyCondition) condition).getTopology();
            if (subsection != null) {
                logger.warn("Topology and subsection conditions have been set for the initial concentration. Subsection will be used.");
            }
        }
        if (condition instanceof SectionCondition) {
            subsection = ((SectionCondition) condition).getSubsection();
            if (topology != null) {
                logger.warn("Topology and subsection conditions have been set for the initial concentration. Subsection will be used.");
            }
        }
    }

    public boolean test(Updatable updatable) {
        for (ConcentrationCondition condition : conditions.values()) {
            // return if any condition fails
            if (!condition.test(updatable)) {
                return false;
            }
        }
        return true;
    }

    public void apply(Simulation simulation) {
        simulation.getUpdatables().forEach(this::apply);
    }

    public void apply(Updatable updatable) {
        if (test(updatable)) {
            if (subsection != null) {
                updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
            } else {
                updatable.getConcentrationContainer().initialize(topology, entity, concentration);
            }
            if (fix) {
                updatable.getConcentrationManager().fix(entity);
            }
        }
    }

    @Override
    public String toString() {
        String fixed = isFix() ? " [fixed] " : "";
        String timed = time.isGreaterThan(Quantities.getQuantity(0, SECOND)) ? " [" + time.toString() + "] " : "";
        String location = topology != null ? topology.toString() : subsection.getIdentifier();
        return "concentration" + timed + fixed + ": location = " + location + ", entity = " + entity.getIdentifier() + ", value = " + UnitRegistry.humanReadable(getConcentration());
    }
}
