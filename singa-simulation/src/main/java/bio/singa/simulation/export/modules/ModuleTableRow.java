package bio.singa.simulation.export.modules;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static bio.singa.simulation.export.TeXFormat.COLUMN_END_NON_BREAKING;
import static bio.singa.simulation.export.TeXFormat.formatTableMultiColumn;

/**
 * @author cl
 */
public interface ModuleTableRow {

    String fullSpace = "\\addlinespace[1em]\n";

    default String generateHeader(String identifier) {
        return formatTableMultiColumn("\\textbf{" + identifier + "}", 2) + COLUMN_END_NON_BREAKING;
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
                parameters.append(COLUMN_END_NON_BREAKING);
            }
            for (int evidenceCounter = 0; evidenceCounter < evidenceCount; evidenceCounter++) {
                String evidenceString = entry.getValue().get(evidenceCounter);
                parameters.append("& \\hspace{1em}").append(evidenceString);
                // last feature
                if (featureCounter == featureCount) {
                    // and last evidence
                    if (evidenceCounter == evidenceCount - 1) {

                    } else {
                        parameters.append(COLUMN_END_NON_BREAKING);
                    }
                } else {
                    parameters.append(COLUMN_END_NON_BREAKING);
                }
            }
        }
        return parameters.toString();
    }



    String toRow();

}
