package de.bioforscher.singa.chemistry.parser.plip;

/**
 * @author cl
 */
public enum InteractionType {

    HALOGEN_BOND(HalogenBond.class, "hal"),
    HYDROGEN_BOND(HydrogenBond.class, "hyb"),
    HYDROPHOBIC_INTERACTION(HydrophobicInteraction.class, "hyp"),
    METAL_COMPLEX(MetalComplex.class, "mec"),
    PI_CATION_INTERACTION(PiCationInteraction.class, "pic"),
    PI_STACKING(PiStacking.class, "pis"),
    SALT_BRIDGE(SaltBridge.class, "sab"),
    WATER_BRIDGE(WaterBridge.class, "wab");

    private final Class<? extends Interaction> interactionClass;
    private final String threeLetterCode;

    InteractionType(Class<? extends Interaction> interactionClass, String threeLetterCode) {
        this.interactionClass = interactionClass;
        this.threeLetterCode = threeLetterCode;
    }

    public Class<? extends Interaction> getInteractionClass() {
        return interactionClass;
    }

    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    public static String getThreeLetterCode(Class<? extends Interaction> interactionClass) {
        for (InteractionType interactionType: values()) {
            if (interactionType.getInteractionClass().equals(interactionClass)) {
                return interactionType.getThreeLetterCode();
            }
        }
        return "";
    }
}
