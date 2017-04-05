package de.bioforscher.singa.chemistry.descriptive;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public final class ChemicalEntities {

    private ChemicalEntities() {

    }

    /**
     * Creates a Map, that references the Entities with their primary identifiers.
     * @param entities The entities to map.
     * @return The mapped entities.
     */
    public static Map<String, ChemicalEntity<?>> generateEntityMapFromSet(Set<ChemicalEntity<?>> entities) {
        return entities.stream()
                .collect(Collectors.toMap(entity -> entity.getIdentifier().toString(), Function.identity()));
    }
}
