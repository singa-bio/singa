package de.bioforscher.singa.structure.parser.plip;

/**
 * @author cl
 */
public enum InteractionType {

    HALOGEN_BOND(HalogenBond.class, "HAL"),
    HYDROGEN_BOND(HydrogenBond.class, "HYB"),
    HYDROPHOBIC_INTERACTION(HydrophobicInteraction.class, "HYP"),
    METAL_COMPLEX(MetalComplex.class, "MEC"),
    PI_CATION_INTERACTION(PiCation.class, "PIC"),
    PI_STACKING(PiStacking.class, "PIS"),
    SALT_BRIDGE(SaltBridge.class, "SAB"),
    WATER_BRIDGE(WaterBridge.class, "WAB");

    private final Class<? extends Interaction> interactionClass;
    private final String threeLetterCode;

    InteractionType(Class<? extends Interaction> interactionClass, String threeLetterCode) {
        this.interactionClass = interactionClass;
        this.threeLetterCode = threeLetterCode;
    }

    public static String getThreeLetterCode(Class<? extends Interaction> interactionClass) {
        for (InteractionType interactionType : values()) {
            if (interactionType.getInteractionClass().equals(interactionClass)) {
                return interactionType.getThreeLetterCode();
            }
        }
        return "";
    }

    public Class<? extends Interaction> getInteractionClass() {
        return this.interactionClass;
    }

    public String getThreeLetterCode() {
        return this.threeLetterCode;
    }
}
