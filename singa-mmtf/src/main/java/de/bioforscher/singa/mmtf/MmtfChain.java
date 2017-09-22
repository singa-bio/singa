package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.*;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.core.utility.Range;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public class MmtfChain implements Chain {

    private StructureDataInterface data;
    private String chainIdentifier;

    /**
     * internal positions in the array relevant for this chain
     */
    private List<Integer> relevantGroups;
    private List<LeafIdentifier> leafIdentifiers;
    private List<Range<Integer>> atomRanges;

    MmtfChain(StructureDataInterface data, String chainIdentifier, List<Integer> internalIdentifiers, int modelIdentifier) {
        this.data = data;
        this.chainIdentifier = chainIdentifier;
        this.relevantGroups = new ArrayList<>();
        this.leafIdentifiers = new ArrayList<>();
        this.atomRanges = new ArrayList<>();

        // number of groups (leaves) per chain
        int[] groupsPerChain = data.getGroupsPerChain();
        int currentGroupIndex = 0;
        // get group indices relevant for this chain
        for (int groupsPerChainIndex = 0; groupsPerChainIndex < groupsPerChain.length; groupsPerChainIndex++) {
            int endRange = currentGroupIndex + groupsPerChain[groupsPerChainIndex];
            if (internalIdentifiers.contains(groupsPerChainIndex)) {
                for (int groupIndex = currentGroupIndex; groupIndex <= endRange - 1; groupIndex++) {
                    relevantGroups.add(groupIndex);
                    leafIdentifiers.add(new LeafIdentifier(data.getStructureId(), modelIdentifier, chainIdentifier, data.getGroupIds()[groupIndex], data.getInsCodes()[groupIndex]));
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

    @Override
    public String getIdentifier() {
        return chainIdentifier;
    }

    @Override
    public List<LeafSubstructure<?>> getAllLeafSubstructures() {
        List<LeafSubstructure<?>> results = new ArrayList<>();
        for (int i = 0; i < atomRanges.size(); i++) {
            final Range<Integer> atomRange = atomRanges.get(i);
            results.add(MmtfLeafFactory.createLeaf(data, leafIdentifiers.get(i), relevantGroups.get(i), atomRange.getLowerBound(), atomRange.getUpperBound()));
        }
        return results;
    }

    @Override
    public Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        int internalIdentifier = leafIdentifiers.indexOf(leafIdentifier);
        if (internalIdentifier == -1) {
            return Optional.empty();
        }
        final Range<Integer> atomRange = atomRanges.get(internalIdentifier);
        return Optional.of(MmtfLeafFactory.createLeaf(data, leafIdentifiers.get(internalIdentifier), relevantGroups.get(internalIdentifier), atomRange.getLowerBound(), atomRange.getUpperBound()));
    }

    @Override
    public List<AminoAcid> getAllAminoAcids() {
        return null;
    }

    @Override
    public Optional<AminoAcid> getAminoAcid(LeafIdentifier leafIdentifier) {
        return null;
    }

    @Override
    public List<Nucleotide> getAllNucleotides() {
        return null;
    }

    @Override
    public Optional<Nucleotide> getNucleotide(LeafIdentifier leafIdentifier) {
        return null;
    }

    @Override
    public List<Ligand> getAllLigands() {
        return null;
    }

    @Override
    public Optional<Ligand> getLigand(LeafIdentifier leafIdentifier) {
        return null;
    }

    @Override
    public List<Atom> getAllAtoms() {
        return null;
    }

    @Override
    public Optional<Atom> getAtom(int atomIdentifier) {
        return null;
    }

    @Override
    public Chain getCopy() {
        return null;
    }
}
