package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.core.events.UpdateEventEmitter;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.deprecated.Diffusion;
import de.bioforscher.simulation.deprecated.Reaction;
import de.bioforscher.simulation.deprecated.RecurrenceDiffusion;
import de.bioforscher.simulation.modules.diffusion.FreeDiffusion;
import de.bioforscher.simulation.parser.EpochUpdateWriter;
import de.bioforscher.simulation.reactions.EnzymeReaction;
import de.bioforscher.simulation.util.BioGraphUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is used to simulate diffusion and reactions.
 *
 * @author Christoph Leberecht
 */
public class GraphAutomaton implements UpdateEventEmitter<NextEpochEvent> {

    private AutomatonGraph graph;
    private FreeDiffusion diffusion;

    private List<Reaction> reactions;
    private Map<String, ChemicalEntity> species;

    private CopyOnWriteArrayList<UpdateEventListener<NextEpochEvent>> listeners;

    private EpochUpdateWriter updateWriter;

    public int epoch;

    public GraphAutomaton(AutomatonGraph graph) {
        this(graph, new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph)));
    }

    public GraphAutomaton(AutomatonGraph graph, Diffusion diffusion) {
        this.graph = graph;
        this.diffusion = new FreeDiffusion();
        this.reactions = new ArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        initializeChemicalEntitiesFromGraph(this.graph);
        initialize();
    }

    private void initializeChemicalEntitiesFromGraph(AutomatonGraph graph) {
        this.species = BioGraphUtilities.generateMapOfEntities(graph);
    }

    public void initialize() {
        // setup listener to write results
        // ZonedDateTime now = ZonedDateTime.now();
        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd
        // HH-mm-ss");
        // try {
        // this.updateWriter = new EpochUpdateWriter(Paths.get("data/"),
        // Paths.get("Simulation " + dtf.format(now)),
        // BioGraphUtilities.generateMapOfEntities(this.graph));
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // this.addEventListener(this.updateWriter);
    }

    /**
     * This method must be called after the nodes that have to be observed have
     * been chosen. Than the files to write are initialized and are ready to
     * receive updates.
     */
    public void activateWriteObservedNodesToFiles() {
        this.graph.getNodes().stream().filter(BioNode::isObserved).forEach(node -> {
            try {
                this.updateWriter.addNodeToObserve(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
    }

    /**
     * Adds a reaction.
     *
     * @param reaction            The reaction.
     * @param resetConcentrations If true, the species defined by the reaction are added to the
     *                            graph. Substrates with a concentration of 1 mol/l and products
     *                            with a concentration of 0 mol/l.
     */
    public void addReaction(Reaction reaction, boolean resetConcentrations) {
        this.reactions.add(reaction);
        if (resetConcentrations) {
            for (BioNode node : this.graph.getNodes()) {
                if (reaction.getClass().equals(EnzymeReaction.class)) {
                    node.addEntity(((EnzymeReaction) reaction).getEnzyme(), 1.0);
                }
                for (ChemicalEntity species : reaction.getSubstrates()) {
                    node.addEntity(species, 1.0);
                }
                for (ChemicalEntity species : reaction.getProducts()) {
                    node.addEntity(species, 0.0);
                }
            }
            for (BioEdge edge : this.graph.getEdges()) {
                if (reaction.getClass().equals(EnzymeReaction.class)) {
                    edge.addPermeability(((EnzymeReaction) reaction).getEnzyme(), 1.0);
                }
                for (ChemicalEntity species : reaction.getSubstrates()) {
                    edge.addPermeability(species, 1.0);
                }
                for (ChemicalEntity species : reaction.getProducts()) {
                    edge.addPermeability(species, 1.0);
                }
            }
        }
    }

    /**
     * Calculates the next state of the system.
     *
     * @return
     */
    public AutomatonGraph next() {

        this.diffusion.applyTo(this.graph);

        // update
        for (BioNode node : this.graph.getNodes()) {
            //    node.setConcentrations(nextConcentrations.get(node.getIdentifier()));
            // applyTo immediate updates
            for (Reaction reaction : this.reactions) {
                reaction.updateConcentrations(node);
            }
            if (node.isObserved()) {
                emitNextEpochEvent(node);
            }
        }

        this.epoch++;
        return this.graph;

    }

    private void emitNextEpochEvent(BioNode node) {
        NextEpochEvent event = new NextEpochEvent(this.epoch, node);
        emitEvent(event);
    }


    public List<Reaction> getReactions() {
        return this.reactions;
    }
    @Override
    public CopyOnWriteArrayList<UpdateEventListener<NextEpochEvent>> getListeners() {
        return this.listeners;
    }

    public Map<String, ChemicalEntity> getSpecies() {
        return this.species;
    }

    public void setSpecies(Map<String, ChemicalEntity> species) {
        this.species = species;
    }
}
