package bio.singa.simulation.model.parameters;

import java.util.HashMap;

/**
 * @author cl
 */
public class ParameterStorage {

    private static ParameterStorage instance;

    private HashMap<String, Parameter> cache;

    private static ParameterStorage getInstance() {
        if (instance == null) {
            synchronized (ParameterStorage.class) {
                instance = new ParameterStorage();
            }
        }
        return instance;
    }

    private ParameterStorage() {
        cache = new HashMap<>();
    }

    public static void add(String identifier, Parameter parameter) {
        getInstance().cache.put(identifier, parameter);
    }

    public static Parameter get(String identifier) {
        return getInstance().cache.get(identifier);
    }

    public static HashMap<String, Parameter> getAll() {
        return getInstance().cache;
    }

}
