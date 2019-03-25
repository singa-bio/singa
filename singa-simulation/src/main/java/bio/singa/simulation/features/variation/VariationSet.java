package bio.singa.simulation.features.variation;

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

    @Override
    public String toString() {
        return variations.toString();
    }
}
