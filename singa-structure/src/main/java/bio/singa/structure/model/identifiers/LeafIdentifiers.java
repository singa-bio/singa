package bio.singa.structure.model.identifiers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class LeafIdentifiers {

    /**
     * Takes an array of leaf identifiers in simple string format (e.g. A-56) and returns {@link LeafIdentifier}s.
     *
     * @param identifers The identifiers in simple string format.
     * @return A list of {@link LeafIdentifier}s.
     */
    public static List<LeafIdentifier> of(String... identifers) {
        return Arrays.stream(identifers).map(LeafIdentifier::fromSimpleString).collect(Collectors.toList());
    }
}
