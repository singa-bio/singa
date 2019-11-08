package bio.singa.simulation.model.agents.pointlike;

import java.util.*;

/**
 * @author cl
 */
public class VesicleStateRegistry {

    private static VesicleStateRegistry instance = getInstance();

    public static final String ACTIN_PROPELLED = "ACTIN_PROPELLED";
    public static final String ACTIN_ATTACHED = "ACTIN_ATTACHED";
    public static final String TETHERED = "TETHERED";
    public static final String MICROTUBULE_ATTACHED = "MICROTUBULE_ATTACHED";
    public static final String MEMBRANE_TETHERED = "MEMBRANE_TETHERED";
    public static final String UNATTACHED = "UNATTACHED";
    public static final String IN_PERINUCLEAR_STORAGE = "IN_PERINUCLEAR_STORAGE";
    public static final String TAGGED_FOR_EXOCYTOSIS = "TAGGED_FOR_EXOCYTOSIS";

    private Set<String> states;

    private static VesicleStateRegistry getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    private VesicleStateRegistry() {
        states = new HashSet<>();
        states.add(ACTIN_PROPELLED);
        states.add(ACTIN_ATTACHED);
        states.add(TETHERED);
        states.add(MICROTUBULE_ATTACHED);
        states.add(MEMBRANE_TETHERED);
        states.add(UNATTACHED);
        states.add(IN_PERINUCLEAR_STORAGE);
    }

    public static void reinitialize() {
        synchronized (VesicleStateRegistry.class) {
            instance = new VesicleStateRegistry();
        }
    }

    public static Set<String> getStates() {
        return getInstance().states;
    }

    public static void addState(String vesicleState) {
        getInstance().states.add(vesicleState);
    }

}
