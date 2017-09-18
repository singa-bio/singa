package de.bioforscher.singa.mmtf;

import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.*;

/**
 * @author cl
 */
public class MmtfChain implements ChainInterface {

    private StructureDataInterface data;
    private String chainIdentifier;
    private Set<Integer> relevantGroups;

    public MmtfChain(StructureDataInterface data, String chainIdentifier) {
        Set<Integer> internalChainEquivalents = new HashSet<>();
        this.relevantGroups = new TreeSet<>();
        this.data = data;
        this.chainIdentifier = chainIdentifier;
        // remember which internal chains are considered to belong to this chain
        for (int chainIndex = 0; chainIndex < data.getChainNames().length; chainIndex++) {
            if (data.getChainNames()[chainIndex].equals(chainIdentifier)) {
                internalChainEquivalents.add(chainIndex);
            }
        }
        // number of groups (leaves) per chain
        int[] groupsPerChain = data.getGroupsPerChain();
        int currentGroupIndex = 0;
        // get group indices relevant for this chain
        for (int groupsPerChainIndex = 0; groupsPerChainIndex < groupsPerChain.length; groupsPerChainIndex++) {
            int endRange = currentGroupIndex + groupsPerChain[groupsPerChainIndex];
            if (internalChainEquivalents.contains(groupsPerChainIndex)) {
                for (int groupIndex = currentGroupIndex; groupIndex <= endRange-1; groupIndex++) {
                    relevantGroups.add(groupIndex);
                }
            }
            currentGroupIndex = endRange;
        }
    }

    @Override
    public String getIdentifier() {
        return chainIdentifier;
    }

    @Override
    public List<LeafSubstructureInterface> getLeafSubstructures() {
        List<LeafSubstructureInterface> results = new ArrayList<>();
        int currentAtomIndex = 0;
        // collect atoms for all relevant groups
        for (int groupIndex = 0; groupIndex < data.getNumGroups(); groupIndex++) {
            // get number of relevant atoms for the current group type
            int lastAtom = currentAtomIndex + data.getNumAtomsInGroup(data.getGroupTypeIndices()[groupIndex]);
            if (relevantGroups.contains(groupIndex)) {
                MmtfLeafSubstructure leaf = new MmtfLeafSubstructure(data, groupIndex, currentAtomIndex, lastAtom - 1);
                results.add(leaf);
            }
            currentAtomIndex = lastAtom;
        }
        return results;
    }
}
