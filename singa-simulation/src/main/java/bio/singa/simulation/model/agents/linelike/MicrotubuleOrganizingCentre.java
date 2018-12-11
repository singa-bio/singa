package bio.singa.simulation.model.agents.linelike;

import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.simulation.Simulation;

/**
 * @author cl
 */
public class MicrotubuleOrganizingCentre {

    private Simulation simulation;
    private MembraneLayer membraneLayer;

    private Circle circleRepresentation;
    private int initialFilaments;

    public MicrotubuleOrganizingCentre(Simulation simulation, MembraneLayer membraneLayer, Circle circleRepresentation, int initialFilaments) {
        this.simulation = simulation;
        this.circleRepresentation = circleRepresentation;
        this.initialFilaments = initialFilaments;
        this.membraneLayer = membraneLayer;
    }

    public LineLikeAgentLayer initializeMicrotubules() {
        LineLikeAgentLayer filamentLayer = new LineLikeAgentLayer(simulation, membraneLayer);
        membraneLayer.setMicrotubuleOrganizingCentre(this);
        // initialize filaments
        int currentFilaments = 0;
        Vector2D centre = circleRepresentation.getMidpoint();
        while (currentFilaments != initialFilaments) {
            // random point on circle circumference
            double angle = Math.random() * Math.PI * 2;
            double x = Math.cos(angle) * circleRepresentation.getRadius();
            double y = Math.sin(angle) * circleRepresentation.getRadius();
            // set starting position and direction
            Vector2D initialPosition = centre.add(new Vector2D(x, y));
            filamentLayer.addMicrotubule(initialPosition, centre.subtract(initialPosition));
            // increment filaments
            currentFilaments++;
        }
        // grow filaments
        while (filamentLayer.hasGrowingFilaments()) {
            filamentLayer.nextEpoch();
        }
        return filamentLayer;
    }

    public LineLikeAgentLayer initializeActin() {
        LineLikeAgentLayer filamentLayer = new LineLikeAgentLayer(simulation, membraneLayer);
        membraneLayer.setMicrotubuleOrganizingCentre(this);
        // initialize filaments
        int currentFilaments = 0;
        Vector2D centre = circleRepresentation.getMidpoint();
        while (currentFilaments != initialFilaments) {
            // random point on circle circumference
            double angle = Math.random() * Math.PI * 2;
            double x = Math.cos(angle) * circleRepresentation.getRadius();
            double y = Math.sin(angle) * circleRepresentation.getRadius();
            // set starting position and direction
            Vector2D initialPosition = centre.add(new Vector2D(x, y));
            filamentLayer.addActin(initialPosition, centre.subtract(initialPosition));
            // increment filaments
            currentFilaments++;
        }
        // grow filaments
        while (filamentLayer.hasGrowingFilaments()) {
            filamentLayer.nextEpoch();
        }
        return filamentLayer;
    }

    public Circle getCircleRepresentation() {
        return circleRepresentation;
    }

}
