package bio.singa.simulation.model.agents.filaments;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.agents.membranes.Membrane;
import bio.singa.simulation.model.agents.membranes.MembraneLayer;
import bio.singa.simulation.model.agents.membranes.MembraneSegment;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static bio.singa.simulation.model.agents.filaments.SkeletalFilament.FilamentBehaviour.STAGNANT;

/**
 * @author cl
 */
public class FilamentLayer {

    private List<SkeletalFilament> filaments;
    private MembraneLayer membraneLayer;
    private Rectangle simulationRegion;
    private Simulation simulation;

    public FilamentLayer(Simulation simulation, MembraneLayer membraneLayer) {
        filaments = new ArrayList<>();
        this.simulation = simulation;
        simulationRegion = simulation.getSimulationRegion();
        this.membraneLayer = membraneLayer;
    }

    public void spawnFilament(Membrane sourceMembrane, Membrane targetMembrane) {
        List<MembraneSegment> segments = new ArrayList<>(sourceMembrane.getSegments());
        // choose random line segment from the given membrane
        // TODO should not choose first but specified region
        List<Vector2D> targetPoints = targetMembrane.getSegments().stream()
                .map(MembraneSegment::getStartingPoint)
                .collect(Collectors.toList());

        List<Vector2D> sourcePoints = sourceMembrane.getSegments().stream()
                .map(MembraneSegment::getStartingPoint)
                .collect(Collectors.toList());

        Vector2D centroid = Vectors.getCentroid(sourcePoints).as(Vector2D.class);
        LineSegment segment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getSegment();
        Vector2D initialPosition = segment.getRandomPoint();
        while (initialPosition.isRightOf(centroid)) {
            segment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getSegment();
            initialPosition = segment.getRandomPoint();
        }

        Map.Entry<Vector2D, Double> entry = VectorMetricProvider.EUCLIDEAN_METRIC.calculateClosestDistance(targetPoints, initialPosition);
        addFilament(initialPosition, initialPosition.subtract(entry.getKey()).normalize());
    }

    public void spawnHorizontalFilament(Membrane sourceMembrane) {
        List<MembraneSegment> segments = new ArrayList<>(sourceMembrane.getSegments());
        // choose random line segment from the given membrane
        LineSegment lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getSegment();
        // add corresponding filament
        while (lineSegment.isHorizontal()) {
            lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getSegment();
        }
        if (lineSegment.isVertical()) {
            addHorizontalFilament(lineSegment);
        } else {
            addPerpendicularFilament(lineSegment);
        }
    }

    private void addVerticalFilament(LineSegment lineSegment) {
        Vector2D initialPosition = lineSegment.getRandomPoint();
        // calculate distances to top and bottom
        double topDistance = simulationRegion.getTopEdge().distanceTo(initialPosition);
        double bottomDistance = simulationRegion.getBottomEdge().distanceTo(initialPosition);
        // check if top or bottom is closer
        if (topDistance > bottomDistance) {
            addFilament(initialPosition, Vector2D.UNIT_VECTOR_UP);
        } else {
            addFilament(initialPosition, Vector2D.UNIT_VECTOR_DOWN);
        }
    }

    private void addHorizontalFilament(LineSegment lineSegment) {
        Vector2D initialPosition = lineSegment.getRandomPoint();
        // calculate distances to left and right
        double rightDistance = simulationRegion.getRightEdge().distanceTo(initialPosition);
        double leftDistance = simulationRegion.getLeftEdge().distanceTo(initialPosition);
        // check if top or bottom is closer
        if (rightDistance > leftDistance) {
            addFilament(initialPosition, Vector2D.UNIT_VECTOR_RIGHT);
        } else {
            addFilament(initialPosition, Vector2D.UNIT_VECTOR_LEFT);
        }
    }

    private void addPerpendicularFilament(LineSegment lineSegment) {
        Vector2D initialPosition = lineSegment.getRandomPoint();
        Vector2D centre = simulationRegion.getCentre();
        addFilament(initialPosition, centre.subtract(initialPosition));
    }

    public void nextEpoch() {
        // TODO scale with time
        ListIterator<SkeletalFilament> iterator = filaments.listIterator();
        while (iterator.hasNext()) {
            SkeletalFilament filament = iterator.next();
            int currentLength = filament.nextEpoch();
            // filament has shrunk to zero
            if (currentLength == 0) {
                iterator.remove();
            }
            updateBehaviours();
        }
    }

    private void updateBehaviours() {
        // check border interactions
        for (SkeletalFilament filament : filaments) {
            Vector2D head = filament.getPlusEnd();
            // TODO collisions with simulation borders
            if (filament.getPlusEndBehaviour() != STAGNANT) {
                for (Membrane membrane : membraneLayer.getMembranes()) {
                    for (MembraneSegment membraneSegment : membrane.getSegments()) {
                        if (membraneSegment.distanceTo(head) < 1 && filament.getSegments().size() > 10) {
                            filament.setPlusEndBehaviour(STAGNANT);
                            break;
                        }
                    }
                }
            }

        }
        // check pairwise interactions
//        for (SkeletalFilament filament : filaments) {
//            Map.Entry<SkeletalFilament, Double> closestRelevantDistance = filament.getClosestRelevantDistance();
//            SkeletalFilament closestFragment = closestRelevantDistance.getKey();
//            double closestDistance = closestRelevantDistance.getValue();
//            // TODO < 25 nm in system scale
//            if (closestDistance < 1) {
//                // calculate angle and convert to degree
//                double angle = filament.angleTo(closestFragment) * 180 / PI;
//                if (angle < 40) {
//                    // zippering - plus: follow
//                    if (filament.getPlusEndBehaviour() == GROW) {
//                        filament.setPlusEndBehaviour(FOLLOW);
//                        filament.setLeadFilament(closestFragment);
//                    }
//                }
//            }
//        }

    }

    public boolean hasGrowingFilaments() {
        for (SkeletalFilament filament : filaments) {
            if (filament.getPlusEndBehaviour() != SkeletalFilament.FilamentBehaviour.STAGNANT) {
                return true;
            }
        }
        return false;
    }

    public void addFilament(Vector2D initialPosition, Vector2D initialDirection) {
        filaments.add(new SkeletalFilament(initialPosition, initialDirection, simulation.getGraph()));
    }

    public List<SkeletalFilament> getFilaments() {
        return filaments;
    }

    public void setFilaments(List<SkeletalFilament> filaments) {
        this.filaments = filaments;
    }
}
