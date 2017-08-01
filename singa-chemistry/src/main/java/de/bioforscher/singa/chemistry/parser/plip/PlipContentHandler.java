package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import static de.bioforscher.singa.chemistry.parser.plip.InteractionType.*;

/**
 * @author cl
 */
public class PlipContentHandler implements ContentHandler {

    private static final Logger logger = LoggerFactory.getLogger(InteractionContainer.class);

    private InteractionContainer interactions;

    private String currentTag;

    private String currentPdbIdentifier = "0000";
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
        this.interactions = new InteractionContainer();
        this.currentPdbIdentifier = pdbIdentifier;
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
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        this.currentTag = qName;
        switch (qName) {
            case "halogen_bond":
                this.currentInteraction = new HalogenBond(Integer.valueOf(atts.getValue("id")));
                this.interactionType = HALOGEN_BOND;
                break;
            case "hydrophobic_interaction":
                this.currentInteraction = new HydrophobicInteraction(Integer.valueOf(atts.getValue("id")));
                this.interactionType = HYDROPHOBIC_INTERACTION;
                break;
            case "hydrogen_bond":
                this.currentInteraction = new HydrogenBond(Integer.valueOf(atts.getValue("id")));
                this.interactionType = HYDROGEN_BOND;
                break;
            case "water_bridge":
                this.currentInteraction = new WaterBridge(Integer.valueOf(atts.getValue("id")));
                this.interactionType = WATER_BRIDGE;
                break;
            case "salt_bridge":
                this.currentInteraction = new SaltBridge(Integer.valueOf(atts.getValue("id")));
                this.interactionType = SALT_BRIDGE;
                break;
            case "pi_stack":
                this.currentInteraction = new PiStacking(Integer.valueOf(atts.getValue("id")));
                this.interactionType = PI_STACKING;
                break;
            case "pi_cation_interaction":
                this.currentInteraction = new PiCationInteraction(Integer.valueOf(atts.getValue("id")));
                this.interactionType = PI_CATION_INTERACTION;
                break;
            case "metal_complex":
                this.currentInteraction = new MetalComplex(Integer.valueOf(atts.getValue("id")));
                this.interactionType = METAL_COMPLEX;
                break;
            case "ligcoo":
                this.ligcoo = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.currentTag = "";
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
                this.ligcoo = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        switch (this.currentTag) {
            case "resnr":
                this.firstLeafSerial = new String(ch, start, length);
                break;
            case "resnr_lig":
                this.secondLeafSerial = new String(ch, start, length);
                break;
            case "reschain":
                this.firstLeafChain = new String(ch, start, length);
                break;
            case "reschain_lig":
                this.secondLeafChain = new String(ch, start, length);
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
                switch (this.interactionType) {
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
                        as(PiCationInteraction.class).setDistance(asDouble(ch, start, length));
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
                switch (this.interactionType) {
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
                switch (this.interactionType) {
                    case HYDROGEN_BOND:
                        as(HydrogenBond.class).setSidechain(asBoolean(ch, start, length));
                        break;
                    default:
                        break;
                }
                break;
            case "protisdon":
                switch (this.interactionType) {
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
                switch (this.interactionType) {
                    case PI_CATION_INTERACTION:
                        as(PiCationInteraction.class).getAtoms2().add(asInteger(ch, start, length));
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
                switch (this.interactionType) {
                    case PI_CATION_INTERACTION:
                        as(PiCationInteraction.class).setOffset(asDouble(ch, start, length));
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
                as(PiCationInteraction.class).setProtcharged(asBoolean(ch, start, length));
                break;
            case "lig_group":
                switch (this.interactionType) {
                    case PI_CATION_INTERACTION:
                        as(PiCationInteraction.class).setLigandGroup(new String(ch, start, length));
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
            case "restype_lig":
                String restype = new String(ch, start, length);
                if (!AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(restype).isPresent()) {
                    this.noResidueInteraction = true;
                }
        }

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }

    private <InteractionClass extends Interaction> InteractionClass as(Class<InteractionClass> interactionType) {
        return interactionType.cast(currentInteraction);
    }

    private void addInteraction() {
        // skip all interactions that are not between standard amino acids
        // TODO This may be going down for amino acid ligands or modified amino acids
        if (noResidueInteraction) {
            noResidueInteraction = false;
            return;
        }
        // TODO sometimes there are impossible leaf indices
        if (this.firstLeafSerial.length() > 9 || this.firstLeafSerial.equals("NA")) {
            logger.warn("The leaf serial {} is not valid. Skipping this interaction.", this.firstLeafSerial);
            return;
        }
        if (this.secondLeafSerial.length() > 9 || this.secondLeafSerial.equals("NA")) {
            logger.warn("The leaf serial {} is not valid. Skipping this interaction.", this.firstLeafSerial);
            return;
        }
        // generate identifiers
        final LeafIdentifier source = new LeafIdentifier(this.currentPdbIdentifier, 0, this.firstLeafChain, Integer.valueOf(this.firstLeafSerial));
        final LeafIdentifier target = new LeafIdentifier(this.currentPdbIdentifier, 0, this.secondLeafChain, Integer.valueOf(this.secondLeafSerial));
        this.currentInteraction.setSource(source);
        this.currentInteraction.setTarget(target);
        this.currentInteraction.setLigandCoordiante(new Vector3D(c1x, c1y, c1z));
        this.currentInteraction.setProteinCoordinate(new Vector3D(c2x, c2y, c2z));
        // add the container to interactions
        this.interactions.addInteraction(this.currentInteraction);

    }

    private double asDouble(char[] ch, int start, int length) {
        return Double.valueOf(new String(ch, start, length));
    }

    private int asInteger(char[] ch, int start, int length) {
        return Integer.valueOf(new String(ch, start, length));
    }

    private boolean asBoolean(char[] ch, int start, int length) {
        return Boolean.valueOf(new String(ch, start, length));
    }


}
