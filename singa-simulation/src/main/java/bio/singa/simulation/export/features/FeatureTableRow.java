package bio.singa.simulation.export.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.features.model.QuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.concentrations.InitialConcentration;

import javax.measure.Quantity;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.simulation.export.TeXFormat.*;
import static tech.units.indriya.AbstractUnit.ONE;

/**
 * @author cl
 */
public class FeatureTableRow {

    public static String formatFeature(Feature<?> feature) {
        return formatFeature(feature, String.valueOf(feature.getIdentifier()));
    }

    public static String formatFeature(Feature<?> feature, String identifier) {
        String type = feature.getDescriptor();
        String content = "";
        String unit = "";
        String comment = feature.getComment();
        if (feature instanceof InitialConcentration) {
            InitialConcentration concentration = (InitialConcentration) feature;
            Quantity<MolarConcentration> quantity = UnitRegistry.humanReadable(concentration.getConcentration());
            content = quantity.getValue().toString();
            unit = quantity.getUnit().toString();
            comment = "of entity " + concentration.getEntity().getIdentifier() + " in " + concentration.getLocation();
            content = formatTableNumberColumnValue(formatStates(content));
            unit = replaceTextMu(unit);
            String evidence = formatEvidence(feature.getAllEvidence());
            return identifier + COLUMN_SEPERATOR_SPACED +
                    type + COLUMN_SEPERATOR_SPACED +
                    content + COLUMN_SEPERATOR_SPACED +
                    unit + COLUMN_SEPERATOR_SPACED +
                    evidence + COLUMN_END_NON_BREAKING +
                    COLUMN_SEPERATOR_SPACED +
                    formatTableMultiColumn(setCursive(comment), 4) +
                    "\\\\ [1ex]\n";
        } else if (QuantitativeFeature.class.isAssignableFrom(feature.getClass())) {
            Quantity<?> quantity = ((QuantitativeFeature<?>) feature).getContent();
            if (quantity.getUnit().equals(ONE)) {
                content = String.valueOf(quantity.getValue());
            } else {
                Quantity<?> convertedQuantity = UnitRegistry.humanReadable(quantity);
                content = String.valueOf(convertedQuantity.getValue());
                unit = String.valueOf(convertedQuantity.getUnit());
            }
            content = formatTableNumberColumnValue(formatStates(content));
            unit = replaceTextMu(unit);
            String evidence = formatEvidence(feature.getAllEvidence());
            return identifier + COLUMN_SEPERATOR_SPACED +
                    type + COLUMN_SEPERATOR_SPACED +
                    content + COLUMN_SEPERATOR_SPACED +
                    unit + COLUMN_SEPERATOR_SPACED +
                    evidence + COLUMN_END_NON_BREAKING +
                    COLUMN_SEPERATOR_SPACED +
                    formatTableMultiColumn(setCursive(comment), 4) +
                    "\\\\ [1ex]\n";
        }

        content = formatStates(feature.getContent().toString());
        String evidence = formatEvidence(feature.getAllEvidence());
        return identifier + COLUMN_SEPERATOR_SPACED +
                type + COLUMN_SEPERATOR_SPACED +
                content + COLUMN_SEPERATOR_SPACED +
                evidence + COLUMN_END_NON_BREAKING +
                COLUMN_SEPERATOR_SPACED +
                formatTableMultiColumn(setCursive(comment), 3) +
                "\\\\ [1ex]\n";

    }

    public static String formatCompoundFeature(Feature<?> feature, String identifier, List<String> refernces) {
        String type = feature.getDescriptor();
        String content = String.join(", ", refernces);
        String evidence = formatEvidence(feature.getAllEvidence());
        return identifier + COLUMN_SEPERATOR_SPACED +
                type + COLUMN_SEPERATOR_SPACED +
                content + COLUMN_SEPERATOR_SPACED +
                evidence + COLUMN_SEPERATOR_SPACED +
                COLUMN_END_BREAKING;
    }

    public static String formatEvidence(List<Evidence> evidences) {
        if (evidences.isEmpty()) {
            return "";
        }
        return evidences.stream()
                .map(e -> e.getIdentifier().replace(" ", ""))
                .collect(Collectors.joining(",", "\\cite{", "}"));
    }

    public static String formatStates(String string) {
        if (string.contains("_")) {
            string = setMonoSpace(string);
            string = string.replace("_", "\\char" + "\u0060" + "_");
        }
        return string;
    }


}
