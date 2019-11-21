package bio.singa.simulation.export.features;

import bio.singa.features.model.Feature;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class FeatureTable {

    public static String toTex(List<Feature<?>> features) {
        return features.stream()
                .map(FeatureTableRow::formatFeature)
                .collect(Collectors.joining());
    }

}
