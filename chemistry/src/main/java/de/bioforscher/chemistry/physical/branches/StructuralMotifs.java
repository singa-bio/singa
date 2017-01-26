package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.physical.families.MatcherFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;

import java.util.EnumSet;

/**
 * Utility methods that deal with {@link StructuralMotif}s.
 *
 * @author fk
 */
public class StructuralMotifs {

    /**
     * prevent instantiation
     */
    private StructuralMotifs() {

    }

    public static void assignExchanges(StructuralMotif structuralMotif, EnumSet<MatcherFamily> familyGroup) {
        for (LeafSubstructure<?, ?> leafSubstructure : structuralMotif.getLeafSubstructures()) {
            familyGroup.stream()
                    .filter(family -> family.getMembers().contains(leafSubstructure.getFamily()))
                    .forEach(familyMember -> {
                        structuralMotif.addExchangeableFamily(leafSubstructure.getLeafIdentifier(), familyMember);
                    });
        }
    }
}
