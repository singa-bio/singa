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

    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);

    private final Simulation simulation;
    private Predicate<Simulation> terminationCondition;
    private Predicate<Simulation> emitCondition;

    private NodeEventEmitter nodeEventEmitter;
    private GraphEventEmitter graphEventEmitter;

    private long nextTick = System.currentTimeMillis();
    private Quantity<Time> scheduledEmitTime = Quantities.getQuantity(0.0, EnvironmentalParameters.getTimeStep().getUnit());


    public SimulationManager(Simulation simulation) {
        logger.debug("Initializing simulation manager ...");
        this.simulation = simulation;
        nodeEventEmitter = new NodeEventEmitter();
        graphEventEmitter = new GraphEventEmitter();
        // emit every event if not specified otherwise
        emitCondition = s -> true;
    }

    public void addNodeUpdateListener(UpdateEventListener<NodeUpdatedEvent> listener) {
        logger.info("Added {} to node update listeners.", listener.getClass().getSimpleName());
        nodeEventEmitter.addEventListener(listener);
    }

    public CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> getNodeListeners() {
        return nodeEventEmitter.getListeners();
    }

    public void addGraphUpdateListener(UpdateEventListener<GraphUpdatedEvent> listener) {
        logger.info("Added {} to graph update listeners.", listener.getClass().getSimpleName());
        graphEventEmitter.addEventListener(listener);
    }

    public void setTerminationCondition(Predicate<Simulation> terminationCondition) {
        this.terminationCondition = terminationCondition;
    }

    public void setSimulationTerminationToTime(Quantity<Time> time) {
        setTerminationCondition(s -> s.getElapsedTime().isLessThan(time));
    }

    public void setSimulationTerminationToEpochs(long epochs) {
        setTerminationCondition(s -> s.getEpoch() < epochs);
    }

    public void setUpdateEmissionCondition(Predicate<Simulation> emitCondition) {
        this.emitCondition = emitCondition;
    }

    public void setUpdateEmissionToFPS(int fps) {
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

    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    protected Simulation call() {
        System.out.println(simulation);
        System.out.println(terminationCondition);
        while (!isCancelled() && terminationCondition.test(simulation)) {
            simulation.nextEpoch();
            if (emitCondition.test(simulation)) {
                graphEventEmitter.emitEvent(new GraphUpdatedEvent(simulation.getGraph()));
                for (AutomatonNode automatonNode : simulation.getGraph().getNodes()) {
                    if (automatonNode.isObserved()) {
                        nodeEventEmitter.emitEvent(new NodeUpdatedEvent(simulation.getElapsedTime(), automatonNode));
                        logger.debug("Emitted next epoch event for node {}.", automatonNode.getIdentifier());
                    }
                }
            }
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
            // FIXME implement possibility to use this with GUI
            // maybe add toogle
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
