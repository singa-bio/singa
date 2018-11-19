package bio.singa.simulation.trajectories;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.events.GraphUpdatedEvent;

import javax.measure.Unit;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class TrajectoryObserver implements UpdateEventListener<GraphUpdatedEvent> {

    private Trajectories trajectories;

    public TrajectoryObserver() {
        trajectories = new Trajectories(UnitRegistry.getTimeUnit(), UnitRegistry.getConcentrationUnit());
    }

    public TrajectoryObserver(Unit<Time> timeUnit, Unit<MolarConcentration> concentrationUnit) {
        trajectories = new Trajectories(timeUnit, concentrationUnit);
    }

    public Trajectories getTrajectories() {
        return trajectories;
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        trajectories.addTrajectoryData(event.getElapsedTime().to(trajectories.getTimeUnit()).getValue().doubleValue(),
                TrajectoryData.of(event.getGraph().getNodes(), trajectories.getConcentrationUnit()));
    }

}
