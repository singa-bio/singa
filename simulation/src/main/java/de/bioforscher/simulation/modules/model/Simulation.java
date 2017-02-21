package de.bioforscher.simulation.modules.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.core.events.UpdateEventEmitter;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.simulation.events.NodeUpdatedEvent;
import de.bioforscher.simulation.model.parameters.SimulationParameter;
import de.bioforscher.simulation.modules.diffusion.FreeDiffusion;
import de.bioforscher.simulation.model.rules.AssignmentRule;
import de.bioforscher.simulation.events.EpochUpdateWriter;
import de.bioforscher.simulation.util.EnvironmentalVariables;
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
                .map(Module::collectAllReferencesEntities)
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

    public void sortAssignmentsByPriority() {
        // assignments have to be done in a certain order, if they depend on other assignment rules
        // initialize assignment rules and their requirements
        Map<AssignmentRule, Set<ChemicalEntity<?>>> assignmentRequirements = new HashMap<>();
        // and the priority of the rule
        Map<AssignmentRule, Integer> priorityMap = new HashMap<>();
        for (AssignmentRule rule : this.assignmentRules) {
            assignmentRequirements.put(rule, new HashSet<>());
            priorityMap.put(rule, Integer.MAX_VALUE);
        }

        for (AssignmentRule targetRule : this.assignmentRules) {
            // rule provides
            ChemicalEntity<?> targetEntity = targetRule.getTargetEntity();
            // check if it is required elsewhere
            for (AssignmentRule sourceRule : this.assignmentRules) {
                if (sourceRule != targetRule) {
                    if (sourceRule.getEntityReference().keySet().contains(targetEntity)) {
                        assignmentRequirements.get(sourceRule).add(targetEntity);
                    }
                }
            }
        }

        List<AssignmentRule> handledRules = new ArrayList<>();
        List<ChemicalEntity<?>> suppliedEntities = new ArrayList<>();
        // rules without requirements to top
        for (Map.Entry<AssignmentRule, Set<ChemicalEntity<?>>> entry : assignmentRequirements.entrySet()) {
            if (entry.getValue().isEmpty()) {
                // if no requirements are needed assign priority 0
                priorityMap.put(entry.getKey(), 0);
                suppliedEntities.add(entry.getKey().getTargetEntity());
                handledRules.add(entry.getKey());
            }
        }

        boolean allAssigned = false;
        int level = 0;
        while (!allAssigned) {
            allAssigned = true;
            level++;
            for (Map.Entry<AssignmentRule, Set<ChemicalEntity<?>>> entry : assignmentRequirements.entrySet()) {
                if (!handledRules.contains(entry.getKey())) {
                    if (suppliedEntities.containsAll(entry.getValue())) {
                        priorityMap.put(entry.getKey(), level);
                        suppliedEntities.add(entry.getKey().getTargetEntity());
                        handledRules.add(entry.getKey());
                        allAssigned = false;
                        break;
                    }
                }
            }
        }

        this.assignmentRules = priorityMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

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
        this.assignmentRules = assignmentRules;
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
        return Quantities.getQuantity(EnvironmentalVariables.getInstance().getTimeStep().getValue().doubleValue() *
                this.epoch, EnvironmentalVariables.getInstance().getTimeStep().getUnit());
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
