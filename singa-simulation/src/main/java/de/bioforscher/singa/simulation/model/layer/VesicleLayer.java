package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.modules.model.VesicleModule;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

/**
 * @author cl
 */
public class VesicleLayer {

    private List<Vesicle> vesicles;

    private List<VesicleModule> vesicleModules;

    private Rectangle simulationArea;

    private final Quantity<Length> displacementEpsilon;

    public VesicleLayer() {
        vesicles = new ArrayList<>();
        vesicleModules = new ArrayList<>();
        displacementEpsilon = Environment.getNodeDistance().divide(5);
    }

    public Rectangle getSimulationArea() {
        return simulationArea;
    }

    public void setSimulationArea(Rectangle simulationArea) {
        this.simulationArea = simulationArea;
    }

    public void addVesicle(Vesicle vesicle) {
        vesicles.add(vesicle);
    }

    public List<Vesicle> getVesicles() {
        return vesicles;
    }

    public void addVesicleModule(VesicleModule vesicleModule) {
        vesicleModules.add(vesicleModule);
    }

    public List<VesicleModule> getVesicleModules() {
        return vesicleModules;
    }

    private void checkForCollisions() {
        //compare the distance to combined radii
        LabeledSymmetricMatrix<Vesicle> distances = SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(vesicles, Vesicle::getPotentialUpdate);
        for (Vesicle vesicle1 : vesicles) {
            // check for collisions with other vesicles
            double firstRadius = Environment.convertSystemToSimulationScale(vesicle1.getRadius());
            for (Vesicle vesicle2 : vesicles) {
                if (vesicle1 != vesicle2) {
                    double distance = distances.getValueForLabel(vesicle1, vesicle2);
                    double combinedRadii = firstRadius * Environment.convertSystemToSimulationScale(vesicle2.getRadius());
                    if (distance < combinedRadii) {
                        vesicle1.permitDisplacement();
                        break;
                    }
                }
            }
            // check for collisions with border
            double x = vesicle1.getPotentialUpdate().getX();
            double y = vesicle1.getPotentialUpdate().getY();
            if (x - firstRadius < simulationArea.getLeftMostXPosition() || x + firstRadius > simulationArea.getRightMostXPosition() ||
                    y - firstRadius < simulationArea.getBottomMostYPosition() || y + firstRadius > simulationArea.getTopMostYPosition()) {
                vesicle1.permitDisplacement();
            }
        }
    }

    public boolean deltasAreBelowDisplacementCutoff() {
        for (Vesicle vesicle : vesicles) {
            Vector2D totalDisplacement = vesicle.calculateTotalDisplacement();
            Quantity<Length> lengthQuantity = Environment.convertSimulationToSystemScale(totalDisplacement.getMagnitude());
            lengthQuantity.to(displacementEpsilon.getUnit());
            if (lengthQuantity.getValue().doubleValue() > displacementEpsilon.getValue().doubleValue()) {
                return false;
            }
        }
        return true;
    }

    public void clearUpdates() {
        for (Vesicle vesicle : vesicles) {
            vesicle.clearPotentialDeltas();
            vesicle.permitDisplacement();
        }
    }

    public void rescaleDiffusifity() {
        for (Vesicle vesicle : vesicles) {
            vesicle.rescaleDiffusivity();
        }
    }

    public void step() {
        checkForCollisions();
        for (Vesicle vesicle : vesicles) {
            vesicle.move();
            vesicle.clearPotentialDeltas();
        }
    }

}
