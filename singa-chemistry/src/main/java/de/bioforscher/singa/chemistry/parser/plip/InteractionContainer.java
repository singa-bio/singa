package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The interaction container takes all interactions in a PLIP xml-file that was generated using the inter-chain
 * interaction annotation feature.
 *
 * @author cl
 */
public class InteractionContainer {

    private static final Logger logger = LoggerFactory.getLogger(InteractionContainer.class);

    /**
     * The interactions
     */
    private List<Interaction> interactions;

    /**
     * Creates a new empty interaction container.
     */
    public InteractionContainer() {
        this.interactions = new ArrayList<>();
    }

    /**
     * Returns all Interactions between two leaves (i.e. two residues.). If no interactions are present an empty list is
     * returned. The order in which the {@link LeafIdentifier}s are given is irrelevant.
     *
     * @param first The first leaf.
     * @param second The second leaf.
     * @return A list of interactions between both leaves.
     */
    public List<Interaction> getInteractionsBetween(LeafIdentifier first, LeafIdentifier second) {
        return interactions.stream()
                .filter(interaction -> interactionPairEquals(interaction.getSource(), interaction.getTarget(), first, second))
                .collect(Collectors.toList());
    }

    /**
     * Returns true if any interaction between the two leaves is annotated in this container.
     *
     * @param first The first leaf.
     * @param second The second leaf.
     * @return True, if any interaction between the two leaves is annotated.
     */
    public boolean hasInteractions(LeafIdentifier first, LeafIdentifier second) {
        return interactions.stream()
                .anyMatch(interaction -> interactionPairEquals(interaction.getSource(), interaction.getTarget(), first, second));
    }

    /**
     * Adds the interactions in this container to a structure as pseudo atoms. Every interactions is assigned a new
     * {@link LeafSubstructure} that has a three letter code according to the type of interaction. The pseudo atom is
     * positioned a the center of the annotated interaction centers.
     *
     * @param structure The structure to assign the interaction to.
     */
    public void mapToPseudoAtoms(Structure structure) {
        for (Interaction interaction : this.interactions) {
            Vector3D centroid = new Vector3D(interaction.getLigandCoordiante()).add(new Vector3D(interaction.getProteinCoordinate())).multiply(0.5);
            structure.addPseudoAtom(interaction.getSource().getChainIdentifier(), InteractionType.getThreeLetterCode(interaction.getClass()), centroid);
        }
    }

    /**
     * Retuens all interactions from this container.
     *
     * @return All interactions.
     */
    public List<Interaction> getInteractions() {
        return interactions;
    }

    /**
     * Adds an interaction to this container. Symmetrical interactions are not added twice. If possible overlapping
     * interactions are merged.
     *
     * @param interaction The interaction to add.
     */
    public void addInteraction(Interaction interaction) {
        // some day this can be implemented better...
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

    /**
     * Tests if two pairs of {@link LeafIdentifier}s contain the same entries. This method considers source and target
     * swappable.
     *
     * @param firstSource The source of the first leaf.
     * @param firstTarget The target of the first leaf.
     * @param secondSource The source of the second leaf.
     * @param secondTarget The target of the second leaf.
     * @return True, if first source equals second source and first target equals second target or first source equals
     * second target and first target equals second source.
     */
    private static boolean interactionPairEquals(LeafIdentifier firstSource, LeafIdentifier firstTarget, LeafIdentifier secondSource, LeafIdentifier secondTarget) {
        if (firstSource.equals(secondSource)) {
            return firstTarget.equals(secondTarget);
        }
        return firstSource.equals(secondTarget) && firstTarget.equals(secondSource);
    }

    /**
     * Tests if two sets of integers (atom identifiers in this context) overlap.
     *
     * @param firstList The first list of integers.
     * @param secondList The second lst of integers.
     * @return True, if at least one integer is contained in both sets.
     */
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

}
