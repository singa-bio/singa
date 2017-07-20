package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.core.events.UpdateEventEmitter;
import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.events.EpochUpdateWriter;
import de.bioforscher.singa.simulation.events.NodeUpdatedEvent;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AssignmentRule;
import de.bioforscher.singa.simulation.model.rules.AssignmentRules;
import de.bioforscher.singa.simulation.modules.timing.TimeStepHarmonizer;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class Simulation implements UpdateEventEmitter<NodeUpdatedEvent> {

    private AutomatonGraph graph;
    private Set<Module> modules;
    private List<AssignmentRule> assignmentRules;
    private Set<ChemicalEntity<?>> chemicalEntities;
    private Set<SimulationParameter> globalParameters;
    private long epoch;
    private Quantity<Time> elapsedTime;
    private TimeStepHarmonizer harmonizer;

    private CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> listeners;
    private EpochUpdateWriter writer;

    public Simulation() {
        this.modules = new HashSet<>();
        this.chemicalEntities = new HashSet<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.elapsedTime = Quantities.getQuantity(0.0, MICRO(SECOND));
        this.epoch = 0;
        this.harmonizer = new TimeStepHarmonizer(Quantities.getQuantity(10, NANO(SECOND)));
    }

    public void nextEpoch() {
        // apply all modules
        boolean timeStepChanged = this.harmonizer.determineHarmonicTimeStep();
        // apply generated deltas
        for (BioNode node : this.getGraph().getNodes()) {
            node.applyDeltas();
            if (node.isObserved()) {
                this.emitNextEpochEvent(node);
            }
        }
        // update epoch and elapsed time
        updateEpoch();
        // if time step did not change
        if (!timeStepChanged) {
            // try larger time step next time
            this.harmonizer.increateTimeStep();
        }
    }

    private void updateEpoch() {
        this.epoch++;
        this.elapsedTime = this.elapsedTime.add(EnvironmentalParameters.getInstance().getTimeStep());
    }

    public void applyAssignmentRules() {
        for (AssignmentRule rule : this.assignmentRules) {
            for (BioNode bioNode : this.graph.getNodes()) {
                rule.applyRule(bioNode);
            }
        }
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
    }

    public Set<Module> getModules() {
        return this.modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public List<AssignmentRule> getAssignmentRules() {
        return this.assignmentRules;
    }

    public void setAssignmentRules(List<AssignmentRule> assignmentRules) {
        this.assignmentRules = AssignmentRules.sortAssignmentRulesByPriority(assignmentRules);

    }

    public Set<ChemicalEntity<?>> getChemicalEntities() {
        return this.chemicalEntities;
    }

    public void setChemicalEntities(Set<ChemicalEntity<?>> chemicalEntities) {
        this.chemicalEntities = chemicalEntities;
    }

    public long getEpoch() {
        return this.epoch;
    }

    public Quantity<Time> getElapsedTime() {
        return this.elapsedTime;
    }

    public EpochUpdateWriter getWriter() {
        return this.writer;
    }

    public void setWriter(EpochUpdateWriter writer) {
        this.writer = writer;
        this.listeners.add(writer);
    }

    private void emitNextEpochEvent(BioNode node) {
        NodeUpdatedEvent event = new NodeUpdatedEvent(this.epoch, node);
        emitEvent(event);
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> getListeners() {
        return this.listeners;
    }

}
