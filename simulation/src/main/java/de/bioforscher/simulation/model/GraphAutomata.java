package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.core.events.UpdateEventEmitter;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.diffusion.Diffusion;
import de.bioforscher.simulation.diffusion.RecurrenceDiffusion;
import de.bioforscher.simulation.parser.EpochUpdateWriter;
import de.bioforscher.simulation.reactions.EnzymeReaction;
import de.bioforscher.simulation.reactions.Reaction;
import de.bioforscher.simulation.util.BioGraphUtilities;
import de.bioforscher.units.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is used to simulate diffusion and reactions.
 *
 * @author Christoph Leberecht
 */
public class GraphAutomata implements UpdateEventEmitter<NextEpochEvent> {

    private AutomatonGraph graph;
    private Diffusion diffusion;
    private List<ImmediateUpdate> immediateUpdates;

    private CopyOnWriteArrayList<UpdateEventListener<NextEpochEvent>> listeners;

    private EpochUpdateWriter updateWriter;

    public int epoch;

    public GraphAutomata(AutomatonGraph graph) {
        this(graph, new RecurrenceDiffusion(BioGraphUtilities.generateMapOfEntities(graph)));
    }

    public GraphAutomata(AutomatonGraph graph, Diffusion diffusion) {
        this.graph = graph;
        this.diffusion = diffusion;
        this.immediateUpdates = new ArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        initialize();
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
        for (BioNode node : this.graph.getNodes()) {
            if (node.isObserved()) {
                try {
                    this.updateWriter.addNodeToObserve(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        this.immediateUpdates.add(reaction);
        if (resetConcentrations) {
            for (BioNode node : this.graph.getNodes()) {
                if (reaction.getClass().equals(EnzymeReaction.class)) {
                    node.addEntity(((EnzymeReaction) reaction).getEnzyme(), 1.0);
                }
                for (Species species : reaction.getSubstrates()) {
                    node.addEntity(species, 1.0);
                }
                for (Species species : reaction.getProducts()) {
                    node.addEntity(species, 0.0);
                }
            }
            for (BioEdge edge : this.graph.getEdges()) {
                if (reaction.getClass().equals(EnzymeReaction.class)) {
                    edge.addSpeciesPermeability(((EnzymeReaction) reaction).getEnzyme(), 1.0);
                }
                for (Species species : reaction.getSubstrates()) {
                    edge.addSpeciesPermeability(species, 1.0);
                }
                for (Species species : reaction.getProducts()) {
                    edge.addSpeciesPermeability(species, 1.0);
                }
            }
        }
        if (this.diffusion.getClass().equals(RecurrenceDiffusion.class)) {
            ((RecurrenceDiffusion) this.diffusion)
                    .initializeDiffusionCoefficients(BioGraphUtilities.generateMapOfEntities(this.graph));
        }
    }

    /**
     * Calculates the next state of the system.
     *
     * @return
     */
    public AutomatonGraph next() {

        // HelperMap
        Map<Integer, Map<Species, Quantity<MolarConcentration>>> nextConcentrations = new HashMap<Integer, Map<Species, Quantity<MolarConcentration>>>();

        // diffusion
        for (BioNode node : this.graph.getNodes()) {
            if (node.isObserved()) {
                emitNextEpochEvent(node);
            }
            if (!node.isSource()) {
                nextConcentrations.put(node.getIdentifier(), this.diffusion.calculateConcentration(node));
            } else {
                nextConcentrations.put(node.getIdentifier(), node.getConcentrations());
            }
        }

        // update
        for (BioNode node : this.graph.getNodes()) {
            node.setConcentrations(nextConcentrations.get(node.getIdentifier()));
            // apply immediate updates
            for (ImmediateUpdate update : this.immediateUpdates) {
                update.updateConcentrations(node);
            }
        }

        this.epoch++;
        return this.graph;

    }

    private void emitNextEpochEvent(BioNode node) {
        NextEpochEvent event = new NextEpochEvent(this.epoch, node);
        emitEvent(event);
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<NextEpochEvent>> getListeners() {
        return this.listeners;
    }

}
