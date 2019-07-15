package bio.singa.simulation.export.format;

import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.model.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class FormatFeature {

    private static String getPrefix(RateConstant rate) {
        if (rate instanceof ForwardsRateConstant) {
            return "";
        } else if (rate instanceof BackwardsRateConstant) {
            return "-";
        } else {
            return ";";
        }
    }

    public static String formatFeatures(List<Feature> features) {
        List<String> featureStrings = new ArrayList<>();
        for (Feature feature : features) {
            if (feature instanceof RateConstant) {
                featureStrings.add(formatRate(((RateConstant) feature)));
            }
        }

        return String.join(", ", featureStrings);
    }

    public static String formatRate(RateConstant rate) {
        return "k$_{" + FormatFeature.getPrefix(rate) + "1}$ = " + rate.getContent();
    }

}
