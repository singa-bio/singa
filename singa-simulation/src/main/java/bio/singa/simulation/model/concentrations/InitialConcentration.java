package bio.singa.simulation.model.concentrations;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.features.model.AbstractQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.List;
import java.util.TreeMap;

/**
 * @author cl
 */
public class InitialConcentration extends AbstractQuantitativeFeature<MolarConcentration> {

    private static final Logger logger = LoggerFactory.getLogger(InitialConcentration.class);

    private TreeMap<Integer, ConcentrationCondition> conditions;
    private CellSubsection subsection;
    private CellTopology topology;
    private ChemicalEntity entity;
    private boolean fix;

    public InitialConcentration(Quantity<MolarConcentration> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
        initialize();
    }

    public InitialConcentration(Quantity<MolarConcentration> quantity, Evidence evidence) {
        super(quantity, evidence);
        initialize();
    }

    public InitialConcentration(Quantity<MolarConcentration> quantity) {
        super(quantity);
        initialize();
    }

    public InitialConcentration() {
        super();
        initialize();
    }

    private void initialize() {
        FeatureRegistry.addQuantitativeFeature(this);
        conditions = new TreeMap<>();
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

    public String getLocation() {
        return topology != null ? topology.toString() : subsection.getIdentifier();
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
        return featureContent;
    }

    public void setConcentration(Quantity<MolarConcentration> concentration) {
        featureContent = concentration;
        baseContent = concentration;
    }

    public boolean isFix() {
        return fix;
    }

    public void setFix(boolean fix) {
        this.fix = fix;
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
            if (condition instanceof TimedCondition) {
                setFix(false);
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
                updatable.getConcentrationContainer().initialize(subsection, entity, featureContent);
            } else {
                updatable.getConcentrationContainer().initialize(topology, entity, featureContent);
            }
            if (fix) {
                updatable.getConcentrationManager().fix(entity);
            }
        }
    }

    @Override
    public String toString() {
        String fixed = isFix() ? " [fixed] " : "";
        String location = getLocation();
        return "concentration" + fixed + ": location = " + location + ", entity = " + entity.getIdentifier() + ", value = " + UnitRegistry.humanReadable(getConcentration());
    }
}
