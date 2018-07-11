package de.bioforscher.singa.simulation.model.modules.displacement;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.SimpleLineSegment;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembrane;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembraneSegment;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

/**
 * @author cl
 */
public class VesicleLayer {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DisplacementBasedModule.class);

    private List<Vesicle> vesicles;
    private Rectangle simulationArea;
    private final Quantity<Length> displacementEpsilon;
    private Simulation simulation;

    public VesicleLayer(Simulation simulation) {
        this.simulation = simulation;
        vesicles = new ArrayList<>();
        displacementEpsilon = Environment.getNodeDistance().divide(10);
    }

    public Rectangle getSimulationArea() {
        return simulationArea;
    }

    public void setSimulationArea(Rectangle simulationArea) {
        this.simulationArea = simulationArea;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void addVesicle(Vesicle vesicle) {
        vesicles.add(vesicle);
    }

    public List<Vesicle> getVesicles() {
        return vesicles;
    }

    private void checkForCollisions() {
        //compare the distance to combined radii
        LabeledSymmetricMatrix<Vesicle> distances = SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(vesicles, Vesicle::getNextPosition);
        vesicleLoop:
        for (Vesicle vesicle1 : vesicles) {
            // check collisions with other vesicles
            double firstRadius = Environment.convertSystemToSimulationScale(vesicle1.getRadius());
            for (Vesicle vesicle2 : vesicles) {
                if (vesicle1 != vesicle2) {
                    double distance = distances.getValueForLabel(vesicle1, vesicle2);
                    double combinedRadii = firstRadius * Environment.convertSystemToSimulationScale(vesicle2.getRadius());
                    if (distance < combinedRadii) {
                        vesicle1.resetNextPosition();
                        continue vesicleLoop;
                    }
                }
            }
            // check collisions with membranes
            for (MacroscopicMembrane macroscopicMembrane : simulation.getMembraneLayer().getMembranes()) {
                for (MacroscopicMembraneSegment membraneSegment : macroscopicMembrane.getSegments()) {
                    for (LineSegment lineSegment : membraneSegment.getLineSegments()) {
                        if (!vesicle1.getCurrentPosition().equals(vesicle1.getNextPosition())) {
                            SimpleLineSegment displacementVector = new SimpleLineSegment(vesicle1.getCurrentPosition(), vesicle1.getNextPosition());
                            if (!displacementVector.intersectionsWith(lineSegment).isEmpty()) {
                                vesicle1.resetNextPosition();
                                continue vesicleLoop;
                            }
                        }
                    }
                }
            }
            // check collisions with border
            double x = vesicle1.getNextPosition().getX();
            double y = vesicle1.getNextPosition().getY();
            if (x - firstRadius < simulationArea.getLeftMostXPosition() || x + firstRadius > simulationArea.getRightMostXPosition() ||
                    y - firstRadius < simulationArea.getTopMostYPosition() || y + firstRadius > simulationArea.getBottomMostYPosition()) {
                vesicle1.resetNextPosition();
            }
        }
    }

    public boolean deltasAreBelowDisplacementCutoff() {
        for (Vesicle vesicle : vesicles) {
            Vector2D totalDisplacement = vesicle.calculateTotalDisplacement();
            Quantity<Length> lengthQuantity = Environment.convertSimulationToSystemScale(totalDisplacement.getMagnitude());
            lengthQuantity.to(displacementEpsilon.getUnit());
            if (lengthQuantity.getValue().doubleValue() > displacementEpsilon.getValue().doubleValue()) {
                logger.info("The magnitude of the spatial displacement of {} is {}, higher than the allowed {}.", vesicle.getStringIdentifier(), lengthQuantity, displacementEpsilon);
                return false;
            }
        }
        return true;
    }

    public void clearUpdates() {
        for (Vesicle vesicle : vesicles) {
            vesicle.clearPotentialConcentrationDeltas();
            vesicle.clearPotentialDisplacementDeltas();
            vesicle.resetNextPosition();
        }
    }

    public void applyDeltas() {
        checkForCollisions();
        for (Vesicle vesicle : vesicles) {
            vesicle.clearPotentialDisplacementDeltas();
            vesicle.updatePosition();
        }
    }

}
