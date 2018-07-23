package bio.singa.structure.parser.plip;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.families.LigandFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.identifiers.UniqueAtomIdentifer;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakChain;
import bio.singa.structure.model.oak.OakStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final List<Interaction> interactions;

    private final List<Interaction> ligandInteractions;

    /**
     * Creates a new empty interaction container.
     */
    public InteractionContainer() {
        interactions = new ArrayList<>();
        ligandInteractions = new ArrayList<>();
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

    /**
     * Returns all Interactions between two leaves (i.e. two residues.). If no interactions are present an empty list is
     * returned. The order in which the {@link LeafIdentifier}s are given is irrelevant.
     *
     * @param first The first leaf.
     * @param second The second leaf.
     * @return A list of interactions between both leaves.
     */
    public List<Interaction> getInteractionsBetween(LeafIdentifier first, LeafIdentifier second) {
        return Stream.concat(interactions.stream(), ligandInteractions.stream())
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
        return Stream.concat(interactions.stream(), ligandInteractions.stream())
                .anyMatch(interaction -> interactionPairEquals(interaction.getSource(), interaction.getTarget(), first, second));
    }

    /**
     * Adds the interactions in this container to a structure as pseudo atoms. Every interactions is assigned a new
     * {@link LeafSubstructure} that has a three letter code according to the type of interaction. The pseudo atom is
     * positioned a the center of the annotated interaction centers.
     *
     * @param structure The structure to assign the interaction to.
     */
    public void mapToPseudoAtoms(OakStructure structure) {
        Stream.concat(interactions.stream(), ligandInteractions.stream())
                .forEach(interaction -> {
                    Vector3D centroid = new Vector3D(interaction.getLigandCoordinate()).add(new Vector3D(interaction.getProteinCoordinate())).multiply(0.5);
                    structure.addAtom(interaction.getSource().getChainIdentifier(), InteractionType.getThreeLetterCode(interaction.getClass()), centroid);
                });
    }

    /**
     * Returns all interactions from this container.
     *
     * @return All interactions.
     */
    public List<Interaction> getInteractions() {
        return interactions;
    }

    public List<Interaction> getLigandInteractions() {
        return ligandInteractions;
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
                            logger.info("The hydrogen bond (id {}) has swapped donor and acceptor with hydrogen bond {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
                            logger.info("Present : {}", presentInteraction);
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
                        // don't merge salt bridges until it is clear when to do this
//                        final SaltBridge iPresent = (SaltBridge) presentInteraction;
//                        final SaltBridge iNew = (SaltBridge) interaction;
//                        if (atomsOverlap(iPresent.getAtoms2(), iNew.getAtoms2())) {
//                            logger.info("The salt bridge (id {}) has overlapping atoms with salt bridge {}.", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
//                            logger.info("Present : {}", presentInteraction);
//                            uncertainInteraction = true;
//                            symmetricEntriesFound = true;
//                            break;
//                        } else {
//                            if (!iPresent.getAtoms1().isEmpty()) {
//                                logger.info("Tried to merge salt bridge (id {}) with salt bridge (id {}) but atoms are already present");
//                                uncertainInteraction = true;
//                            } else {
//                                logger.trace("Merging salt bridge (id {}) with salt bridge (id {}).", iNew.getPlipIdentifier(), iPresent.getPlipIdentifier());
//                                iPresent.mergeWith(iNew);
//                                logger.trace("Updated : {}", presentInteraction);
//                            }
//                            symmetricEntriesFound = true;
//                            break;
//                        }
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
                    } else if (interaction instanceof PiCation) {
                        final PiCation iPresent = (PiCation) presentInteraction;
                        final PiCation iNew = (PiCation) interaction;
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
                        logger.info("Interaction of {} (id={}) is a possible duplicate of {} with (id={}). Don't know what to do yet. ", interaction.getClass().getSimpleName(), interaction.getPlipIdentifier(), presentInteraction.getClass().getSimpleName(), presentInteraction.getPlipIdentifier());
                        uncertainInteraction = true;
                        logger.info("Present : {}", presentInteraction);
                    }
                }
            }

            if (!symmetricEntriesFound && !uncertainInteraction) {
                logger.trace("Adding  : {}", interaction);
                interactions.add(interaction);
                return;
            } else {
                if (uncertainInteraction) {
                    logger.debug("Skipping uncertain interaction: {}", interaction);
                } else {
                    logger.trace("Skipping: {}", interaction);
                }
            }

            if (allAreDifferent) {
                //  trivial case: all are different kinds
                logger.trace("This kind of interaction is not present between the leaves.");
                logger.trace("Adding  : {}", interaction);
                interactions.add(interaction);
            }

        } else {
            // no interaction between the leaves is present
            logger.trace("There are no interactions annotated between those leaves.");
            logger.trace("Adding  : {}", interaction);
            interactions.add(interaction);
        }

    }

    public void validateWithStructure(OakStructure structure) {

        ListIterator<Interaction> interactionListIterator = interactions.listIterator();

        while (interactionListIterator.hasNext()) {
            Interaction interaction = interactionListIterator.next();
            boolean sourceIsLigand = false;
            boolean targetIsLigand = false;
            // handle insertion codes for source
            LeafIdentifier source = interaction.getSource();
            Optional<LeafSubstructure<?>> optionalSourceLeaf = structure.getLeafSubstructure(source);
            if (!optionalSourceLeaf.isPresent()) {
                // source could not be retrieved
                logger.debug("Bad leaf reference for source {} in {}.", source, interaction);
                fixBrokenSourceIdentifier(interaction, structure);
            } else {
                LeafSubstructure leafSubstructure = optionalSourceLeaf.get();
                // in order to be classified as an ligand you should belong to the ligand family
                // and be in the non-consecutive part of the chain
                sourceIsLigand = determineLigandInteraction(leafSubstructure, structure);
            }

            // handle insertion codes for target
            LeafIdentifier target = interaction.getTarget();
            Optional<LeafSubstructure<?>> optionalTargetLeaf = structure.getLeafSubstructure(target);
            if (!optionalTargetLeaf.isPresent()) {
                // target could not be retrieved
                logger.debug("Bad leaf reference for target {} in {}.", target, interaction);
                // try to retrieve first referenced atom
                fixBrokenTargetIdentifier(interaction, structure);
            } else {
                LeafSubstructure leafSubstructure = optionalTargetLeaf.get();
                // in order to be classified as an ligand you should belong to the ligand family
                // and be in the non-consecutive part of the chain
                targetIsLigand = determineLigandInteraction(leafSubstructure, structure);
            }

            if (targetIsLigand || sourceIsLigand) {
                ligandInteractions.add(interaction);
                interactionListIterator.remove();
            }
        }
    }

    private void fixBrokenSourceIdentifier(Interaction interaction, OakStructure structure) {
        // try to retrieve first referenced atom
        int firstSourceAtom = interaction.getFirstSourceAtom();
        Optional<Map.Entry<UniqueAtomIdentifer, Atom>> sourceEntry = structure.getUniqueAtomEntry(firstSourceAtom);
        if (sourceEntry.isPresent()) {
            // use the atom identifier to remap leaf
            UniqueAtomIdentifer atomIdentifer = sourceEntry.get().getKey();
            LeafIdentifier leafIdentifier = new LeafIdentifier(atomIdentifer.getPdbIdentifier(), atomIdentifer.getModelIdentifier(),
                    atomIdentifer.getChainIdentifier(), atomIdentifer.getLeafSerial(), atomIdentifer.getLeafInsertionCode());
            logger.debug("Fixed to leaf identifier {}.", leafIdentifier);
            interaction.setSource(leafIdentifier);
        } else {
            logger.warn("Unable to fix {}.", interaction);
        }
    }

    private void fixBrokenTargetIdentifier(Interaction interaction, OakStructure structure) {
        // try to retrieve first referenced atom
        Optional<Map.Entry<UniqueAtomIdentifer, Atom>> targetEntry = structure.getUniqueAtomEntry(interaction.getFirstTargetAtom());
        if (targetEntry.isPresent()) {
            // use the atom identifier to remap leaf
            UniqueAtomIdentifer atomIdentifer = targetEntry.get().getKey();
            LeafIdentifier leafIdentifier = new LeafIdentifier(atomIdentifer.getPdbIdentifier(), atomIdentifer.getModelIdentifier(),
                    atomIdentifer.getChainIdentifier(), atomIdentifer.getLeafSerial(), atomIdentifer.getLeafInsertionCode());
            logger.debug("Fixed to leaf identifier {}.", leafIdentifier);
            interaction.setTarget(leafIdentifier);
        } else {
            logger.warn("Unable to fix {}.", interaction);
        }
    }

    private boolean determineLigandInteraction(LeafSubstructure leafSubstructure, Structure structure) {
        if (leafSubstructure.getFamily() instanceof LigandFamily) {
            Optional<Chain> optionalChain = structure.getFirstModel().getChain(leafSubstructure.getIdentifier().getChainIdentifier());
            if (optionalChain.isPresent()) {
                OakChain chain = (OakChain) optionalChain.get();
                if (!chain.getConsecutivePart().contains(leafSubstructure)) {
                    logger.debug("{} is an interaction to a ligand", leafSubstructure);
                    return true;
                } else {
                    logger.debug("{} seems to be a ligand but is in the consecutive part of the chain", leafSubstructure);
                }
            }
        }
        return false;
    }

}
