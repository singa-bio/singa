package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.BuddingFrequency;
import de.bioforscher.singa.simulation.features.endocytosis.MaturationTime;
import de.bioforscher.singa.simulation.features.endocytosis.SpawnTimeSampler;
import de.bioforscher.singa.simulation.features.endocytosis.VesicleRadius;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.macroscopic.MembraneSegment;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.simulation.model.modules.concentration.ModuleState.*;
import static tec.uom.se.unit.Units.HERTZ;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ConstitutiveEndocytosis extends DisplacementBasedModule {

    private HashMap<Vesicle, Quantity<Time>> maturingVesicles;
    private List<MembraneSegment> segments;

    // randomized next spawn time
    private Quantity<Time> nextSpawnTime;
    // randomized next spawn site
    private Vector2D nextSpawnSite;
    // randomized radius
    private Quantity<Length> nextSpawnRadius;

    public ConstitutiveEndocytosis() {
        maturingVesicles = new HashMap<>();
        segments = new ArrayList<>();
        // features
        getRequiredFeatures().add(BuddingFrequency.class);
        getRequiredFeatures().add(VesicleRadius.class);
        getRequiredFeatures().add(MaturationTime.class);
    }

    @Override
    public void calculateUpdates() {
        evaluateModuleState();
        if (state == PENDING) {
            updateMaturation();
            state = SUCCEEDED;
        }
    }

    @Override
    protected void evaluateModuleState() {
        // TODO module currently does not work with recalculations of time steps
        BuddingFrequency frequency = getFeature(BuddingFrequency.class);
        // more than on spawn par time step
        ComparableQuantity<Time> timeBetweenEvents = Quantities.getQuantity(1.0 / frequency.getFeatureContent().to(HERTZ).getValue().doubleValue(), SECOND);
        if (timeBetweenEvents.isLessThan(Environment.getTimeStep())) {
            state = REQUIRING_RECALCULATION;
        } else {
            state = PENDING;
        }
    }

    public void optimizeTimeStep() {
        while (state == REQUIRING_RECALCULATION) {
            simulation.getVesicleLayer().clearUpdates();
            updateScheduler.decreaseTimeStep();
            calculateUpdates();
        }
    }

    public void addMembraneSegment(MembraneSegment segment) {
        segments.add(segment);
    }

    public HashMap<Vesicle, Quantity<Time>> getMaturingVesicles() {
        return maturingVesicles;
    }

    private void updateMaturation() {
        if (nextSpawnTime == null) {
            determineNextSpawnEvent();
        }
        // check if spawn event needs to happen
        if (simulation.getElapsedTime().isGreaterThan(nextSpawnTime)) {
            // spawn vesicle
            spawnVesicle();
            determineNextSpawnEvent();
        }
        // update maturation time of existing vesicles
        List<Vesicle> maturedVesicles = new ArrayList<>();
        for (Map.Entry<Vesicle, Quantity<Time>> entry : maturingVesicles.entrySet()) {
            Vesicle maturingVesicle = entry.getKey();
            Quantity<Time> maturationTime = entry.getValue();
            // TODO currently all vesicles take the same time to mature
            ComparableQuantity<Time> totalTime = getFeature(MaturationTime.class).getFeatureContent();
            // if maturation time is reached
            if (totalTime.isLessThan(maturationTime)) {
                // add vesicle to vesicle layer
                simulation.getVesicleLayer().addVesicle(maturingVesicle);
                maturedVesicles.add(maturingVesicle);
            } else {
                // increase Maturation time
                maturingVesicles.put(maturingVesicle, maturationTime.add(Environment.getTimeStep()));
            }
        }

        for (Vesicle maturedVesicle : maturedVesicles) {
            maturingVesicles.remove(maturedVesicle);
        }
    }

    private void spawnVesicle() {
        Vesicle vesicle = new Vesicle(nextSpawnSite, nextSpawnRadius);
        System.out.println("Spawing "+vesicle);
        maturingVesicles.put(vesicle, Quantities.getQuantity(0.0, Environment.getTimeUnit()));
    }

    private void determineNextSpawnEvent() {
        // choose random line segment from the given membrane
        LineSegment lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getLineSegments().iterator().next();
        // choose random point on that site
        nextSpawnSite = lineSegment.getRandomPoint();
        // sample spawn time
        Quantity<Frequency> frequency = getFeature(BuddingFrequency.class).getFeatureContent();
        nextSpawnTime = SpawnTimeSampler.sampleNextEventTime(simulation.getElapsedTime(), frequency);
        // sample vesicle radius
        Quantity<Length> vesicleRadius = getFeature(VesicleRadius.class).getFeatureContent();
        nextSpawnRadius = SpawnTimeSampler.sampleNextVesicleRadius(vesicleRadius);
    }

}
