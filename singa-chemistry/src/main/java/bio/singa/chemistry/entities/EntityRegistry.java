package bio.singa.chemistry.entities;

import java.util.HashMap;

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

}
