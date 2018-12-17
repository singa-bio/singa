package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.FilamentType;

/**
 * @author cl
 */
public class AttachedFilament extends QualitativeFeature<FilamentType> {

    public AttachedFilament(FilamentType filamentType, List<Evidence> evidence) {
        super(filamentType, evidence);
    }

    public AttachedFilament(FilamentType filamentType, Evidence evidence) {
        super(filamentType, evidence);
    }

    public AttachedFilament(FilamentType filamentType) {
        super(filamentType);
    }

}
