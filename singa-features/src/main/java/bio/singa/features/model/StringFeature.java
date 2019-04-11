package bio.singa.features.model;

import java.util.List;

/**
 * @author cl
 */
public abstract class StringFeature extends QualitativeFeature<String> {

    public StringFeature(String content, List<Evidence> evidence) {
        super(content, evidence);
    }

    public StringFeature(String content, Evidence evidence) {
        super(content, evidence);
    }

    public StringFeature(String content) {
        super(content);
    }
}
