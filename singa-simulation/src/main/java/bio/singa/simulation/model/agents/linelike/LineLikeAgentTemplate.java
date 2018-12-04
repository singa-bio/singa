package bio.singa.simulation.model.agents.linelike;

import bio.singa.features.parameters.Environment;
import bio.singa.simulation.model.graphs.AutomatonGraph;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

/**
 * @author cl
 */
public class LineLikeAgentTemplate {

    private Quantity<Length> scale;
    private List<LineLikeAgent> filaments;

    public LineLikeAgentTemplate(List<LineLikeAgent> filaments, Quantity<Length> scale) {
        this.scale = scale;
        this.filaments = filaments;
    }

    public void associateInGraph(AutomatonGraph graph) {
        filaments.forEach(filament -> filament.associateInGraph(graph));
    }

    public void mapToSystemExtend() {
        Quantity<Length> systemScale = Environment.convertSimulationToSystemScale(1).to(scale.getUnit());
        scale(scale.getValue().doubleValue()/systemScale.getValue().doubleValue());
    }

    public void scale(double scalingFactor) {
        filaments.forEach(filament -> filament.setPath(filament.getPath().scale(scalingFactor)));
    }

    public void reduce() {
        // resize to a handleable number of edges
        for (LineLikeAgent filament : filaments) {
            while (filament.getPath().size() > 200) {
                filament.setPath(filament.getPath().reduce());
            }
        }
    }

    public Quantity<Length> getScale() {
        return scale;
    }

    public List<LineLikeAgent> getFilaments() {
        return filaments;
    }

}
