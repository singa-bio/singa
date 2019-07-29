package bio.singa.simulation.model.agents.linelike;

import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.sections.CellRegion;
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

    public void initializeActin(LineLikeAgentLayer layer, CellRegion targetedMembrane) {
        logger.info("Initializing actin network with {} filaments.", initialFilaments);
        membraneLayer.setMicrotubuleOrganizingCentre(this);
        if (targetedMembrane != null) {
            layer.setTargetedGrowth(true);
            layer.setTargetMembrane(targetedMembrane);
        } else {
            layer.setTargetedGrowth(false);
        }
        // initialize filaments
        int currentFilaments = 0;
        Vector2D centre = circleRepresentation.getMidpoint();
        while (currentFilaments != initialFilaments) {
            spawnActin(layer);
            // increment filaments
            currentFilaments++;
        }
        // grow filaments
        while (layer.hasGrowingFilaments()) {
            layer.nextEpoch();
            layer.purgeMisguidedFilaments();
        }
    }

    public void initializeActin(LineLikeAgentLayer layer) {
        initializeActin(layer, null);
    }

    public void spawnActin(LineLikeAgentLayer layer) {
        // random point on circle circumference
        double angle = Math.random() * Math.PI * 2;
        double x = Math.cos(angle) * circleRepresentation.getRadius();
        double y = Math.sin(angle) * circleRepresentation.getRadius();
        // set starting position and direction
        Vector2D initialPosition = circleRepresentation.getMidpoint().add(new Vector2D(x, y));
        layer.addActin(initialPosition, circleRepresentation.getMidpoint().subtract(initialPosition));
    }

    public Circle getCircleRepresentation() {
        return circleRepresentation;
    }

}
