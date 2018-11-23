package bio.singa.simulation.model.agents.pointlike;

import java.util.*;

/**
 * @author cl
 */
public class VesicleStateRegistry {

    public static class VesicleState {

        private String identifier;
        private String description;

        public VesicleState(String identifier, String description) {
            this.identifier = identifier;
            this.description = description;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VesicleState that = (VesicleState) o;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toString() {
            return "State: " + identifier;
        }
    }

    private static VesicleStateRegistry instance = getInstance();

    public static final VesicleState ACTIN_PROPELLED = new VesicleState("ACTIN_PROPELLED", "propelled by actin depolymerization");
    public static final VesicleState MICROTUBULE_ATTACHED = new VesicleState("MICROTUBULE_ATTACHED", "attached to micotubule with motor protein");
    public static final VesicleState ACTIN_ATTACHED = new VesicleState("ACTIN_ATTACHED", "attached to actin with motor protein");
    public static final VesicleState MEMBRANE_ATTACHED = new VesicleState("MEMBRANE_ATTACHED", "attached to membrane");
    public static final VesicleState UNATTACHED = new VesicleState("UNATTACHED", "unattached from any cellular component");

    private Set<VesicleState> states;

    private static VesicleStateRegistry getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    private VesicleStateRegistry() {
        states = new HashSet<>();
        states.add(ACTIN_PROPELLED);
        states.add(MICROTUBULE_ATTACHED);
        states.add(ACTIN_ATTACHED);
        states.add(MEMBRANE_ATTACHED);
        states.add(UNATTACHED);
    }

    public static void reinitialize() {
        synchronized (VesicleStateRegistry.class) {
            instance = new VesicleStateRegistry();
        }
    }

    public static Set<VesicleState> getStates() {
        return getInstance().states;
    }

    public static void addState(VesicleState vesicleState) {
        getInstance().states.add(vesicleState);
    }

    public static Optional<VesicleState> getState(String stateIdentifier) {
        return getInstance().states.stream()
                .filter(state -> state.identifier.equals(stateIdentifier))
                .findAny();
    }

}
