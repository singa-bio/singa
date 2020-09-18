package bio.singa.structure.parser.plip;

import bio.singa.features.identifiers.LeafIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * @author cl
 */
public class PlipContentHandler implements ContentHandler {

    private static final Logger logger = LoggerFactory.getLogger(InteractionContainer.class);

    private final InteractionContainer interactions;

    private String currentTag;

    private String currentPdbIdentifier;
    private String firstLeafSerial;
    private String secondLeafSerial;
    private String firstLeafChain;
    private String secondLeafChain;

    private boolean ligcoo;
    private double c1x;
    private double c1y;
    private double c1z;
    private double c2x;
    private double c2y;
    private double c2z;

    private boolean noResidueInteraction;

    private InteractionType interactionType;
    private Interaction currentInteraction;

    public PlipContentHandler(String pdbIdentifier) {
        interactions = new InteractionContainer();
        currentPdbIdentifier = pdbIdentifier;
    }

    public String getCurrentPdbIdentifier() {
        return currentPdbIdentifier;
    }

    public void setCurrentPdbIdentifier(String currentPdbIdentifier) {
        this.currentPdbIdentifier = currentPdbIdentifier;
    }

    public InteractionContainer getInteractionContainer() {
        return interactions;
    }

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() {

    }

    @Override
    public void endDocument() {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {

    }

    @Override
    public void endPrefixMapping(String prefix) {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        currentTag = qName;
        switch (qName) {
            case "halogen_bond":
                currentInteraction = new HalogenBond(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.HALOGEN_BOND;
                break;
            case "hydrophobic_interaction":
                currentInteraction = new HydrophobicInteraction(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.HYDROPHOBIC_INTERACTION;
                break;
            case "hydrogen_bond":
                currentInteraction = new HydrogenBond(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.HYDROGEN_BOND;
                break;
            case "water_bridge":
                currentInteraction = new WaterBridge(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.WATER_BRIDGE;
                break;
            case "salt_bridge":
                currentInteraction = new SaltBridge(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.SALT_BRIDGE;
                break;
            case "pi_stack":
                currentInteraction = new PiStacking(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.PI_STACKING;
                break;
            case "pi_cation_interaction":
                currentInteraction = new PiCation(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.PI_CATION_INTERACTION;
                break;
            case "metal_complex":
                currentInteraction = new MetalComplex(Integer.parseInt(atts.getValue("id")));
                interactionType = InteractionType.METAL_COMPLEX;
                break;
            case "ligcoo":
                ligcoo = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        currentTag = "";
        switch (qName) {
            case "halogen_bond":
            case "hydrophobic_interaction":
            case "hydrogen_bond":
            case "water_bridge":
            case "salt_bridge":
            case "pi_stack":
            case "pi_cation_interaction":
            case "metal_complex":
                addInteraction();
                break;
            case "ligcoo":
                ligcoo = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
//        String testString = new String(ch, start, length);
//        if (currentTag.isEmpty() || testString.isBlank()) {
//            return;
//        }
//        System.out.println(currentTag + ": ("+length+") " + testString);
        switch (currentTag) {
            case "resnr":
                firstLeafSerial = new String(ch, start, length);
                break;
            case "resnr_lig":
                secondLeafSerial = new String(ch, start, length);
                break;
            case "reschain":
                firstLeafChain = new String(ch, start, length);
                break;
            case "reschain_lig":
                secondLeafChain = new String(ch, start, length);
                break;
            case "x":
                if (ligcoo) {
                    c1x = asDouble(ch, start, length);
                } else {
                    c2x = asDouble(ch, start, length);
                }
                break;
            case "y":
                if (ligcoo) {
                    c1y = asDouble(ch, start, length);
                } else {
                    c2y = asDouble(ch, start, length);
                }
                break;
            case "z":
                if (ligcoo) {
                    c1z = asDouble(ch, start, length);
                } else {
                    c2z = asDouble(ch, start, length);
                }
                break;
            case "dist":
                switch (interactionType) {
                    case HALOGEN_BOND:
                        as(HalogenBond.class).setDistance(asDouble(ch, start, length));
                        break;
                    case METAL_COMPLEX:
                        as(MetalComplex.class).setDistance(asDouble(ch, start, length));
                        break;
                    case HYDROPHOBIC_INTERACTION:
                        as(HydrophobicInteraction.class).setDistance(asDouble(ch, start, length));
                        break;
                    case PI_CATION_INTERACTION:
                        as(PiCation.class).setDistance(asDouble(ch, start, length));
                        break;
                    case PI_STACKING:
                        as(PiStacking.class).setDistance(asDouble(ch, start, length));
                        break;
                    case SALT_BRIDGE:
                        as(SaltBridge.class).setDistance(asDouble(ch, start, length));
                        break;
                }
                break;
            case "dist_a-w":
                as(WaterBridge.class).setDistanceAW(asDouble(ch, start, length));
                break;
            case "dist_d-w":
                as(WaterBridge.class).setDistanceDW(asDouble(ch, start, length));
                break;
            case "don_angle":
                switch (interactionType) {
                    case WATER_BRIDGE:
                        as(WaterBridge.class).setDonorAngle(asDouble(ch, start, length));
                        break;
                    case HYDROGEN_BOND:
                        as(HydrogenBond.class).setAngle(asDouble(ch, start, length));
                        break;
                    case HALOGEN_BOND:
                        as(HalogenBond.class).setDonorAngle(asDouble(ch, start, length));
                        break;
                }
                break;
            case "water_angle":
                as(WaterBridge.class).setWaterAngle(asDouble(ch, start, length));
                break;
            case "donor_idx":
                as(WaterBridge.class).setDonor(asInteger(ch, start, length));
                break;
            case "acceptor_idx":
                as(WaterBridge.class).setAcceptor(asInteger(ch, start, length));
                break;
            case "sidechain":
                if (interactionType == InteractionType.HYDROGEN_BOND) {
                    as(HydrogenBond.class).setSidechain(asBoolean(ch, start, length));
                }
                break;
            case "protisdon":
                switch (interactionType) {
                    case HYDROGEN_BOND:
                        as(HydrogenBond.class).setProtIsDon(asBoolean(ch, start, length));
                        break;
                    case WATER_BRIDGE:
                        as(WaterBridge.class).setProtIsDon(asBoolean(ch, start, length));
                        break;
                }
                break;
            case "dist_h-a":
                as(HydrogenBond.class).setDistanceHA(asDouble(ch, start, length));
                break;
            case "dist_d-a":
                as(HydrogenBond.class).setDistanceDA(asDouble(ch, start, length));
                break;
            case "donoridx":
                as(HydrogenBond.class).setDonor(asInteger(ch, start, length));
                break;
            case "acceptoridx":
                as(HydrogenBond.class).setAcceptor(asInteger(ch, start, length));
                break;
            case "metal_idx":
                as(MetalComplex.class).setAtom1(asInteger(ch, start, length));
                break;
            case "target_idx":
                as(MetalComplex.class).setAtom2(asInteger(ch, start, length));
                break;
            case "metal_type":
                as(MetalComplex.class).setMetalType(new String(ch, start, length));
                break;
            case "location":
                as(MetalComplex.class).setLocation(new String(ch, start, length));
                break;
            case "geometry":
                as(MetalComplex.class).setGeometry(new String(ch, start, length));
                break;
            case "complexnum":
                as(MetalComplex.class).setComplexnum(asInteger(ch, start, length));
                break;
            case "acc_angle":
                as(HalogenBond.class).setAcceptorAngle(asDouble(ch, start, length));
                break;
            case "don_idx":
                as(HalogenBond.class).setDonor(asInteger(ch, start, length));
                break;
            case "acc_idx":
                as(HalogenBond.class).setAcceptor(asInteger(ch, start, length));
                break;
            case "idx":
                switch (interactionType) {
                    case PI_CATION_INTERACTION:
                        as(PiCation.class).getAtoms2().add(asInteger(ch, start, length));
                        break;
                    case PI_STACKING:
                        as(PiStacking.class).getAtoms2().add(asInteger(ch, start, length));
                        break;
                    case SALT_BRIDGE:
                        as(SaltBridge.class).getAtoms2().add(asInteger(ch, start, length));
                        break;
                }
                break;
            case "protispos":
                as(SaltBridge.class).setProtIsPos(asBoolean(ch, start, length));
                break;
            case "angle":
                as(PiStacking.class).setAngle(asDouble(ch, start, length));
                break;
            case "offset":
                switch (interactionType) {
                    case PI_CATION_INTERACTION:
                        as(PiCation.class).setOffset(asDouble(ch, start, length));
                        break;
                    case PI_STACKING:
                        as(PiStacking.class).setOffset(asDouble(ch, start, length));
                        break;
                }
                break;
            case "type":
                as(PiStacking.class).setType(new String(ch, start, length));
                break;
            case "protcharged":
                as(PiCation.class).setProtcharged(asBoolean(ch, start, length));
                break;
            case "lig_group":
                switch (interactionType) {
                    case PI_CATION_INTERACTION:
                        as(PiCation.class).setLigandGroup(new String(ch, start, length));
                        break;
                    case SALT_BRIDGE:
                        as(SaltBridge.class).setLigandGroup(new String(ch, start, length));
                        break;
                }
                break;
            case "protcarbonidx":
                as(HydrophobicInteraction.class).setAtom1(asInteger(ch, start, length));
                break;
            case "ligcarbonidx":
                as(HydrophobicInteraction.class).setAtom2(asInteger(ch, start, length));
                break;
        }

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {

    }

    @Override
    public void processingInstruction(String target, String data) {

    }

    @Override
    public void skippedEntity(String name) {

    }

    private <InteractionClass extends Interaction> InteractionClass as(Class<InteractionClass> interactionType) {
        return interactionType.cast(currentInteraction);
    }

    private void addInteraction() {
        // skip all interactions that are not between standard amino acids
        // TODO This may be failing for amino acid ligands or modified amino acids
        if (noResidueInteraction) {
            noResidueInteraction = false;
            return;
        }
        // TODO sometimes there are impossible leaf indices
        if (firstLeafSerial.length() > 9 || firstLeafSerial.equals("NA")) {
            logger.debug("The leaf serial {} is not valid. Skipping this interaction.", firstLeafSerial);
            return;
        }
        if (secondLeafSerial.length() > 9 || secondLeafSerial.equals("NA")) {
            logger.debug("The leaf serial {} is not valid. Skipping this interaction.", firstLeafSerial);
            return;
        }
        // generate identifiers
        final LeafIdentifier source = new LeafIdentifier(currentPdbIdentifier, 1, firstLeafChain, Integer.parseInt(firstLeafSerial));
        final LeafIdentifier target = new LeafIdentifier(currentPdbIdentifier, 1, secondLeafChain, Integer.parseInt(secondLeafSerial));
        // FIXME for metal complexes this seems to be not valid to take the first occurring entry as source: at atom level source is always the ion
        currentInteraction.setSource(source);
        currentInteraction.setTarget(target);
        currentInteraction.setLigandCoordinate(new double[]{c1x, c1y, c1z});
        currentInteraction.setProteinCoordinate(new double[]{c2x, c2y, c2z});
        // add the container to interactions
        interactions.addInteraction(currentInteraction);

    }

    private double asDouble(char[] ch, int start, int length) {
        return Double.parseDouble(new String(ch, start, length));
    }

    private int asInteger(char[] ch, int start, int length) {
        return Integer.parseInt(new String(ch, start, length));
    }

    private boolean asBoolean(char[] ch, int start, int length) {
        return Boolean.parseBoolean(new String(ch, start, length));
    }


}
