package bio.singa.simulation.model.agents.linelike;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.metrics.model.VectorMetricProvider;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.ACTIN;
import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.GrowthBehaviour.STAGNANT;
import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.MICROTUBULE;

/**
 * @author cl
 */
public class LineLikeAgentLayer {

    private List<LineLikeAgent> filaments;
    private MembraneLayer membraneLayer;
    private Rectangle simulationRegion;
    private Simulation simulation;
    private List<LineLikeAgent> misguidedFilaments;

    private boolean targetedGrowth = false;
    private CellRegion targetMembrane;

    public LineLikeAgentLayer(Simulation simulation, MembraneLayer membraneLayer) {
        filaments = new ArrayList<>();
        this.simulation = simulation;
        simulationRegion = simulation.getSimulationRegion();
        this.membraneLayer = membraneLayer;
        misguidedFilaments = new ArrayList<>();
    }

    public boolean isTargetedGrowth() {
        return targetedGrowth;
    }

    public void setTargetedGrowth(boolean targetedGrowth) {
        this.targetedGrowth = targetedGrowth;
    }

    public CellRegion getTargetMembrane() {
        return targetMembrane;
    }

    public void setTargetMembrane(CellRegion targetMembrane) {
        this.targetMembrane = targetMembrane;
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
        addMicrotubule(initialPosition, initialPosition.subtract(entry.getKey()).normalize());
    }

    public void spawnActinFilament(Membrane cellMembrane) {
        List<MembraneSegment> segments = new ArrayList<>(cellMembrane.getSegments());
        // choose random line segment from the given membrane
        LineSegment lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getSegment();
        Vector2D initialPosition = lineSegment.getRandomPoint();

        List<Vector2D> membranePoints = cellMembrane.getSegments().stream()
                .map(MembraneSegment::getStartingPoint)
                .collect(Collectors.toList());
        Vector2D centroid = Vectors.getCentroid(membranePoints).as(Vector2D.class);

        addActin(initialPosition, initialPosition.subtract(centroid).normalize());
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
            addMicrotubule(initialPosition, Vector2D.UNIT_VECTOR_UP);
        } else {
            addMicrotubule(initialPosition, Vector2D.UNIT_VECTOR_DOWN);
        }
    }

    private void addHorizontalFilament(LineSegment lineSegment) {
        Vector2D initialPosition = lineSegment.getRandomPoint();
        // calculate distances to left and right
        double rightDistance = simulationRegion.getRightEdge().distanceTo(initialPosition);
        double leftDistance = simulationRegion.getLeftEdge().distanceTo(initialPosition);
        // check if top or bottom is closer
        if (rightDistance > leftDistance) {
            addMicrotubule(initialPosition, Vector2D.UNIT_VECTOR_RIGHT);
        } else {
            addMicrotubule(initialPosition, Vector2D.UNIT_VECTOR_LEFT);
        }
    }

    private void addPerpendicularFilament(LineSegment lineSegment) {
        Vector2D initialPosition = lineSegment.getRandomPoint();
        Vector2D centre = simulationRegion.getCentre();
        addMicrotubule(initialPosition, centre.subtract(initialPosition));
    }

    public void nextEpoch() {
        // TODO scale with time
        ListIterator<LineLikeAgent> iterator = filaments.listIterator();
        while (iterator.hasNext()) {
            LineLikeAgent filament = iterator.next();
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
        for (LineLikeAgent filament : filaments) {
            Vector2D head = filament.getPlusEnd();
            // TODO collisions with simulation borders
            if (filament.getPlusEndBehaviour() != STAGNANT) {
                for (Membrane membrane : membraneLayer.getMembranes()) {
                    for (MembraneSegment membraneSegment : membrane.getSegments()) {
                        if (membraneSegment.distanceTo(head) < 1 && filament.getPath().size() > 10) {
                            filament.setPlusEndBehaviour(STAGNANT);
                            if (targetedGrowth = true && !membraneSegment.getNode().getCellRegion().equals(targetMembrane)) {
                                misguidedFilaments.add(filament);
                            }
                            break;
                        }
                    }
                }
                if (!simulationRegion.containsVector(head)) {
                    filament.setPlusEndBehaviour(STAGNANT);
                    misguidedFilaments.add(filament);
                    break;
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
//                        filament.setLeadAgent(closestFragment);
//                    }
//                }
//            }
//        }

    }

    public boolean hasGrowingFilaments() {
        for (LineLikeAgent filament : filaments) {
            if (filament.getPlusEndBehaviour() != STAGNANT) {
                return true;
            }
        }
        return false;
    }

    public void addFilaments(Collection<LineLikeAgent> filaments) {
        this.filaments.addAll(filaments);
    }

    public void addMicrotubule(Vector2D initialPosition, Vector2D initialDirection) {
        filaments.add(new LineLikeAgent(MICROTUBULE, initialPosition, initialDirection, simulation.getGraph()));
    }

    public void addActin(Vector2D initialPosition, Vector2D initialDirection) {
        filaments.add(new LineLikeAgent(ACTIN, initialPosition, initialDirection, simulation.getGraph()));
    }

    public void purgeMisguidedFilaments() {
        for (LineLikeAgent misguidedFilament : misguidedFilaments) {
            filaments.remove(misguidedFilament);
            for (AutomatonNode node : simulation.getGraph().getNodes()) {
                node.getAssociatedLineLikeAgents().remove(misguidedFilament);
            }
            if (misguidedFilament.getType().equals(ACTIN)) {
                simulation.getMembraneLayer().getMicrotubuleOrganizingCentre().spawnActin(this);
            } else {
                simulation.getMembraneLayer().getMicrotubuleOrganizingCentre().spawnMicrotubule(this);
            }
        }
        misguidedFilaments.clear();
    }

    public List<LineLikeAgent> getFilaments() {
        return filaments;
    }

    public void addFilament(LineLikeAgent filament) {
        filament.associateInGraph(simulation.getGraph());
        filaments.add(filament);
    }

    public void setFilaments(List<LineLikeAgent> filaments) {
        this.filaments = filaments;
    }
}
