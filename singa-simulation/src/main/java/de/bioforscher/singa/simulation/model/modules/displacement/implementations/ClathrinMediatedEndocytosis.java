package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.BuddingRate;
import de.bioforscher.singa.simulation.features.endocytosis.MaturationTime;
import de.bioforscher.singa.simulation.features.endocytosis.SpawnTimeSampler;
import de.bioforscher.singa.simulation.features.endocytosis.VesicleRadius;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembraneSegment;
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.simulation.model.modules.concentration.ModuleState.*;
import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.ACTIN_DEPOLYMERIZATION;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ClathrinMediatedEndocytosis extends DisplacementBasedModule {

    private HashMap<Vesicle, Quantity<Time>> maturingVesicles;
    private List<MacroscopicMembraneSegment> segments;

    private Quantity<Area> totalArea;
    private Quantity<Frequency> normalizedFrequency;

    // referencing entity to area and number
    private Map<ChemicalEntity, AbstractMap.Entry<Quantity<Area>, Double>> initialMembraneCargo;

    // randomized next spawn time
    private Quantity<Time> nextSpawnTime;
    // randomized next spawn site
    private Vector2D nextSpawnSite;
    // randomized radius
    private Quantity<Length> nextSpawnRadius;

    public ClathrinMediatedEndocytosis() {
        maturingVesicles = new HashMap<>();
        segments = new ArrayList<>();
        initialMembraneCargo = new HashMap<>();
        // features
        getRequiredFeatures().add(BuddingRate.class);
        getRequiredFeatures().add(VesicleRadius.class);
        getRequiredFeatures().add(MaturationTime.class);
    }

    @Override
    public void calculateUpdates() {
        if (totalArea == null) {
            calculateTotalMembraneArea();
        }
        normalizeSpawnFrequency();
        evaluateModuleState();
        if (state == PENDING) {
            updateMaturation();
            state = SUCCEEDED;
        }
    }

    private void calculateTotalMembraneArea() {
        totalArea = Quantities.getQuantity(0.0, Environment.getAreaUnit());
        for (MacroscopicMembraneSegment segment : segments) {
            for (LineSegment lineSegment : segment.getLineSegments()) {
                totalArea = totalArea.add(Environment.convertSimulationToSystemScale(lineSegment.getLength())
                        .multiply(Environment.getNodeDistance()).asType(Area.class));
            }
        }
    }

    private void normalizeSpawnFrequency() {
        normalizedFrequency = getFeature(BuddingRate.class).getFeatureContent().multiply(totalArea.to(new ProductUnit<>(NANO(METRE).pow(2)))).asType(Frequency.class);
    }

    public void addMembraneCargo(Quantity<Area> referenceArea, double numberOfEntities, ChemicalEntity chemicalEntity) {
        initialMembraneCargo.put(chemicalEntity, new AbstractMap.SimpleEntry<>(referenceArea, numberOfEntities));
    }

    @Override
    protected void evaluateModuleState() {
        // more than one spawn per time step
        ComparableQuantity<Time> timeBetweenEvents = Quantities.getQuantity(1.0 / (normalizedFrequency.getValue().doubleValue() * 2.0), SECOND);
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

    public void addMembraneSegment(MacroscopicMembraneSegment segment) {
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
        if (simulation.getElapsedTime().isGreaterThanOrEqualTo(nextSpawnTime)) {
            // spawn vesicle
            spawnVesicle();
            determineNextSpawnEvent();
        }
        // update maturation time of existing vesicles
        List<Vesicle> maturedVesicles = new ArrayList<>();
        for (Map.Entry<Vesicle, Quantity<Time>> entry : maturingVesicles.entrySet()) {
            Vesicle maturingVesicle = entry.getKey();
            Quantity<Time> maturationTime = entry.getValue();
            ComparableQuantity<Time> totalTime = getFeature(MaturationTime.class).getFeatureContent();
            // if maturation time is reached
            if (totalTime.isLessThan(maturationTime)) {
                // add vesicle to vesicle layer
                scissiorVesicle(maturingVesicle);
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
        vesicle.setAttachmentState(ACTIN_DEPOLYMERIZATION);
        maturingVesicles.put(vesicle, Quantities.getQuantity(0.0, Environment.getTimeUnit()));
    }

    private void scissiorVesicle(Vesicle vesicle) {
        for (Map.Entry<ChemicalEntity, Map.Entry<Quantity<Area>, Double>> entry : initialMembraneCargo.entrySet()) {
            // get values
            ChemicalEntity chemicalEntity = entry.getKey();
            Quantity<Area> area = entry.getValue().getKey();
            double number = entry.getValue().getValue();
            // scale to vesicle surface
            double molecules = vesicle.getArea()
                    .multiply(number / area.to(vesicle.getArea().getUnit())
                            .getValue().doubleValue()).getValue().doubleValue();
            // convert to concentration
            Quantity<MolarConcentration> concentration = MolarConcentration.moleculesToConcentration(molecules, Environment.getSubsectionVolume()).to(Environment.getConcentrationUnit());
            vesicle.getConcentrationContainer().set(CellTopology.MEMBRANE, chemicalEntity, concentration);
        }
        simulation.getVesicleLayer().addVesicle(vesicle);
    }

    private void determineNextSpawnEvent() {
        // choose random line segment from the given membrane
        LineSegment lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getLineSegments().iterator().next();
        // choose random point on that site, spawn a little on the inside
        nextSpawnSite = lineSegment.getRandomPoint().add(simulation.getSimulationRegion().getCentre().normalize());
        // sample spawn time
        nextSpawnTime = SpawnTimeSampler.sampleNextEventTime(simulation.getElapsedTime(), normalizedFrequency);
        // sample vesicle radius
        Quantity<Length> vesicleRadius = getFeature(VesicleRadius.class).getFeatureContent();
        nextSpawnRadius = SpawnTimeSampler.sampleNextVesicleRadius(vesicleRadius);
    }

    @Override
    public String toString() {
        return "Endocytosis maturing and scission";
    }

}
