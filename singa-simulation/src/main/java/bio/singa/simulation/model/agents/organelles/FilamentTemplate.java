package bio.singa.simulation.model.agents.organelles;

import bio.singa.features.parameters.Environment;
import bio.singa.simulation.model.agents.filaments.SkeletalFilament;
import bio.singa.simulation.model.graphs.AutomatonGraph;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;

/**
 * @author cl
 */
public class FilamentTemplate {

    private Quantity<Length> scale;
    private List<SkeletalFilament> filaments;

    public FilamentTemplate(List<SkeletalFilament> filaments, Quantity<Length> scale) {
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
        filaments.forEach(filament -> filament.scale(scalingFactor));
    }

    public void reduce() {
        // resize to a handleable number of edges
        for (SkeletalFilament filament : filaments) {
            while (filament.getSegments().size() > 200) {
                filament.reduce();
            }
        }
    }

    public Quantity<Length> getScale() {
        return scale;
    }

    public List<SkeletalFilament> getFilaments() {
        return filaments;
    }

}
