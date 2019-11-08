package bio.singa.simulation.export.reactiontable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public interface ModuleTableRow {

    String fullSpace = "\\addlinespace[1em]\n";
    String nonBreakingColumnEnd = "\\\\*\n";
    String breakingColumnEnd = "\\\\\n";

    default String generateHeader(String identifier) {
        return multiColumnEnvironment("\\textbf{" + identifier + "}", 2) + nonBreakingColumnEnd;
    }

    default String generateFeatureString(Map<String, List<String>> featureMap) {
        StringBuilder parameters = new StringBuilder();
        Iterator<Map.Entry<String, List<String>>> entryIterator = featureMap.entrySet().iterator();
        int featureCounter = 0;
        int featureCount = featureMap.entrySet().size();
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = entryIterator.next();
            featureCounter++;
            parameters.append("& ").append(entry.getKey());
            int evidenceCount = entry.getValue().size();
            // no evidence and last feature
            if (evidenceCount == 0 && featureCounter == featureCount) {

            } else {
                parameters.append(nonBreakingColumnEnd);
            }
            for (int evidenceCounter = 0; evidenceCounter < evidenceCount; evidenceCounter++) {
                String evidenceString = entry.getValue().get(evidenceCounter);
                parameters.append("& \\hspace{1em}").append(evidenceString);
                // last feature
                if (featureCounter == featureCount) {
                    // and last evidence
                    if (evidenceCounter == evidenceCount - 1) {

                    } else {
                        parameters.append(nonBreakingColumnEnd);
                    }
                } else {
                    parameters.append(nonBreakingColumnEnd);
                }
            }
        }
        return parameters.toString();
    }

    default String multiColumnEnvironment(String content, int number) {
        return "\\multicolumn{" + number + "}{l}{" + content + "}";
    }

    String toRow();

}
