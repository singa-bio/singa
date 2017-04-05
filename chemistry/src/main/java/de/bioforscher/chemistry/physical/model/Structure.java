package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isChain;
import static de.bioforscher.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isModel;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller branchSubstructures that can be connected with edges.
 */
public class Structure {

    private String pdbID;

    /**
     * The branchSubstructures of the graph.
     */
    private Map<Integer, BranchSubstructure<?>> branchSubstructures;

    public Structure() {
        this.branchSubstructures = new TreeMap<>();
    }

    /**
     * TODO behavior of this method is unexpected, this should return a {@link StructuralModel}?
     */
    private List<BranchSubstructure<?>> getBranchSubstructures() {
        return new ArrayList<>(this.branchSubstructures.values());
    }

    /**
     * Adds a predefined {@link BranchSubstructure} to this Structure. This {@link BranchSubstructure} needs to have a unique
     * identifier, with which it can be addressed.
     *
     * @param branchSubstructure The {@link BranchSubstructure} to add.
     */
    public void addSubstructure(BranchSubstructure<?> branchSubstructure) {
        this.branchSubstructures.put(branchSubstructure.getIdentifier(), branchSubstructure);
    }

    public List<StructuralModel> getAllModels() {
        return this.getBranchSubstructures().stream()
                .filter(isModel())
                .map(StructuralModel.class::cast)
                .collect(Collectors.toList());
    }

    public List<Chain> getAllChains() {
        return this.getAllBranches().stream()
                .filter(isChain())
                .map(Chain.class::cast)
                .collect(Collectors.toList());
    }

    public List<AminoAcid> getAllResidues() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getAminoAcids)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getAllLeafs() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getLeafSubstructures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<BranchSubstructure<?>> getAllBranches() {
        List<BranchSubstructure<?>> branchSubstructures = this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getBranchSubstructures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        branchSubstructures.addAll(this.branchSubstructures.values());
        return branchSubstructures;
    }

    public List<Atom> getAllAtoms() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getAllAtoms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public String getPdbID() {
        return this.pdbID;
    }

    public void setPdbID(String pdbID) {
        this.pdbID = pdbID;
    }
}
