package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

/**
 * @author cl
 */
public class MultiStringFeature  extends QualitativeFeature<List<String>> {

    public MultiStringFeature(List<String> strings, List<Evidence> evidence) {
        super(strings, evidence);
    }

    public MultiStringFeature(List<String> strings, Evidence evidence) {
        super(strings, evidence);
    }

    public MultiStringFeature(List<String> strings) {
        super(strings);
    }

}
