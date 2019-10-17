package bio.singa.simulation.trajectories.nested;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.events.GraphUpdatedEvent;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Unit;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class NestedUpdateRecorder implements UpdateEventListener<GraphUpdatedEvent> {

    private Trajectories trajectories;
    private Simulation simulation;

    public NestedUpdateRecorder(Simulation simulation) {
        this(simulation, UnitRegistry.getTimeUnit(), UnitRegistry.getConcentrationUnit());
    }

    public NestedUpdateRecorder(Simulation simulation, Unit<Time> timeUnit, Unit<MolarConcentration> concentrationUnit) {
        this.simulation = simulation;
        trajectories = new Trajectories(timeUnit, concentrationUnit);
        trajectories.setSimulationWidth(simulation.getSimulationRegion().getWidth());
        trajectories.setSimulationHeight(simulation.getSimulationRegion().getHeight());
    }

    public Trajectories getTrajectories() {
        return trajectories;
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        trajectories.addTrajectoryData(event.getElapsedTime().to(trajectories.getTimeUnit()).getValue().doubleValue(),
                TrajectoryData.of(simulation.getUpdatables(), trajectories.getConcentrationUnit()));
    }

}
