package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.AttachmentDistance;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.concentration.ModuleState;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.macroscopic.filaments.SkeletalFilament;
import tec.uom.se.ComparableQuantity;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.MICROTUBULE;
import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.UNATTACHED;

/**
 * @author cl
 */
public class VesicleAttachment extends DisplacementBasedModule {

    private SkeletalFilament closestFilament;
    private double closestDistance;
    private ListIterator<Vector2D> segmentIterator;

    public VesicleAttachment() {
        // feature
        getRequiredFeatures().add(AttachmentDistance.class);
    }

    @Override
    public void calculateUpdates() {
        List<Vesicle> vesicles = simulation.getVesicleLayer().getVesicles();
        for (Vesicle vesicle : vesicles) {
            // only for unattached vesicles
            if (vesicle.getAttachmentState() == UNATTACHED) {
                determineClosestSegment(vesicle);
                ComparableQuantity<Length> threshold = getFeature(AttachmentDistance.class).getFeatureContent().add(vesicle.getRadius());
                Quantity<Length> distance = Environment.convertSimulationToSystemScale(closestDistance);
                if (threshold.isGreaterThanOrEqualTo(distance)) {
                    vesicle.setAttachmentState(MICROTUBULE);
                    vesicle.setAttachedFilament(closestFilament);
                    vesicle.setSegmentIterator(segmentIterator);
                }
            }
        }
        state = ModuleState.SUCCEEDED;
    }

    private void determineClosestSegment(Vesicle vesicle) {
        Vector2D centre = vesicle.getCurrentPosition();
        closestFilament = null;
        Vector2D closestSegment = null;
        closestDistance = Double.MAX_VALUE;
        // get closest relevant node
        for (AutomatonNode node : vesicle.getAssociatedNodes().keySet()) {
            // get relevant segments
            for (Map.Entry<SkeletalFilament, Set<Vector2D>> entry : node.getMicrotubuleSegments().entrySet()) {
                SkeletalFilament currentFilament = entry.getKey();
                Set<Vector2D> segments = entry.getValue();
                // check each segment
                for (Vector2D currentSegment : segments) {
                    double currentDistance = centre.distanceTo(currentSegment);
                    if (currentDistance < closestDistance) {
                        closestDistance = currentDistance;
                        closestFilament = currentFilament;
                        closestSegment = currentSegment;
                    }
                }
            }
        }
        if (closestSegment != null) {
            segmentIterator = closestFilament.getSegmentIterator(closestSegment);
        }
    }

}
