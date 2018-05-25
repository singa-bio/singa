package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.simulation.model.layer.SpatialDelta;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class VesicleModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(VesicleModule.class);

    private Simulation simulation;

    /**
     * The functions that are applied with each epoch.
     */
    private final Map<Function<Vesicle, SpatialDelta>, Predicate<Vesicle>> deltaFunctions;

    public VesicleModule(Simulation simulation) {
        this.simulation = simulation;
        deltaFunctions = new HashMap<>();
    }

    public void addDeltaFunction(Function<Vesicle, SpatialDelta> deltaFunction, Predicate<Vesicle> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    public void determineAllDeltas(List<Vesicle> vesicles) {
        // determine deltas
        for (Vesicle vesicle : vesicles) {
            logger.trace("Determining delta for {}.", vesicle.getStringIdentifier());
            determineDeltas(vesicle);
        }
    }

    public void determineDeltas(Vesicle vesicle) {
        for (Map.Entry<Function<Vesicle, SpatialDelta>, Predicate<Vesicle>> entry : deltaFunctions.entrySet()) {
            // test predicate
            if (entry.getValue().test(vesicle)) {
                SpatialDelta spatialDelta = entry.getKey().apply(vesicle);
                vesicle.addPotentialSpatialDelta(spatialDelta);
            }
        }
    }

}
