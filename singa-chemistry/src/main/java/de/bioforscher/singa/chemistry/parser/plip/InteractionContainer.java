package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class InteractionContainer {

    int duplicateCounter;

    private static final Logger logger = LoggerFactory.getLogger(InteractionContainer.class);

    private ArrayList<Interaction> interactions;

    public InteractionContainer() {
        this.interactions = new ArrayList<>();
    }

    public List<Interaction> getInteractionsBetween(LeafIdentifier first, LeafIdentifier second) {
        return interactions.stream()
                .filter(interaction -> interactionPairEquals(interaction.getSource(), interaction.getTarget(), first, second))
                .collect(Collectors.toList());
    }

    public void addInteraction(Interaction interaction) {
        logger.debug("Handling interaction between: {} and {}", interaction.getSource(), interaction.getTarget());
        // get interactions that are already present between the leaves
        List<Interaction> presentInteractions = getInteractionsBetween(interaction.getSource(), interaction.getTarget());
        if (presentInteractions.size() > 0) {
            // there are already interactions between those leaves
            logger.trace("There are already interactions annotated between those leaves.");
            boolean allAreDifferent = true;
            boolean symmetricEntriesFound = false;
            boolean uncertainInteraction = false;
            for (Interaction presentInteraction : presentInteractions) {
                // trivial case: both are the same interaction type
                if (presentInteraction.getClass().equals(interaction.getClass())) {
                    allAreDifferent = false;
                    logger.trace("There is already an interaction of the same kind.");
                    if (interaction instanceof HydrogenBond) {
                        final HydrogenBond iPresent = (HydrogenBond) presentInteraction;
                        final HydrogenBond iNew = (HydrogenBond) interaction;
                        // hydrogen bond has identical donor and acceptor
                        if ((iPresent.getAcceptor() == iNew.getAcceptor() && (iPresent.getDonor() == iNew.getDonor()))) {
                            logger.trace("The hydrogen bond (id {}) is the symmetric version of the already added hydrogen bond {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                            symmetricEntriesFound = true;
                            break;
                        }
                        if ((iPresent.getAcceptor() == iNew.getDonor() && (iPresent.getDonor() == iNew.getAcceptor()))) {
                            logger.warn("The hydrogen bond (id {}) has swapped donor and acceptor with hydrogen bond {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.warn("Present : {}", presentInteraction);
                            uncertainInteraction = true;
                            symmetricEntriesFound = true;
                            break;
                        } else {
                            logger.trace("The hydrophobic interaction (id {}) is not the symmetric version of.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                        }
                    } else if (interaction instanceof HydrophobicInteraction) {
                        final HydrophobicInteraction iPresent = (HydrophobicInteraction) presentInteraction;
                        final HydrophobicInteraction iNew = (HydrophobicInteraction) interaction;
                        if ((iPresent.getAtom1() == iNew.getAtom1() && (iPresent.getAtom2() == iNew.getAtom2()))) {
                            logger.trace("The hydrophobic interaction (id {}) is the symmetric version of the already added hydrophobic interaction {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                            symmetricEntriesFound = true;
                            break;
                        } else {
                            logger.trace("The the hydrophobic interaction (id {}) is not the symmetric version of the hydrophobic interaction (id {}).", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                        }
                    } else if (interaction instanceof WaterBridge) {
                        final WaterBridge iPresent = (WaterBridge) presentInteraction;
                        final WaterBridge iNew = (WaterBridge) interaction;
                        if (((iPresent.getAcceptor() == iNew.getAcceptor() && (iPresent.getDonor() == iNew.getDonor()))) ||
                                ((iPresent.getDonor() == iNew.getAcceptor() && (iPresent.getAcceptor() == iNew.getDonor())))) {
                            logger.trace("The water bridge (id {}) is the symmetric version of the already added water bridge {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                            symmetricEntriesFound = true;
                            break;
                        } else {
                            logger.trace("The water bridge (id {}) is not the symmetric version of the water bridge (id {}).", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                        }
                    } else if (interaction instanceof SaltBridge) {
                        final SaltBridge iPresent = (SaltBridge) presentInteraction;
                        final SaltBridge iNew = (SaltBridge) interaction;
                        if (atomsOverlap(iPresent.getAtoms2(), iNew.getAtoms2())) {
                            logger.warn("The salt bridge (id {}) has overlapping atoms with salt bridge {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.warn("Present : {}", presentInteraction);
                            uncertainInteraction = true;
                            symmetricEntriesFound = true;
                            break;
                        } else {
                            if (!iPresent.getAtoms1().isEmpty()) {
                                logger.warn("Tried to merge salt bridge (id {}) with salt bridge (id {}) but atoms are already present");
                                uncertainInteraction = true;
                            } else {
                                logger.trace("Merging salt bridge (id {}) with salt bridge (id {}).", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                                iPresent.mergeWith(iNew);
                                logger.trace("Updated : {}", presentInteraction);
                            }
                            symmetricEntriesFound = true;
                            break;
                        }
                    } else if (interaction instanceof PiStacking) {
                        final PiStacking iPresent = (PiStacking) presentInteraction;
                        final PiStacking iNew = (PiStacking) interaction;
                        if (iPresent.getAtoms2().equals(iNew.getAtoms2())) {
                            logger.trace("The pi stack (id {}) is the identical version of the already added pi stack {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                            symmetricEntriesFound = true;
                            break;
                        } else {
                            logger.trace("The pi stack (id {}) is not the symmetric version of the pi stack (id {}).", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                        }
                    } else if (interaction instanceof PiCationInteraction) {
                        final PiCationInteraction iPresent = (PiCationInteraction) presentInteraction;
                        final PiCationInteraction iNew = (PiCationInteraction) interaction;
                        if (iPresent.getAtoms2().equals(iNew.getAtoms2())) {
                            logger.trace("The pi-cation interaction (id {}) is the identical version of the already added pi-cation interaction  {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                            symmetricEntriesFound = true;
                            break;
                        } else {
                            logger.trace("The pi-cation interaction  (id {}) is not the symmetric version of the pi-cation interaction  (id {}).", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.trace("Present : {}", presentInteraction);
                        }
                    } else {
                        // TODO it is possible that some more interactions can be merged
                        // TODO if currently all pi-cation and pi stacks are considered non symmetrical if they are not identical
                        logger.warn("Interaction of {} (id={}) is a possible duplicate of {} with (id={}). Don't know what to do yet. ", interaction.getClass().getSimpleName(), interaction.getPlipIdentifier(), presentInteraction.getClass().getSimpleName(), presentInteraction.getPlipIdentifier());
                        uncertainInteraction = true;
                        logger.warn("Present : {}", presentInteraction);
                    }
                }
            }

            if (!symmetricEntriesFound && !uncertainInteraction) {
                logger.trace("Adding  : {}", interaction);
                this.interactions.add(interaction);
                return;
            } else {
                if (uncertainInteraction) {
                    logger.warn("Skipping: {}", interaction);
                } else {
                    logger.trace("Skipping: {}", interaction);
                }
            }

            if (allAreDifferent) {
                //  trivial case: all are different kinds
                logger.trace("This kind of interaction is not present between the leaves.");
                logger.trace("Adding  : {}", interaction);
                this.interactions.add(interaction);
            }

        } else {
            // no interaction between the leaves is present
            logger.trace("There are no interactions annotated between those leaves.");
            logger.trace("Adding  : {}", interaction);
            this.interactions.add(interaction);
        }


    }

    private static boolean interactionPairEquals(LeafIdentifier source1, LeafIdentifier target1, LeafIdentifier source2, LeafIdentifier target2) {
        if (source1.equals(source2)) {
            return target1.equals(target2);
        }
        return source1.equals(target2) && target1.equals(source2);
    }

    private static boolean atomsOverlap(List<Integer> firstList, List<Integer> secondList) {
        for (int first : firstList) {
            for (int second : secondList) {
                if (first == second) {
                    return true;
                }
            }
        }
        return false;
    }

    public void convertToPseudoAtomsFor(Structure structure) {
        // for now add some pseudo atoms
//        for (Interaction interaction : this.interactions.values()) {
//            Vector3D centroid = interaction.getLigandCoordiante().add(interaction.getProteinCoordinate()).multiply(0.5);
//            structure.addAtom(interaction.getSource().getChainIdentifier(), ElementProvider.ARSENIC, interaction.getClass().getSimpleName(), centroid);
//        }
    }

}
