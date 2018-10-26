package bio.singa.simulation.features.variation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.intervals.Sampler;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;

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

    public List<Set<?>> generateAllCombinations() {
        List<Set<?>> result = new ArrayList<>();
        collectAllCombinations(variations, result, 0, new HashSet<>());
        return result;
    }

    // generate all combinations
    private void collectAllCombinations(List<Variation<?>> originalData, List<Set<?>> result, int depth, Set<?> permutation) {
        if (depth == originalData.size()) {
            result.add(permutation);
            return;
        }
        Variation<?> current = originalData.get(depth);
        for (Object variation : current.getVariations()) {
            HashSet<Object> newPermutation = new HashSet<>(permutation);
            Object instance = current.create(variation);
            newPermutation.add(instance);
            collectAllCombinations(originalData, result, depth + 1, newPermutation);
        }
    }

    public static void main(String[] args) {
        VariationSet vs = new VariationSet();

        CellRegion region = new CellRegion("R1");
        CellSubsection subsection = new CellSubsection("S1");

        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ConcentrationVariation first = new ConcentrationVariation(region, subsection, a);
        List<Double> firstSamples = Sampler.sampleMultiplicative(1, 10, 10);
        firstSamples.stream()
                .map(sample -> new MolarConcentration(sample, MOLE_PER_LITRE))
                .forEach(first::addVariation);

        ConcentrationVariation second = new ConcentrationVariation(region, subsection, b);
        List<Double> secondSamples = Sampler.sampleExponentially(-4, 2, 1);
        secondSamples.stream()
                .map(sample -> new MolarConcentration(sample, MOLE_PER_LITRE))
                .forEach(second::addVariation);

        vs.addVariation(first);
        vs.addVariation(second);

        List<Set<?>> sets = vs.generateAllCombinations();
        System.out.println();


    }


}
