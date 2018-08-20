package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.BuddingRate;
import bio.singa.simulation.features.MaturationTime;
import bio.singa.simulation.features.SpawnTimeSampler;
import bio.singa.simulation.features.VesicleRadius;
import bio.singa.simulation.model.agents.membranes.MembraneSegment;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellTopology;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.simulation.features.BuddingRate.SQUARE_NANOMETRE;
import static bio.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.ACTIN_DEPOLYMERIZATION;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ClathrinMediatedEndocytosis extends QualitativeModule {

    private List<MembraneSegment> segments;
    private Map<ChemicalEntity, AbstractMap.Entry<Quantity<Area>, Double>> initialMembraneCargo;

    private List<SpawnEvent> queuedEvents;
    private List<SpawnEvent> candidateEvents;
    private List<SpawnEvent> completingEvents;

    public ClathrinMediatedEndocytosis() {
        initialMembraneCargo = new HashMap<>();
        segments = new ArrayList<>();
        queuedEvents = new ArrayList<>();
        candidateEvents = new ArrayList<>();
        completingEvents = new ArrayList<>();
        // features
        getRequiredFeatures().add(BuddingRate.class);
        getRequiredFeatures().add(VesicleRadius.class);
        getRequiredFeatures().add(MaturationTime.class);
    }

    @Override
    public void calculateUpdates() {
        checkSpawnTimes();
        determineNewEvents();
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        completingEvents.clear();
        candidateEvents.clear();
    }

    @Override
    public void onCompletion() {
        // add generated candidate events
        queuedEvents.addAll(candidateEvents);
        // spawn vesicles and remove events
        for (SpawnEvent completingEvent : completingEvents) {
            spawnVesicle(completingEvent);
            queuedEvents.remove(completingEvent);
        }
    }

    public void addMembraneSegment(MembraneSegment segment) {
        segments.add(segment);
    }

    public void addMembraneSegments(Collection<MembraneSegment> segments) {
        this.segments.addAll(segments);
    }

    public void addMembraneCargo(Quantity<Area> referenceArea, double numberOfEntities, ChemicalEntity chemicalEntity) {
        initialMembraneCargo.put(chemicalEntity, new AbstractMap.SimpleEntry<>(referenceArea, numberOfEntities));
    }

    public List<SpawnEvent> getQueuedEvents() {
        return queuedEvents;
    }

    private void checkSpawnTimes() {
        for (SpawnEvent queuedEvent : queuedEvents) {
            // check each event if it should spawn
            if (simulation.getElapsedTime().isGreaterThanOrEqualTo(queuedEvent.getSpawnTime())) {
                // move to completing events
                completingEvents.add(queuedEvent);
            }
        }
    }

    private void spawnVesicle(SpawnEvent event) {
        Vesicle vesicle = new Vesicle(event.getSpawnSite(), event.getSpawnRadius());
        vesicle.setAttachmentState(ACTIN_DEPOLYMERIZATION);
        initializeCargo(vesicle);
        simulation.getVesicleLayer().addVesicle(vesicle);
    }

    private void initializeCargo(Vesicle vesicle) {
        for (Map.Entry<ChemicalEntity, Map.Entry<Quantity<Area>, Double>> entry : initialMembraneCargo.entrySet()) {
            // get values
            ChemicalEntity chemicalEntity = entry.getKey();
            Quantity<Area> area = entry.getValue().getKey();
            double number = entry.getValue().getValue();
            // scale to vesicle surface
            double molecules = vesicle.getArea().multiply(number / area.to(vesicle.getArea().getUnit())
                    .getValue().doubleValue()).getValue().doubleValue();
            // convert to concentration
            Quantity<MolarConcentration> concentration = MolarConcentration.moleculesToConcentration(molecules, Environment.getSubsectionVolume())
                    .to(Environment.getConcentrationUnit());
            // set concentration
            vesicle.getConcentrationContainer().initialize(CellTopology.MEMBRANE, chemicalEntity, concentration);
        }
    }

    private void determineNewEvents() {
        for (MembraneSegment segment : segments) {
            // probability = rate (1/area*time) * area * time step
            double probability = getFeature(BuddingRate.class).getFeatureContent().multiply(segment.getArea().to(SQUARE_NANOMETRE))
                    .multiply(Environment.getTimeStep().to(SECOND)).getValue().doubleValue();
            // roll if event happens
            if (ThreadLocalRandom.current().nextDouble() < probability) {
                candidateEvents.add(createSpawnEvent(segment));
            }
        }
    }

    private SpawnEvent createSpawnEvent(MembraneSegment segment) {
        // choose random point on that site, spawn a little on the inside
        Vector2D spawnSite = segment.getSegment().getRandomPoint().add(simulation.getSimulationRegion().getCentre().normalize());
        // sample maturation time
        Quantity<Time> spawnTime = simulation.getElapsedTime().add(SpawnTimeSampler.sampleMaturatuionTime(getFeature(MaturationTime.class).getFeatureContent()));
        // sample vesicle radius
        Quantity<Length> spawnRadius = SpawnTimeSampler.sampleVesicleRadius(getFeature(VesicleRadius.class).getFeatureContent());
        // return event
        return new SpawnEvent(spawnTime, spawnSite, spawnRadius);
    }

    @Override
    public String toString() {
        return "Endocytosis maturing and scission";
    }

    public class SpawnEvent {

        // randomized next spawn time
        private Quantity<Time> spawnTime;
        // randomized next spawn site
        private Vector2D spawnSite;
        // randomized radius
        private Quantity<Length> spawnRadius;

        public SpawnEvent(Quantity<Time> spawnTime, Vector2D spawnSite, Quantity<Length> spawnRadius) {
            this.spawnTime = spawnTime;
            this.spawnSite = spawnSite;
            this.spawnRadius = spawnRadius;
        }

        public Quantity<Time> getSpawnTime() {
            return spawnTime;
        }

        public Vector2D getSpawnSite() {
            return spawnSite;
        }

        public Quantity<Length> getSpawnRadius() {
            return spawnRadius;
        }
    }


}
