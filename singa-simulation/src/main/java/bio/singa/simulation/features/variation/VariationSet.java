package bio.singa.simulation.features.variation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Quantity;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class VariationSet {

    private List<Variation<?>> variations;

    public VariationSet() {
        variations = new ArrayList<>();
    }

    public List<Variation<?>> getVariations() {
        return variations;
    }

    public void setVariations(List<Variation<?>> variations) {
        this.variations = variations;
    }

    public void addVariation(Variation<?> variation) {
        variations.add(variation);
    }

    public void addAll(Variation<?>... variations) {
        this.variations.addAll(Arrays.asList(variations));
    }

    public String getAffectedParameters() {
        return variations.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(","));
    }

    public List<List<?>> generateAllCombinations() {
        List<List<?>> result = new ArrayList<>();
        collectAllCombinations(variations, result, 0, new ArrayList<>());
        return result;
    }

    // generate all combinations
    private void collectAllCombinations(List<Variation<?>> originalData, List<List<?>> result, int depth, List<?> permutation) {
        if (depth == originalData.size()) {
            result.add(permutation);
            return;
        }
        Variation<?> current = originalData.get(depth);
        for (Object variation : current.getVariations()) {
            List<Object> newPermutation = new ArrayList<>(permutation);
            Object instance = current.create(variation);
            newPermutation.add(instance);
            collectAllCombinations(originalData, result, depth + 1, newPermutation);
        }
    }

    public static void applyParameters(Simulation simulation, List<?> parameterVariations) {
        for (Object parameterVariation : parameterVariations) {
            if (parameterVariation instanceof InitialConcentration) {
                // varying concentration
                simulation.getConcentrationInitializer().addInitialConcentration((InitialConcentration) parameterVariation);
            } else if (parameterVariation instanceof EntityFeatureVariationEntry) {
                // varying feature of a entity
                Collection<ChemicalEntity> chemicalEntities = simulation.getChemicalEntities();
                EntityFeatureVariationEntry entityVariation = (EntityFeatureVariationEntry) parameterVariation;
                for (ChemicalEntity chemicalEntity : chemicalEntities) {
                    if (chemicalEntity.equals(entityVariation.getEntity())) {
                        chemicalEntity.setFeature(entityVariation.getFeature());
                        break;
                    }
                }
            } else if (parameterVariation instanceof ModuleFeatureVariationEntry) {
                // varying feature of a module
                List<UpdateModule> modules = simulation.getModules();
                ModuleFeatureVariationEntry moduleVariation = (ModuleFeatureVariationEntry) parameterVariation;
                for (UpdateModule module : modules) {
                    if (module.equals(moduleVariation.getModule())) {
                        module.setFeature(moduleVariation.getFeature());
                    }
                }
            } else {
                // nonsense variation
                throw new IllegalStateException("The parameter variation " + parameterVariation + " is invalid.");
            }
        }
    }

    public static void writeVariationLog(Path currentVariationSetPath, List<?> currentVariationSet) {
        String collect = currentVariationSet.stream()
                .map(Object::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        try {
            Files.write(currentVariationSetPath.resolve("variations.log"), collect.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write variation log to " + currentVariationSetPath + ".", e);
        }
    }

    public static void writeVariationResults(Path baseSimulation, VariationSet variations, Map<String, Double> resultingValues) {
        StringBuilder result = new StringBuilder();
        result.append(variations.getAffectedParameters())
                .append(System.lineSeparator());
        for (Map.Entry<String, Double> entry : resultingValues.entrySet()) {
            result.append(entry.getKey())
                    .append(",")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }
        try {
            Files.write(baseSimulation.getParent().resolve("variations_results.log"), result.toString().getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write variation results to " + baseSimulation + ".", e);
        }
    }

    public static String getValueString(Object parameter) {
        // add membrane concentration
        if (parameter instanceof SectionConcentration) {
            // varying concentration
            return String.valueOf(MolarConcentration.concentrationToMolecules(((SectionConcentration) parameter).getConcentration()
                    .to(UnitRegistry.getConcentrationUnit()).getValue().doubleValue())
                    .getValue().doubleValue());
        } else if (parameter instanceof EntityFeatureVariationEntry) {
            // varying feature of a entity
            Object featureContent = ((EntityFeatureVariationEntry) parameter).getFeature().getContent();
            if (featureContent instanceof Quantity) {
                return String.valueOf(((Quantity) featureContent).getValue().doubleValue());
            }
            return String.valueOf(featureContent);
        } else if (parameter instanceof ModuleFeatureVariationEntry) {
            // varying feature of a module
            Object featureContent = ((ModuleFeatureVariationEntry) parameter).getFeature().getContent();
            if (featureContent instanceof Quantity) {
                return String.valueOf(((Quantity) featureContent).getValue().doubleValue());
            }
            return String.valueOf(featureContent);
        } else {
            // nonsense variation
            throw new IllegalArgumentException("The parameter " + parameter + " is not a parameter.");
        }
    }

    @Override
    public String toString() {
        return variations.toString();
    }
}
