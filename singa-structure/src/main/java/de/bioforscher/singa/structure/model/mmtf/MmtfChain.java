package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.core.utility.Range;
import de.bioforscher.singa.structure.model.families.StructuralFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.*;

/**
 * The implementation of {@link Chain} for mmtf structures. Remembers the chain identifier, the leaf relevant for this
 * chain, their respective leaf identifiers, and atom ranges.
 *
 * @author cl
 */
public class MmtfChain implements Chain {

    /**
     * The original bytes kept to copy.
     */
    private byte[] bytes;

    /**
     * The original data.
     */
    private StructureDataInterface data;

    /**
     * The identifier of this chain.
     */
    private String chainIdentifier;

    /**
     * The leaves that have already been requested.
     */
    private Map<Integer, MmtfLeafSubstructure<?>> cachedLeaves;

    /**
     * The indices of the relevant leaves in the group data arrays.
     */
    private List<Integer> relevantGroups;

    /**
     * The generated leaf identifiers for all relevant leaves.
     */
    private Map<Integer, LeafIdentifier> leafIdentifiers;

    /**
     * Contains the relevant atom ranges for all relevant leaves.
     */
    private Map<Integer, Range<Integer>> atomRanges;

    /**
     * Creates a new {@link MmtfChain}.
     *
     * @param data The original data.
     * @param chainIdentifier The chain identifier.
     * @param internalChainIndices All internal chain indices relevant for this chain.
     * @param modelIndex The index of the parent model (the model identifier is the model index + 1).
     */
    MmtfChain(StructureDataInterface data, byte[] bytes, String chainIdentifier, List<Integer> internalChainIndices, int modelIndex) {
        this.data = data;
        this.bytes = bytes;
        this.chainIdentifier = chainIdentifier;
        relevantGroups = new ArrayList<>();
        leafIdentifiers = new HashMap<>();
        atomRanges = new HashMap<>();
        cachedLeaves = new HashMap<>();

        // number of groups (leaves) per chain
        int[] groupsPerChain = data.getGroupsPerChain();
        int currentGroupIndex = 0;
        // get group indices relevant for this chain
        for (int groupsPerChainIndex = 0; groupsPerChainIndex < groupsPerChain.length; groupsPerChainIndex++) {
            int endRange = currentGroupIndex + groupsPerChain[groupsPerChainIndex];
            if (internalChainIndices.contains(groupsPerChainIndex)) {
                for (int groupIndex = currentGroupIndex; groupIndex <= endRange - 1; groupIndex++) {
                    relevantGroups.add(groupIndex);
                    leafIdentifiers.put(groupIndex, new LeafIdentifier(data.getStructureId(), modelIndex + 1, chainIdentifier, data.getGroupIds()[groupIndex], data.getInsCodes()[groupIndex]));
                }
            }
            currentGroupIndex = endRange;
        }

        int currentAtomIndex = 0;
        for (int groupIndex = 0; groupIndex < data.getNumGroups(); groupIndex++) {
            // get number of relevant atoms for the current group type
            int lastAtom = currentAtomIndex + data.getNumAtomsInGroup(data.getGroupTypeIndices()[groupIndex]);
            if (relevantGroups.contains(groupIndex)) {
                atomRanges.put(groupIndex, new Range<>(currentAtomIndex, lastAtom - 1));
            }
            currentAtomIndex = lastAtom;
        }

    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfChain} to a new instance.
     *
     * @param mmtfChain The {@link MmtfChain} to copy.
     */
    private MmtfChain(MmtfChain mmtfChain) {
        bytes = mmtfChain.bytes;
        data = mmtfChain.data;
        chainIdentifier = mmtfChain.chainIdentifier;
        cachedLeaves = new HashMap<>(mmtfChain.cachedLeaves);
        relevantGroups = new ArrayList<>(mmtfChain.relevantGroups);
        leafIdentifiers = new HashMap<>(mmtfChain.leafIdentifiers);
        atomRanges = new HashMap<>(mmtfChain.atomRanges);
    }

    @Override
    public String getChainIdentifier() {
        return chainIdentifier;
    }

    @Override
    public List<LeafSubstructure<?>> getAllLeafSubstructures() {
        List<LeafSubstructure<?>> results = new ArrayList<>();
        for (Integer relevantGroup : relevantGroups) {
            if (cachedLeaves.containsKey(relevantGroup)) {
                results.add(cachedLeaves.get(relevantGroup));
            } else {
                final Range<Integer> atomRange = atomRanges.get(relevantGroup);
                MmtfLeafSubstructure<?> leaf = MmtfLeafFactory.createLeaf(data, bytes, leafIdentifiers.get(relevantGroup), relevantGroup, atomRange.getLowerBound(), atomRange.getUpperBound());
                cachedLeaves.put(relevantGroup, leaf);
                results.add(leaf);
            }
        }
        return results;
    }

    @Override
    public Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final int internalIndex = getInternalIndexForLeafIdentifier(leafIdentifier);
        if (internalIndex == -1) {
            return Optional.empty();
        }
        if (cachedLeaves.containsKey(internalIndex)) {
            return Optional.of(cachedLeaves.get(internalIndex));
        } else {
            final Range<Integer> atomRange = atomRanges.get(internalIndex);
            MmtfLeafSubstructure<?> leaf = MmtfLeafFactory.createLeaf(data, bytes, leafIdentifiers.get(internalIndex), internalIndex, atomRange.getLowerBound(), atomRange.getUpperBound());
            cachedLeaves.put(internalIndex, leaf);
            return Optional.of(leaf);
        }
    }

    @Override
    public LeafSubstructure<?> getFirstLeafSubstructure() {
        return getLeafSubstructure(leafIdentifiers.values().iterator().next()).get();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final int internalIndex = getInternalIndexForLeafIdentifier(leafIdentifier);
        if (internalIndex == -1) {
            return false;
        }
        leafIdentifiers.remove(internalIndex);
        cachedLeaves.remove(internalIndex);
        // the Integer cast has to be there
        relevantGroups.remove((Integer) internalIndex);
        atomRanges.remove(internalIndex);
        return true;
    }

    /**
     * Removes all {@link LeafSubstructure}s from this container that are not referenced in the given
     * {@link LeafSubstructureContainer}. Basically all LeafSubstructures are removed that do not match any of the
     * given containers families. This method also keeps exchangeable families if any are defined.
     *
     * Further, this method does not cache the leafs that are being removed in contrary to the default implementation.
     *
     * @param leafSubstructuresToKeep The leaf structures that are kept.
     */
    public void removeLeafSubstructuresNotRelevantFor(LeafSubstructureContainer leafSubstructuresToKeep) {
        // this method is somewhat optimized for speed
        // collect all containing types (own types <b>plus</b> exchangeable types) of the query motif
        Set<String> relevantFamilies = new HashSet<>();
        for (LeafSubstructure<?> leafSubstructure : leafSubstructuresToKeep.getAllLeafSubstructures()) {
            relevantFamilies.add(leafSubstructure.getFamily().getThreeLetterCode().toUpperCase());
            for (StructuralFamily structuralFamily : leafSubstructure.getExchangeableFamilies()) {
                relevantFamilies.add(structuralFamily.getThreeLetterCode().toUpperCase());
            }
        }
        // remove references by mmtf group name
        final ListIterator<Integer> relevantGroupIterator = relevantGroups.listIterator();
        while (relevantGroupIterator.hasNext()) {
            int internalIndex = relevantGroupIterator.next();
            final String groupFamily = data.getGroupName(data.getGroupTypeIndices()[internalIndex]);
            if (!relevantFamilies.contains(groupFamily)) {
                leafIdentifiers.remove(internalIndex);
                atomRanges.remove(internalIndex);
                relevantGroupIterator.remove();
            }
        }
    }

    @Override
    public int getNumberOfLeafSubstructures() {
        return leafIdentifiers.size();
    }

    private int getInternalIndexForLeafIdentifier(LeafIdentifier leafIdentifier) {
        for (Map.Entry<Integer, LeafIdentifier> leafIdentifierEntry : leafIdentifiers.entrySet()) {
            if (leafIdentifierEntry.getValue().equals(leafIdentifier)) {
                return leafIdentifierEntry.getKey();
            }
        }
        return -1;
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                leafSubstructure.removeAtom(atomIdentifier);
            }
        }
    }

    @Override
    public Chain getCopy() {
        return new MmtfChain(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MmtfChain mmtfChain = (MmtfChain) o;
        return Objects.equals(chainIdentifier, mmtfChain.chainIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chainIdentifier);
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
