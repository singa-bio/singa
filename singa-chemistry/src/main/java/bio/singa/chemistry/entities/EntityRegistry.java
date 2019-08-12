package bio.singa.chemistry.entities;

import bio.singa.core.utility.ListHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class EntityRegistry {

    private static EntityRegistry instance = getInstance();

    private HashMap<String, ChemicalEntity> entities;

    private static EntityRegistry getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    public static void reinitialize() {
        synchronized (EntityRegistry.class) {
            instance = new EntityRegistry();
        }
    }

    private EntityRegistry() {
        entities = new HashMap<>();
    }

    public static void put(String identifier, ChemicalEntity entity) {
        if (entity instanceof ComplexEntity) {
            // do not allow entities that are only small molecules or binding sites in complexes
            if (((ComplexEntity) entity).getProteins().size() < 1) {
                return;
            }
        }
        getInstance().entities.put(identifier, entity);
    }

    public static ChemicalEntity get(String referenceIdentifier) {
        return getInstance().entities.get(referenceIdentifier);
    }

    public static ChemicalEntity matchExactly(String... containedIdentifiers) {
        List<String> identifiers = Arrays.asList(containedIdentifiers);
        // for each entity
        for (String referenceIdentifier : getInstance().entities.keySet()) {
            // check if both have the same elements
            List<String> split = Arrays.asList(referenceIdentifier.split("-"));
            if (ListHelper.haveSameElements(split, identifiers)) {
                return get(referenceIdentifier);
            }
        }
        return null;
    }

    public static List<String> listEntities() {
        return getInstance().entities.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue())
                .sorted()
                .collect(Collectors.toList());
    }

}
