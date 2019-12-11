package bio.singa.simulation.trajectories.errors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.events.GraphUpdatedEvent;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.sections.CellSubsection;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class ErrorRecorder implements UpdateEventListener<GraphUpdatedEvent>  {

    private ConcentrationBasedModule module;
    private ChemicalEntity entity;
    private CellSubsection subsection;
    private Map<Quantity<Time>, Map<RectangularCoordinate, Double>> errorMap;

    public ErrorRecorder(ConcentrationBasedModule module, ChemicalEntity entity, CellSubsection subsection) {
        this.module = module;
        this.entity = entity;
        this.subsection = subsection;
        errorMap = new HashMap<>();
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> fullDeltas = module.getSupplier().getCurrentFullDeltas();
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> halfDeltas = module.getSupplier().getCurrentHalfDeltas();
        Map<RectangularCoordinate, Double> map = errorMap.put(event.getElapsedTime(), new HashMap<>());
        for (Map.Entry<ConcentrationDeltaIdentifier, ConcentrationDelta> entry : fullDeltas.entrySet()) {
            ConcentrationDeltaIdentifier identifier = entry.getKey();
            if (identifier.getEntity().equals(entity) && identifier.getSubsection().equals(subsection)) {
                double halfDelta = halfDeltas.get(identifier).getValue();
                double fullDelta = entry.getValue().getValue();
                // calculate error
                double error = Math.abs(1 - (fullDelta / halfDelta));
                RectangularCoordinate coordinate = RectangularCoordinate.fromString(identifier.getUpdatable().getStringIdentifier().replace("n", ""));
                map.put(coordinate, error);
            }
        }
    }
}
