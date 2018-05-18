package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;

import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC;

/**
 * @author cl
 */
public class VesicleLayer {

    private List<Vesicle> vesicles;
    private Rectangle rectangle;

    public VesicleLayer(Rectangle rectangle) {
        vesicles = new ArrayList<>();
        this.rectangle = rectangle;
    }

    public void addVesicle(Vesicle vesicle) {
        vesicles.add(vesicle);
    }

    public List<Vesicle> getVesicles() {
        return vesicles;
    }

    private void checkForCollisions() {
        //compare the distance to combined radii
        if (vesicles.size() > 1) {
            LabeledSymmetricMatrix<Vesicle> distances = SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(vesicles, Vesicle::getPotentialUpdate);
            for (Vesicle vesicle1 : vesicles) {
                // check for collisions with other vesicles
                for (Vesicle vesicle2 : vesicles) {
                    if (vesicle1 != vesicle2) {
                        double distance = distances.getValueForLabel(vesicle1, vesicle2);
                        double combinedRadii = EnvironmentalParameters.convertSystemToSimulationScale(vesicle1.getRadius()) * EnvironmentalParameters.convertSystemToSimulationScale(vesicle2.getRadius());
                        if (distance < combinedRadii) {
                            vesicle1.permitDisplacement();
                            break;
                        }
                    }
                }
                // check for collisions with border
                double x = vesicle1.getPotentialUpdate().getX();
                double y = vesicle1.getPotentialUpdate().getY();
                double r = EnvironmentalParameters.convertSystemToSimulationScale(vesicle1.getRadius());
                if (x - r < rectangle.getLeftMostXPosition() || x + r > rectangle.getRightMostXPosition() ||
                        y - r < rectangle.getBottomMostYPosition() || y + r > rectangle.getTopMostYPosition()) {
                    vesicle1.permitDisplacement();
                }
            }
        }
    }

    public void nextEpoch() {
        for (Vesicle vesicle : vesicles) {
            vesicle.calculateDisplacement(EnvironmentalParameters.getTimeStep());
        }
        checkForCollisions();
        for (Vesicle vesicle : vesicles) {
            vesicle.move();
        }
    }

}
