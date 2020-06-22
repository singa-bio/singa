package bio.singa.simulation.entities;

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
        getInstance().entities.put(identifier, entity);
    }

    public static void put(ChemicalEntity entity) {
        getInstance().entities.put(entity.getIdentifier(), entity);
    }

    public static ChemicalEntity get(String referenceIdentifier) {
        return getInstance().entities.get(referenceIdentifier);
    }

    public static boolean contains(String referenceIdentifier) {
        return getInstance().entities.containsKey(referenceIdentifier);
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

    public static List<ChemicalEntity> allWith(String... containedIdentifiers) {
        List<ChemicalEntity> matches = new ArrayList<>();
        List<String> identifiers = Arrays.asList(containedIdentifiers);
        // for each entity
        for (String referenceIdentifier : getInstance().entities.keySet()) {
            // check if both have the same elements
            List<String> split = Arrays.asList(referenceIdentifier.split("-"));
            if (split.containsAll(identifiers)) {
                matches.add(getInstance().entities.get(referenceIdentifier));
            }
        }
        return matches;
    }

    public static List<String> listEntities() {
        return getInstance().entities.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue())
                .sorted()
                .collect(Collectors.toList());
    }

    public static  Collection<ChemicalEntity> getAll() {
        return getInstance().entities.values();
    }

}
