package de.bioforscher.singa.chemistry.parser.plip;

/**
 * @author cl
 */
public enum InteractionType {

    HALOGEN_BOND(HalogenBond.class),
    HYDROGEN_BOND(HydrogenBond.class),
    HYDROPHOBIC_INTERACTION(HydrophobicInteraction.class),
    METAL_COMPLEX(MetalComplex.class),
    PI_CATION_INTERACTION(PiCationInteraction.class),
    PI_STACKING(PiStacking.class),
    SALT_BRIDGE(SaltBridge.class),
    WATER_BRIDGE(WaterBridge.class);

    private final Class<? extends Interaction> interactionClass;

    InteractionType(Class<? extends Interaction> interactionClass) {
        this.interactionClass = interactionClass;
    }

    public Class<? extends Interaction> getInteractionClass() {
        return interactionClass;
    }



}
