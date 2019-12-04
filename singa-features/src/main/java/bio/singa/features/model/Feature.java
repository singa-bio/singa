package bio.singa.features.model;

import java.util.List;

/**
 *
 * @author cl
 */
public interface Feature<FeatureContent> {

    int getIdentifier();
    void setIdentifier(int identifier);
    FeatureContent getContent();
    String getDescriptor();

    List<FeatureContent> getAlternativeContents();
    void addAlternativeContent(FeatureContent alternativeContent);
    void setAlternativeContent(int index);

    Evidence getPrimaryEvidence();
    List<Evidence> getAllEvidence();
    void addEvidence(Evidence evidence);

    String getComment();
    void setComment(String comment);

}
