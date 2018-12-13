package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.AttachedFilament;
import bio.singa.simulation.features.AttachedMotor;
import bio.singa.simulation.features.AttachmentDistance;
import bio.singa.simulation.features.MotorPullDirection;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellTopology;
import tec.uom.se.ComparableQuantity;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.*;

import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.FilamentType;
import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.FilamentType.MICROTUBULE;

/**
 * @author cl
 */
public class LineLikeAgentAttachment extends QualitativeModule {

    private Map<Vesicle, AttachmentInformation> attachingVesicles;

    public LineLikeAgentAttachment() {
        attachingVesicles = new HashMap<>();
        // feature
        getRequiredFeatures().add(AttachedMotor.class);
        getRequiredFeatures().add(AttachedFilament.class);
        getRequiredFeatures().add(MotorPullDirection.class);
        getRequiredFeatures().add(AttachmentDistance.class);
    }

    @Override
    public void calculateUpdates() {
        processVesicles(simulation.getVesicleLayer().getVesicles());
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    private void processVesicles(List<Vesicle> vesicles) {
        ChemicalEntity motor = getFeature(AttachedMotor.class).getContent();
        for (Vesicle vesicle : vesicles) {
            // at least one motor is available
            Optional<ChemicalEntity> motorEntity = vesicle.getConcentrationContainer().containsEntity(CellTopology.MEMBRANE, motor);
            if (motorEntity.isPresent()) {
                double molarConcentrationQuantity = vesicle.getConcentrationContainer().get(CellTopology.MEMBRANE, motorEntity.get());
                if (MolarConcentration.concentrationToMolecules(molarConcentrationQuantity).getValue().intValue() < 1) {
                    continue;
                }
            } else {
                continue;
            }
            // and the current vesicle is unattached
            if (!vesicle.getVesicleState().equals(VesicleStateRegistry.UNATTACHED)) {
                continue;
            }
            // attach if there is any close filament
            AttachmentInformation attachmentInformation = determineClosestSegment(vesicle);
            ComparableQuantity<Length> threshold = (ComparableQuantity<Length>) getFeature(AttachmentDistance.class).getContent().add(vesicle.getRadius());
            Quantity<Length> distance = Environment.convertSimulationToSystemScale(attachmentInformation.getClosestDistance());
            if (threshold.isGreaterThanOrEqualTo(distance)) {
                attachingVesicles.put(vesicle, attachmentInformation);
            }
        }
    }

    private AttachmentInformation determineClosestSegment(Vesicle vesicle) {
        Vector2D centre = vesicle.getCurrentPosition();
        LineLikeAgent closestFilament = null;
        Vector2D closestSegment = null;
        double closestDistance = Double.MAX_VALUE;
        FilamentType filamentType = getFeature(AttachedFilament.class).getContent();
        // get closest relevant node
        for (AutomatonNode node : vesicle.getAssociatedNodes().keySet()) {
            // get relevant path
            for (Map.Entry<LineLikeAgent, Set<Vector2D>> entry : node.getAssociatedLineLikeAgents().entrySet()) {
                LineLikeAgent currentFilament = entry.getKey();
                // check if the filament has the right type
                if (currentFilament.getType().equals(filamentType)) {
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
        }
        ListIterator<Vector2D> segmentIterator = null;
        if (closestSegment != null) {
            segmentIterator = closestFilament.getPath().getSegmentIterator(closestSegment);
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
        FilamentType filamentType = getFeature(AttachedFilament.class).getContent();
        if (filamentType.equals(MICROTUBULE)) {
            vesicle.setVesicleState(VesicleStateRegistry.MICROTUBULE_ATTACHED);
        } else {
            vesicle.setVesicleState(VesicleStateRegistry.ACTIN_ATTACHED);
        }
        vesicle.setTargetDirection(getFeature(MotorPullDirection.class).getContent());
        vesicle.setAttachedFilament(attachmentInformation.getClosestFilament());
        vesicle.setSegmentIterator(attachmentInformation.getSegmentIterator());
    }

    private class AttachmentInformation {

        private LineLikeAgent closestFilament;
        private double closestDistance;
        private ListIterator<Vector2D> segmentIterator;

        public AttachmentInformation(LineLikeAgent closestFilament, double closestDistance, ListIterator<Vector2D> segmentIterator) {
            this.closestFilament = closestFilament;
            this.closestDistance = closestDistance;
            this.segmentIterator = segmentIterator;
        }

        public LineLikeAgent getClosestFilament() {
            return closestFilament;
        }

        public void setClosestFilament(LineLikeAgent closestFilament) {
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
