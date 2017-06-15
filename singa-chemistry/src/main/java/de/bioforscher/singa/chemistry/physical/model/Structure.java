package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isChain;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isModel;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller branchSubstructures that can be connected with edges.
 */
public class Structure {

    private String pdbIdentifier;
    private String title;
    /**
     * The branchSubstructures of the graph.
     */
    private Map<Integer, BranchSubstructure<?>> branchSubstructures;

    public Structure() {
        this.branchSubstructures = new TreeMap<>();
    }

    @Override
    public String toString() {
        return "Structure{" +
                "pdbIdentifier='" + this.pdbIdentifier + '\'' +
                '}';
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
        return getBranchSubstructures().stream()
                .filter(isModel())
                .map(StructuralModel.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Returns an {@link Optional} of the first model found in this structure. This should always be deterministic for
     * Xtal structures. NMR structures contain several models.
     *
     * @return An {@link Optional} encapsulating the first {@link StructuralModel} found.
     */
    public Optional<StructuralModel> getFirstModel() {
        return getBranchSubstructures().stream()
                .filter(isModel())
                .map(StructuralModel.class::cast)
                .findFirst();
    }

    public List<Chain> getAllChains() {
        return getAllBranches().stream()
                .filter(isChain())
                .map(Chain.class::cast)
                .collect(Collectors.toList());
    }

    public List<AminoAcid> getAllAminoAcids() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getAminoAcids)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getAllLeaves() {
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

    public String getPdbIdentifier() {
        return this.pdbIdentifier;
    }

    public void setPdbIdentifier(String pdbID) {
        this.pdbIdentifier = pdbID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
