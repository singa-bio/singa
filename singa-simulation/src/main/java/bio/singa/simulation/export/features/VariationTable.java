package bio.singa.simulation.export.features;

import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureRegistry;
import bio.singa.features.model.QuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.export.TeXFormat;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static bio.singa.simulation.export.TeXFormat.*;

/**
 * @author cl
 */
public class VariationTable {

    private FeatureTable featureTable;
    private String texString;

    public VariationTable(FeatureTable featureTable) {
        this.featureTable = featureTable;
    }

    public static VariationTable from(FeatureTable featureTable) {
        VariationTable variationTable = new VariationTable(featureTable);
        variationTable.generate();
        return variationTable;
    }

    private void generate() {
        determineVariableFeatures();
        Map<Feature<?>, String> featureIdentifierMap = featureTable.getFeatureIdentifierMap();
        StringBuilder sb = new StringBuilder();
        List<QuantitativeFeature<?>> variableFeatures = determineVariableFeatures();

        Map<String, QuantitativeFeature<?>> sortedFeatures = new TreeMap<>();
        for (QuantitativeFeature<?> variableFeature : variableFeatures) {
            String id = featureIdentifierMap.get(variableFeature);
            sortedFeatures.put(id, variableFeature);
        }

        int totalVariations = 1;
        for (Map.Entry<String, QuantitativeFeature<?>> entry : sortedFeatures.entrySet()) {
            String key = entry.getKey();
            QuantitativeFeature<?> feature = entry.getValue();
            List<? extends Quantity<?>> alternativeContents = feature.getAlternativeContents();
            String values = alternativeContents.stream()
                    .map(UnitRegistry::humanReadable)
                    .map(quantitiy -> quantitiy.getValue().doubleValue())
                    .map(TeXFormat::formatNumber)
                    .collect(Collectors.joining(", "));
            String unit = String.valueOf(UnitRegistry.humanReadable(feature.getContent()).getUnit());
            unit = replaceTextMu(unit);
            int currentVariations = alternativeContents.size();
            totalVariations *= currentVariations;
            sb.append(key).append(COLUMN_SEPERATOR_SPACED)
                    .append(setMonoSpace(values)).append(COLUMN_SEPERATOR_SPACED)
                    .append(unit).append(COLUMN_SEPERATOR_SPACED)
                    .append(currentVariations).append(COLUMN_END_BREAKING);

        }

        sb.append(COLUMN_SEPERATOR_SPACED).append(COLUMN_SEPERATOR_SPACED)
                .append("Total variations").append(COLUMN_SEPERATOR_SPACED)
                .append(totalVariations).append(COLUMN_END_BREAKING);

        texString = sb.toString();
    }

    private List<QuantitativeFeature<?>> determineVariableFeatures() {
        List<QuantitativeFeature<?>> variableFeatures = new ArrayList<>();
        // collect all features that have variations associated to them
        for (QuantitativeFeature<?> quantitativeFeature : FeatureRegistry.getQuantitativeFeatures()) {
            if (!quantitativeFeature.getAlternativeContents().isEmpty()) {
                variableFeatures.add(quantitativeFeature);
            }
        }
        for (AbstractScalableQuantitativeFeature<?> scalableQuantitativeFeature : FeatureRegistry.getScalableQuantitativeFeatures()) {
            if (!scalableQuantitativeFeature.getAlternativeContents().isEmpty()) {
                variableFeatures.add(scalableQuantitativeFeature);
            }
        }
        return variableFeatures;
    }

    public FeatureTable getFeatureTable() {
        return featureTable;
    }

    public String getTexString() {
        return texString;
    }

}
