package bio.singa.simulation.model.agents.linelike;

import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cl
 */
public class MicrotubuleOrganizingCentre {

    private static final Logger logger = LoggerFactory.getLogger(MicrotubuleOrganizingCentre.class);

    private MembraneLayer membraneLayer;
    private Circle circleRepresentation;
    private int initialFilaments;

    public MicrotubuleOrganizingCentre(MembraneLayer membraneLayer, Circle circleRepresentation, int initialFilaments) {
        this.circleRepresentation = circleRepresentation;
        this.initialFilaments = initialFilaments;
        this.membraneLayer = membraneLayer;
    }

    public void initializeMicrotubules(LineLikeAgentLayer layer) {
        logger.info("Initializing microtubule network with {} filaments.", initialFilaments);
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
            layer.addMicrotubule(initialPosition, centre.subtract(initialPosition));
            // increment filaments
            currentFilaments++;
        }
        // grow filaments
        while (layer.hasGrowingFilaments()) {
            layer.nextEpoch();
        }
        layer.purgeMisguidedFilaments();
    }

    public void initializeActin(LineLikeAgentLayer layer) {
        logger.info("Initializing actin network with {} filaments.", initialFilaments);
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
            layer.addActin(initialPosition, centre.subtract(initialPosition));
            // increment filaments
            currentFilaments++;
        }
        // grow filaments
        while (layer.hasGrowingFilaments()) {
            layer.nextEpoch();
        }
        layer.purgeMisguidedFilaments();
    }

    public Circle getCircleRepresentation() {
        return circleRepresentation;
    }

}
