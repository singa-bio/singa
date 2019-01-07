package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.StringFeature;

import java.util.List;

/**
 * @author cl
 */
public class AttachedFilament extends StringFeature {

    public AttachedFilament(String filamentType, List<Evidence> evidence) {
        super(filamentType, evidence);
    }

    public AttachedFilament(String filamentType, Evidence evidence) {
        super(filamentType, evidence);
    }

    public AttachedFilament(String filamentType) {
        super(filamentType);
    }

}
