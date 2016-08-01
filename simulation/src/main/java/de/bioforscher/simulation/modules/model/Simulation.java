package de.bioforscher.simulation.modules.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.core.events.UpdateEventEmitter;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.NodeUpdatedEvent;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 06.07.2016.
 */
public class Simulation implements UpdateEventEmitter<NodeUpdatedEvent> {

    private AutomatonGraph graph;
    private Set<Module> modules;
    private Set<ChemicalEntity> species;
    private int epoch;

    private CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> listeners;

    public Simulation() {
        this.modules = new HashSet<>();
        this.species = new HashSet<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.epoch = 0;
    }

    public Set<ChemicalEntity> collectAllReferencedEntities() {
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

    public Set<ChemicalEntity> getSpecies() {
        return this.species;
    }

    public void setSpecies(Set<ChemicalEntity> species) {
        this.species = species;
    }

    public int getEpoch() {
        return this.epoch;
    }

    public Quantity<Time> getElapsedTime() {
        return Quantities.getQuantity(EnvironmentalVariables.getInstance().getTimeStep().getValue().doubleValue() *
                this.epoch, EnvironmentalVariables.getInstance().getTimeStep().getUnit());
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
