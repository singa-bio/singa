package bio.singa.simulation.export.features;

import bio.singa.features.model.Feature;
import bio.singa.simulation.export.format.FormatFeature;

import java.util.stream.Collectors;

import static bio.singa.simulation.export.TeXTableSyntax.COLUMN_END_BREAKING;
import static bio.singa.simulation.export.TeXTableSyntax.COLUMN_SEPERATOR_SPACED;

/**
 * @author cl
 */
public class FeatureTableRow {

    public static String formatFeature(Feature<?> feature) {
        String identifier = String.valueOf(feature.getIdentifier());
        String type = feature.getDescriptor();
        String content = feature.getContent().toString();
        String evidence = FormatFeature.formatAllEvidence(feature).stream()
                .collect(Collectors.joining(",","\\cite{","}"));

        return identifier + COLUMN_SEPERATOR_SPACED +
                type + COLUMN_SEPERATOR_SPACED +
                content + COLUMN_SEPERATOR_SPACED +
                evidence + COLUMN_END_BREAKING;
    }

}
