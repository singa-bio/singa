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
import de.bioforscher.singa.simulation.modules.diffusion.FreeDiffusion;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class Simulation implements UpdateEventEmitter<NodeUpdatedEvent> {

    private AutomatonGraph graph;
    private Set<Module> modules;
    private List<AssignmentRule> assignmentRules;
    private Set<ChemicalEntity<?>> chemicalEntities;
    private Set<SimulationParameter> globalParameters;
    private int epoch;

    private CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> listeners;
    private EpochUpdateWriter writer;

    public Simulation() {
        this.modules = new HashSet<>();
        this.chemicalEntities = new HashSet<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.epoch = 0;
    }

    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.modules.stream()
                .map(Module::collectAllReferencedEntities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void nextEpoch() {
        this.modules.forEach(
                module -> module.applyTo(this.graph)
        );
        this.graph.getNodes().stream().filter(BioNode::isObserved).forEach(this::emitNextEpochEvent);
        this.epoch++;
    }

    public void applyAssignmentRules() {
        this.assignmentRules.forEach(rule ->
                this.graph.getNodes().forEach(rule::applyRule)
        );
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

    public int getEpoch() {
        return this.epoch;
    }

    public Quantity<Time> getElapsedTime() {
        return Quantities.getQuantity(EnvironmentalParameters.getInstance().getTimeStep().getValue().doubleValue() *
                this.epoch, EnvironmentalParameters.getInstance().getTimeStep().getUnit());
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

    public FreeDiffusion getFreeDiffusionModule() {
        // FIXME: probably temporary
        Optional<FreeDiffusion> diffusion = this.modules.stream()
                .filter(module -> module.getClass().equals(FreeDiffusion.class))
                .findFirst().map(module -> (FreeDiffusion) module);
        this.collectAllReferencedEntities();
        diffusion.get().prepareDiffusionCoefficients(this.getChemicalEntities());
        return diffusion.get();
    }

}
