package bio.singa.mathematics.combinatorics;

import bio.singa.core.parameters.MixedParameterList;
import bio.singa.core.parameters.ParameterValue;
import bio.singa.core.parameters.UniqueParameterList;

import java.util.ArrayList;
import java.util.List;

public class Permutations {

    public static List<MixedParameterList> generateAllSubsets(List<UniqueParameterList<?>> lists) {
        List<MixedParameterList> result = new ArrayList<>();
        collectAllSubsets(lists, result, 0, new MixedParameterList());
        return result;
    }

    // generates all subsets
    private static void collectAllSubsets(List<UniqueParameterList<?>> originalData, List<MixedParameterList> result, int depth,
                                          MixedParameterList permutation) {

        if (depth == originalData.size()) {
            result.add(permutation);
            return;
        }

        UniqueParameterList<?> currentList = originalData.get(depth);
        for (Object currentValue : currentList.getValues()) {
            MixedParameterList newPermutation = permutation.clone();
            permutation.add((ParameterValue<?>) currentValue);
            collectAllSubsets(originalData, result, depth + 1, newPermutation);
        }

    }

    /**
     * The method returns a list that contains all combinations of of the given
     * lists, with at most one element of each list is contained in the
     * resulting set. The indices of the resulting {@link MixedParameterList} is
     * preserved.
     * <p>
     * An Example:
     * <p>
     * S0 = {A,B,C}, |S0| = 3
     * S1 = {D,E} , |S1| = 2
     * S2 = {A,E} , |S2| = 2
     * S = { S0, S1, S2 }
     * m = |S0| * |S1| * |S0| = 3 * 2 * 2 = 12 combinations
     * <p>
     * Combinations: [A, D, A] [A, D, E] [A, E, A] [A, E, E] [B, D, A] [B, D, E]
     * [B, E, A] [B, E, E] [C, D, A] [C, D, E] [C, E, A] [C, E, E]
     *
     * @param lists The lists.
     * @return The combinations of the lists.
     */
    public static List<MixedParameterList> generateAllCombinations(List<UniqueParameterList<?>> lists) {
        List<MixedParameterList> result = new ArrayList<>();
        collectAllCombinations(lists, result, 0, new MixedParameterList());
        return result;
    }

    // generate all combinations
    private static void collectAllCombinations(List<UniqueParameterList<?>> originalData, List<MixedParameterList> result, int depth,
                                               MixedParameterList permutation) {

        if (depth == originalData.size()) {
            result.add(permutation);
            return;
        }

        UniqueParameterList<?> currentList = originalData.get(depth);
        for (Object currentValue : currentList.getValues()) {
            MixedParameterList newPermutation = permutation.clone();
            newPermutation.add((ParameterValue<?>) currentValue);
            collectAllCombinations(originalData, result, depth + 1, newPermutation);
        }

    }

}
