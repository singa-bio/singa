package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.endocytosis.AttachmentDistance;
import bio.singa.simulation.model.agents.filaments.SkeletalFilament;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import tec.uom.se.ComparableQuantity;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.*;

import static bio.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.MICROTUBULE;
import static bio.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.UNATTACHED;

/**
 * @author cl
 */
public class VesicleAttachment extends QualitativeModule {

    private Map<Vesicle, AttachmentInformation> attachingVesicles;

    public VesicleAttachment() {
        attachingVesicles = new HashMap<>();
        // feature
        getRequiredFeatures().add(AttachmentDistance.class);
    }

    @Override
    public void calculateUpdates() {
        processVesicles(simulation.getVesicleLayer().getVesicles());
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    private void processVesicles(List<Vesicle> vesicles) {
        for (Vesicle vesicle : vesicles) {
            // only for unattached vesicles
            if (vesicle.getAttachmentState() == UNATTACHED) {
                AttachmentInformation attachmentInformation = determineClosestSegment(vesicle);
                ComparableQuantity<Length> threshold = getFeature(AttachmentDistance.class).getFeatureContent().add(vesicle.getRadius());
                Quantity<Length> distance = Environment.convertSimulationToSystemScale(attachmentInformation.getClosestDistance());
                if (threshold.isGreaterThanOrEqualTo(distance)) {
                    attachingVesicles.put(vesicle, attachmentInformation);
                }
            }
        }
    }

    private AttachmentInformation determineClosestSegment(Vesicle vesicle) {
        Vector2D centre = vesicle.getCurrentPosition();
        SkeletalFilament closestFilament = null;
        Vector2D closestSegment = null;
        double closestDistance = Double.MAX_VALUE;
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
        ListIterator<Vector2D> segmentIterator = null;
        if (closestSegment != null) {
            segmentIterator = closestFilament.getSegmentIterator(closestSegment);
        }
        return new AttachmentInformation(closestFilament, closestDistance, segmentIterator);
    }

    @Override
    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        attachingVesicles.clear();
    }

    @Override
    public void onCompletion() {
        for (Map.Entry<Vesicle, AttachmentInformation> entry : attachingVesicles.entrySet()) {
            attachVesicle(entry.getKey(), entry.getValue());
        }
    }

    private void attachVesicle(Vesicle vesicle, AttachmentInformation attachmentInformation) {
        vesicle.setAttachmentState(MICROTUBULE);
        vesicle.setAttachedFilament(attachmentInformation.getClosestFilament());
        vesicle.setSegmentIterator(attachmentInformation.getSegmentIterator());
    }

    private class AttachmentInformation {

        private SkeletalFilament closestFilament;
        private double closestDistance;
        private ListIterator<Vector2D> segmentIterator;

        public AttachmentInformation(SkeletalFilament closestFilament, double closestDistance, ListIterator<Vector2D> segmentIterator) {
            this.closestFilament = closestFilament;
            this.closestDistance = closestDistance;
            this.segmentIterator = segmentIterator;
        }

        public SkeletalFilament getClosestFilament() {
            return closestFilament;
        }

        public void setClosestFilament(SkeletalFilament closestFilament) {
            this.closestFilament = closestFilament;
        }

        public double getClosestDistance() {
            return closestDistance;
        }

        public void setClosestDistance(double closestDistance) {
            this.closestDistance = closestDistance;
        }

        public ListIterator<Vector2D> getSegmentIterator() {
            return segmentIterator;
        }

        public void setSegmentIterator(ListIterator<Vector2D> segmentIterator) {
            this.segmentIterator = segmentIterator;
        }
    }

}
