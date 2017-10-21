package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.core.utility.Range;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
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
        this.relevantGroups = new ArrayList<>();
        this.leafIdentifiers = new HashMap<>();
        this.atomRanges = new HashMap<>();
        this.cachedLeaves = new HashMap<>();

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
        this.bytes = mmtfChain.bytes;
        this.data = MmtfStructure.bytesToStructureData(bytes);
        this.chainIdentifier = mmtfChain.chainIdentifier;
        this.relevantGroups = new ArrayList<>(mmtfChain.relevantGroups);
        this.leafIdentifiers = new HashMap<>(mmtfChain.leafIdentifiers);
        this.atomRanges = new HashMap<>(mmtfChain.atomRanges);
    }

    @Override
    public String getIdentifier() {
        return chainIdentifier;
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        List<LeafSubstructure> results = new ArrayList<>();
        for (int internalIndex = 0; internalIndex < atomRanges.size(); internalIndex++) {
            if (cachedLeaves.containsKey(internalIndex)) {
                results.add(cachedLeaves.get(internalIndex));
            } else {
                final Range<Integer> atomRange = atomRanges.get(internalIndex);
                MmtfLeafSubstructure<?> leaf = MmtfLeafFactory.createLeaf(data, bytes, leafIdentifiers.get(internalIndex), relevantGroups.get(internalIndex), atomRange.getLowerBound(), atomRange.getUpperBound());
                cachedLeaves.put(internalIndex, leaf);
                results.add(leaf);
            }
        }
        return results;
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final int internalIndex = getInternalIndexForLeafIdentifier(leafIdentifier);
        if (internalIndex == -1) {
            return Optional.empty();
        }
        if (cachedLeaves.containsKey(internalIndex)) {
            return Optional.of(cachedLeaves.get(internalIndex));
        } else {
            final Range<Integer> atomRange = atomRanges.get(internalIndex);
            MmtfLeafSubstructure<?> leaf = MmtfLeafFactory.createLeaf(data, bytes, leafIdentifiers.get(internalIndex), relevantGroups.get(internalIndex), atomRange.getLowerBound(), atomRange.getUpperBound());
            return Optional.of(leaf);
        }
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final int internalIndex = getInternalIndexForLeafIdentifier(leafIdentifier);
        if (internalIndex == -1) {
            return false;
        }
        this.leafIdentifiers.remove(internalIndex);
        this.cachedLeaves.remove(internalIndex);
        this.relevantGroups.remove(internalIndex);
        this.atomRanges.remove(internalIndex);
        return true;
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


    private int getInternalIndexForLeafIdentifier(LeafIdentifier leafIdentifier) {
        for (Map.Entry<Integer, LeafIdentifier> leafIdentifierEntry : this.leafIdentifiers.entrySet()) {
            if (leafIdentifierEntry.getValue().equals(leafIdentifier)) {
                return leafIdentifierEntry.getKey();
            }
        }
        return -1;
    }
}
