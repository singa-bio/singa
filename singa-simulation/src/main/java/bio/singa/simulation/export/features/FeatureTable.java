package bio.singa.simulation.export.features;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.EntityRegistry;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Feature;
import bio.singa.features.model.QuantitativeFeature;
import bio.singa.simulation.features.InitialConcentrations;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class FeatureTable {

    private Map<Feature<?>, String> featureIdentifierMap;
    private String teXString;

    private boolean useContinuousIndex;

    public FeatureTable() {
        featureIdentifierMap = new HashMap<>();
        useContinuousIndex = true;
    }

    public static String toTex(List<Feature<?>> features) {
        return features.stream()
                .map(FeatureTableRow::formatFeature)
                .collect(Collectors.joining());
    }

    public static FeatureTable fromSimulation(Simulation simulation) {
        FeatureTable table = new FeatureTable();
        table.generate(simulation);
        return table;
    }

    public boolean isUseContinuousIndex() {
        return useContinuousIndex;
    }

    public void setUseContinuousIndex(boolean useContinuousIndex) {
        this.useContinuousIndex = useContinuousIndex;
    }

    public void generate(Simulation simulation) {
        List<Feature<?>> features = new ArrayList<>();
        for (UpdateModule module : simulation.getModules()) {
            for (Feature<?> feature : module.getFeatures()) {
                features.add(feature);
                if (feature instanceof InitialConcentrations) {
                    List<InitialConcentration> concentrations = ((InitialConcentrations) feature).getContent();
                    for (InitialConcentration concentration : concentrations) {
                        if (!features.contains(concentration)) {
                            features.add(concentration);
                        }
                    }
                }
            }
        }
        for (ChemicalEntity chemicalEntity : EntityRegistry.getAll()) {
            for (Feature<?> feature : chemicalEntity.getFeatures()) {
                if (!(feature instanceof Identifier)) {
                    features.add(feature);
                }
            }
        }
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            for (Feature<?> feature : vesicle.getFeatures()) {
                if (!features.contains(feature)) {
                    features.add(feature);
                }
            }
        }
        for (InitialConcentration concentration : simulation.getConcentrations()) {
            if (!features.contains(concentration)) {
                features.add(concentration);
            }
        }

        int featureCounter = 1;
        String formatString = "F%0" + (int) (Math.log10(features.size()) + 1) + "d";
        for (Feature<?> feature : features) {
            if (!featureIdentifierMap.containsKey(feature)) {
                String identifier = String.format(formatString, featureCounter);
                featureCounter++;
                featureIdentifierMap.put(feature, identifier);
            }
        }

        List<String> quanitativeRows = new ArrayList<>();
        List<String> qualitativeRows = new ArrayList<>();
        for (Map.Entry<Feature<?>, String> featureStringEntry : featureIdentifierMap.entrySet()) {
            Feature<?> feature = featureStringEntry.getKey();
            String identifier = featureStringEntry.getValue();
            if (QuantitativeFeature.class.isAssignableFrom(feature.getClass())) {
                quanitativeRows.add(FeatureTableRow.formatFeature(feature, identifier));
            } else {
                if (feature instanceof InitialConcentrations) {
                    List<InitialConcentration> concentrations = ((InitialConcentrations) feature).getContent();
                    List<String> referencedIdentifiers = new ArrayList<>();
                    for (InitialConcentration concentration : concentrations) {
                        referencedIdentifiers.add(featureIdentifierMap.get(concentration));
                    }
                    qualitativeRows.add(FeatureTableRow.formatCompoundFeature(feature, identifier, referencedIdentifiers));
                } else {
                    qualitativeRows.add(FeatureTableRow.formatFeature(feature, identifier));
                }
            }
        }
        quanitativeRows.sort(String::compareTo);
        qualitativeRows.sort(String::compareTo);
        teXString = String.join("", quanitativeRows) + "\n"
                + String.join("", qualitativeRows);
    }

    public Map<Feature<?>, String> getFeatureIdentifierMap() {
        return featureIdentifierMap;
    }

    public void setFeatureIdentifierMap(Map<Feature<?>, String> featureIdentifierMap) {
        this.featureIdentifierMap = featureIdentifierMap;
    }

    public String getTeXString() {
        return teXString;
    }

    public void setTeXString(String teXString) {
        this.teXString = teXString;
    }

}
