package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.core.events.UpdateEventListener;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.events.*;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

/**
 * Changes in simulations can be observed by tagging {@link AutomatonNode}s of the {@link AutomatonGraph}. As a standard
 * implementation there is the {@link EpochUpdateWriter} that can be added to the Simulation that will write log files
 * to the specified file locations.
 *
 * @author cl
 */
public class SimulationManager extends Task<Simulation> {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);

    /**
     * The simulation.
     */
    private final Simulation simulation;

    /**
     * The condition determining when the simulation should be terminated.
     */
    private Predicate<Simulation> terminationCondition;

    /**
     * The condition determining when events should be emitted.
     */
    private Predicate<Simulation> emitCondition;

    /**
     * The emitter for node events.
     */
    private NodeEventEmitter nodeEventEmitter;

    /**
     * The emitter for graph events.
     */
    private GraphEventEmitter graphEventEmitter;

    /**
     * The time for the next update to be issued. (For FPS based emission).
     */
    private long nextTick = System.currentTimeMillis();

    /**
     * The time for the next update to be issued (in simulation time).
     */
    private Quantity<Time> scheduledEmitTime = Quantities.getQuantity(0.0, EnvironmentalParameters.getTimeStep().getUnit());

    /**
     * Creates a new simulation manager for the given simulation.
     *
     * @param simulation The simulation.
     */
    public SimulationManager(Simulation simulation) {
        logger.debug("Initializing simulation manager ...");
        this.simulation = simulation;
        nodeEventEmitter = new NodeEventEmitter();
        graphEventEmitter = new GraphEventEmitter();
        // emit every event if not specified otherwise
        emitCondition = s -> true;
    }

    /**
     * Adds a new listener for node based events.
     *
     * @param listener The listener.
     */
    public void addNodeUpdateListener(UpdateEventListener<NodeUpdatedEvent> listener) {
        logger.info("Added {} to node update listeners.", listener.getClass().getSimpleName());
        nodeEventEmitter.addEventListener(listener);
    }

    /**
     * Returns all currently registered node event listeners.
     *
     * @return All currently registered node event listeners.
     */
    public CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> getNodeListeners() {
        return nodeEventEmitter.getListeners();
    }

    /**
     * Adds a new listener for graph based events.
     *
     * @param listener The listener.
     */
    public void addGraphUpdateListener(UpdateEventListener<GraphUpdatedEvent> listener) {
        logger.info("Added {} to graph update listeners.", listener.getClass().getSimpleName());
        graphEventEmitter.addEventListener(listener);
    }

    /**
     * Sets a condition determining when the simulation should be terminated.
     *
     * @param terminationCondition The termination condition.
     */
    public void setTerminationCondition(Predicate<Simulation> terminationCondition) {
        this.terminationCondition = terminationCondition;
    }

    /**
     * Schedules the termination of the simulation after the given time (simulation time) has passed.
     *
     * @param time The time.
     */
    public void setSimulationTerminationToTime(Quantity<Time> time) {
        setTerminationCondition(s -> s.getElapsedTime().isLessThan(time));
    }

    /**
     * Schedules the termination of the simulation after the given number of epochs have passed.
     *
     * @param numberOfEpochs The number of epochs.
     */
    public void setSimulationTerminationToEpochs(long numberOfEpochs) {
        setTerminationCondition(s -> s.getEpoch() < numberOfEpochs);
    }

    /**
     * Sets a condition determining when events should be emitted.
     *
     * @param emitCondition The emission condition.
     */
    public void setUpdateEmissionCondition(Predicate<Simulation> emitCondition) {
        this.emitCondition = emitCondition;
    }

    /**
     * Sets the emission of updates for a rending engine. If more epochs are processed than can be displayed the epochs
     * in between are not emitted. If epoch calculation is slower each epoch is emitted.
     *
     * @param fps The frames (emits) per (real time) second.
     */
    public void tieUpdateEmissionToFPS(int fps) {
        int skipTicks = 1000 / fps;
        emitCondition = s -> {
            long currentMillis = System.currentTimeMillis();
            if (currentMillis > nextTick) {
                nextTick = currentMillis + skipTicks;
                return true;
            }
            return false;
        };
    }

    /**
     * Schedules the emission of events after the given time (simulation time) has passed.
     *
     * @param timePassed The (simulation) time passed.
     */
    public void setUpdateEmissionToTimePassed(Quantity<Time> timePassed) {
        emitCondition = s -> {
            ComparableQuantity<Time> currentTime = s.getElapsedTime();
            if (currentTime.isGreaterThan(scheduledEmitTime)) {
                scheduledEmitTime = currentTime.add(timePassed);
                return true;
            }
            return false;
        };
    }

    public void emitGraphEvent(Simulation simulation) {
        graphEventEmitter.emitEvent(new GraphUpdatedEvent(simulation.getGraph()));
    }
    public void emitNodeEvent(Simulation simulation, AutomatonNode automatonNode) {
        nodeEventEmitter.emitEvent(new NodeUpdatedEvent(simulation.getElapsedTime(), automatonNode));
    }


    /**
     * Returns the simulation.
     *
     * @return The simulation.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    protected Simulation call() {
        while (!isCancelled() && terminationCondition.test(simulation)) {
            if (emitCondition.test(simulation)) {
                logger.info("Emitting event after {} (epoch {}).", simulation.getElapsedTime(), simulation.getEpoch());
                emitGraphEvent(simulation);
                for (AutomatonNode automatonNode : simulation.getObservedNodes()) {
                    emitNodeEvent(simulation, automatonNode);
                    logger.debug("Emitted next epoch event for node {}.", automatonNode.getIdentifier());
                }
            }
            simulation.nextEpoch();
        }
        return simulation;
    }

    @Override
    protected void done() {
        try {
            logger.info("Simulation finished.");
            for (UpdateEventListener<NodeUpdatedEvent> nodeUpdatedEventUpdateEventListener : getNodeListeners()) {
                if (nodeUpdatedEventUpdateEventListener instanceof EpochUpdateWriter) {
                    ((EpochUpdateWriter) nodeUpdatedEventUpdateEventListener).closeWriters();
                }
            }
            // will exit jfx when simulation finishes
            // FIXME implement possibility to keep the GUI after simulation finishes - maybe add toogle
            Platform.exit();
            if (!isCancelled()) {
                get();
            }
        } catch (ExecutionException e) {
            // Exception occurred, deal with it
            logger.error("Encountered an exception during simulation: " + e.getCause());
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Shouldn't happen, we're invoked when computation is finished
            throw new AssertionError(e);
        }
    }


}
