package bio.singa.simulation.trajectories.nested;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class TrajectoryData {

    private Map<Updatable, ConcentrationData> concentrationData;

    private TrajectoryData() {
        concentrationData = new HashMap<>();
    }

    public Map<Updatable, ConcentrationData> getConcentrationData() {
        return concentrationData;
    }

    public static TrajectoryData of(Collection<AutomatonNode> updatables, Unit<MolarConcentration> concentrationUnit) {
        TrajectoryData data = new TrajectoryData();
        for (Updatable updatable : updatables) {
            data.concentrationData.put(updatable, ConcentrationData.of(updatable, concentrationUnit));
        }
        return data;
    }

}
