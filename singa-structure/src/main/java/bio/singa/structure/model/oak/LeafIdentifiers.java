package bio.singa.structure.model.oak;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class LeafIdentifiers {

    /**
     * Takes an array of leaf identifiers in simple string format (e.g. A-56) and returns {@link PdbLeafIdentifier}s.
     *
     * @param identifers The identifiers in simple string format.
     * @return A list of {@link PdbLeafIdentifier}s.
     */
    public static List<PdbLeafIdentifier> of(String... identifers) {
        return Arrays.stream(identifers).map(PdbLeafIdentifier::fromSimpleString).collect(Collectors.toList());
    }
}
