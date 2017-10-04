package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Chain;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.core.utility.Range;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * The implementation of {@link Chain} for mmtf structures. Remembers the chain identifier, the leaf relevant for this
 * chain, their respective leaf identifiers, and atom ranges.
 *
 * @author cl
 */
public class MmtfChain implements Chain {

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
    private HashMap<Integer, MmtfLeafSubstructure<?>> cachedLeaves;

    /**
     * The indices of the relevant leaves in the group data arrays.
     */
    private List<Integer> relevantGroups;

    /**
     * The generated leaf identifiers for all relevant leaves.
     */
    private List<LeafIdentifier> leafIdentifiers;

    /**
     * Contains the relevant atom ranges for all relevant leaves.
     */
    private List<Range<Integer>> atomRanges;

    /**
     * Creates a new {@link MmtfChain}.
     *
     * @param data The original data.
     * @param chainIdentifier The chain identifier.
     * @param internalChainIndices All internal chain indices relevant for this chain.
     * @param modelIndex The index of the parent model (the model identifier is the model index + 1).
     */
    MmtfChain(StructureDataInterface data, String chainIdentifier, List<Integer> internalChainIndices, int modelIndex) {
        this.data = data;
        this.chainIdentifier = chainIdentifier;
        this.relevantGroups = new ArrayList<>();
        this.leafIdentifiers = new ArrayList<>();
        this.atomRanges = new ArrayList<>();
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
                    leafIdentifiers.add(new LeafIdentifier(data.getStructureId(), modelIndex + 1, chainIdentifier, data.getGroupIds()[groupIndex], data.getInsCodes()[groupIndex]));
                }
            }
            currentGroupIndex = endRange;
        }

        int currentAtomIndex = 0;
        for (int groupIndex = 0; groupIndex < data.getNumGroups(); groupIndex++) {
            // get number of relevant atoms for the current group type
            int lastAtom = currentAtomIndex + data.getNumAtomsInGroup(data.getGroupTypeIndices()[groupIndex]);
            if (relevantGroups.contains(groupIndex)) {
                atomRanges.add(new Range<>(currentAtomIndex, lastAtom - 1));
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
        this.data = mmtfChain.data;
        this.chainIdentifier = mmtfChain.chainIdentifier;
        this.relevantGroups = new ArrayList<>(mmtfChain.relevantGroups);
        this.leafIdentifiers = new ArrayList<>(mmtfChain.leafIdentifiers);
        this.atomRanges = new ArrayList<>(mmtfChain.atomRanges);
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
                MmtfLeafSubstructure<?> leaf = MmtfLeafFactory.createLeaf(data, leafIdentifiers.get(internalIndex), relevantGroups.get(internalIndex), atomRange.getLowerBound(), atomRange.getUpperBound());
                cachedLeaves.put(internalIndex, leaf);
                results.add(leaf);
            }
        }
        return results;
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        int internalIndex = leafIdentifiers.indexOf(leafIdentifier);
        if (internalIndex == -1) {
            return Optional.empty();
        }
        if (cachedLeaves.containsKey(internalIndex)) {
            return Optional.of(cachedLeaves.get(internalIndex));
        } else {
            final Range<Integer> atomRange = atomRanges.get(internalIndex);
            MmtfLeafSubstructure<?> leaf = MmtfLeafFactory.createLeaf(data, leafIdentifiers.get(internalIndex), relevantGroups.get(internalIndex), atomRange.getLowerBound(), atomRange.getUpperBound());
            return Optional.of(leaf);
        }
    }

    @Override
    public Chain getCopy() {
        return new MmtfChain(this);
    }
}
