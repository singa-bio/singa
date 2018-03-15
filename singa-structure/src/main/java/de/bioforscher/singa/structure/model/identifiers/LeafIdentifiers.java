package de.bioforscher.singa.structure.model.identifiers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class LeafIdentifiers {

    public static List<LeafIdentifier> of(String... identifers) {
        return Arrays.stream(identifers).map(LeafIdentifier::fromSimpleString).collect(Collectors.toList());
    }

}
