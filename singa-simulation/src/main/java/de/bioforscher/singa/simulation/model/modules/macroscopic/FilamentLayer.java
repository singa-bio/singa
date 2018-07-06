package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.simulation.model.modules.macroscopic.SkeletalFilament.FilamentBehaviour.*;
import static java.lang.Math.PI;

/**
 * @author cl
 */
public class FilamentLayer {

    private List<SkeletalFilament> filaments;
    private MembraneLayer membraneLayer;
    private Rectangle simulationRegion;

    public FilamentLayer(Rectangle simulationRegion, MembraneLayer membraneLayer) {
        filaments = new ArrayList<>();
        this.simulationRegion = simulationRegion;
        this.membraneLayer = membraneLayer;
    }

    public void spawnFilament(MacroscopicMembrane membrane) {
        List<MembraneSegment> segments = new ArrayList<>(membrane.getSegments());
        // choose random line segment from the given membrane
        LineSegment lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getLineSegments().iterator().next();
        // add corresponding filament
        if (lineSegment.isHorizontal()) {
            addVerticalFilament(lineSegment);
        } else if (lineSegment.isVertical()) {
            addHorizontalFilament(lineSegment);
        } else {
            addPerpendicularFilament(lineSegment);
        }
    }

    public void spawnHorizontalFilament(MacroscopicMembrane membrane) {
        List<MembraneSegment> segments = new ArrayList<>(membrane.getSegments());
        // choose random line segment from the given membrane
        LineSegment lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getLineSegments().iterator().next();
        // add corresponding filament
        while (lineSegment.isHorizontal()) {
            lineSegment = segments.get(ThreadLocalRandom.current().nextInt(0, segments.size())).getLineSegments().iterator().next();
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
                for (MacroscopicMembrane membrane : membraneLayer.getMembranes()) {
                    for (MembraneSegment membraneSegment : membrane.getSegments()) {
                        for (LineSegment lineSegment : membraneSegment.getLineSegments()) {
                            if (lineSegment.distanceTo(head) < 1 && filament.getSegments().size() > 10) {
                                filament.setPlusEndBehaviour(STAGNANT);
                                break;
                            }
                        }
                    }
                }
            }

        }
        // check pairwise interactions
        for (int first = 0; first < filaments.size(); first++) {
            SkeletalFilament filament1 = filaments.get(first);
            // only check each filament once
            for (int second = first + 1; second < filaments.size(); second++) {
                SkeletalFilament filament2 = filaments.get(second);
                if (filament1 != filament2) {
                    // check pairwise distances
                    double distance = filament1.distanceTo(filament2);
                    // TODO < 25 nm in system scale
                    if (distance < 1) {
                        // calculate angle and convert to degree
                        double angle = filament1.angleTo(filament2) * 180 / PI;
                        if (angle < 40) {
                            // zippering - plus: follow
                            if (filament1.getPlusEndBehaviour() == GROW) {
                                filament1.setPlusEndBehaviour(FOLLOW);
                                filament1.setLeadFilament(filament2);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addFilament(Vector2D initialPosition, Vector2D initialDirection) {
        filaments.add(new SkeletalFilament(initialPosition, initialDirection));
    }

    public List<SkeletalFilament> getFilaments() {
        return filaments;
    }

    public void setFilaments(List<SkeletalFilament> filaments) {
        this.filaments = filaments;
    }
}
