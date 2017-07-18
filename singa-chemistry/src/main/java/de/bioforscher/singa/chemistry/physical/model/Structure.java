package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.core.identifier.PDBIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isChain;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isModel;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller branchSubstructures that can be connected with edges.
 */
public class Structure {

    private static final Logger logger = LoggerFactory.getLogger(Structure.class);

    /**
     * The PDB identifier of the structure.
     */
    private String pdbIdentifier;

    /**
     * The title of the structure.
     */
    private String title;

    /**
     * The branches this structure contains.
     */
    private Map<Object, BranchSubstructure<?, ?>> branchSubstructures;

    /**
     * Creates a new empty structure.
     */
    public Structure() {
        this.branchSubstructures = new TreeMap<>();
    }

    /**
     * Returns the PDB identifier in lower case.
     *
     * @return The PDB identifier.
     */
    public String getPdbIdentifier() {
        return this.pdbIdentifier;
    }

    /**
     * Sets the PDB identifier. The identifier has to conform to the patterns in {@link PDBIdentifier}.
     * @param pdbIdentifier The PDB Identifier.
     */
    public void setPdbIdentifier(String pdbIdentifier) {
        if (PDBIdentifier.PATTERN.matcher(pdbIdentifier).matches()) {
            this.pdbIdentifier = pdbIdentifier.toLowerCase();
        } else {
            logger.warn("The identifier {} is not a valid PDB identifier. No identifier has been set.");
        }
    }

    /**
     * Returns the title of the structure.
     * @return The title of the structure.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title of the structure.
     * @param title The title of the structure.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns all {@link BranchSubstructure}s, referenced for this structure, without traversing to subordinate
     * branches.
     *
     * @return All {@link BranchSubstructure}s.
     */
    private Collection<BranchSubstructure<?, ?>> getBranchSubstructures() {
        return this.branchSubstructures.values();
    }

    /**
     * Adds a predefined {@link BranchSubstructure} to this Structure.
     *
     * @param branchSubstructure The {@link BranchSubstructure} to add.
     */
    public void addBranchSubstructure(BranchSubstructure<?, ?> branchSubstructure) {
        this.branchSubstructures.put(branchSubstructure.getIdentifier(), branchSubstructure);
    }

    /**
     * Returns all {@link BranchSubstructure}s referenced in this structure. This returns also subordinate branches such
     * as {@link Chain}s or {@link StructuralMotif}s.
     *
     * @return All branch substructures.
     */
    public List<BranchSubstructure<?, ?>> getAllBranches() {
        List<BranchSubstructure<?, ?>> branchSubstructures = this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getBranchSubstructures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        branchSubstructures.addAll(this.branchSubstructures.values());
        return branchSubstructures;
    }

    /**
     * Returns all {@link StructuralModel}s from this structure. In most cases this shows the same behaviour as the
     * {@link Structure#getBranchSubstructures} method.
     *
     * @return All models.
     */
    public List<StructuralModel> getAllModels() {
        return getBranchSubstructures().stream()
                .filter(isModel())
                .map(StructuralModel.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Returns an {@link Optional} of the first {@link StructuralModel} found in this structure. This should always be
     * deterministic for christal structures. NMR structures contain several models.
     *
     * @return An {@link Optional} encapsulating the first {@link StructuralModel} found.
     */
    public Optional<StructuralModel> getFirstModel() {
        return getBranchSubstructures().stream()
                .filter(isModel())
                .map(StructuralModel.class::cast)
                .findFirst();
    }

    /**
     * Returns all {@link Chain}s from all models.
     *
     * @return All chains.
     */
    public List<Chain> getAllChains() {
        return getAllBranches().stream()
                .filter(isChain())
                .map(Chain.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Returns an {@link Optional} of the first {@link Chain} found in this structure.
     *
     * @return An {@link Optional} encapsulating the first {@link Chain} found.
     */
    public Optional<Chain> getFistChain() {
        return getAllBranches().stream()
                .filter(isChain())
                .map(Chain.class::cast)
                .findFirst();
    }

    /**
     * Returns all {@link LeafSubstructure}s referenced anywhere in this structure.
     *
     * @return All leaf substructures.
     */
    public List<LeafSubstructure<?, ?>> getAllLeaves() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getLeafSubstructures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns all {@link AminoAcid}s referenced anywhere in this structure.
     *
     * @return All amino acids.
     */
    public List<AminoAcid> getAllAminoAcids() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getAminoAcids)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns all {@link Atom}s referenced anywhere in this structure.
     *
     * @return All atoms.
     */
    public List<Atom> getAllAtoms() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getAllAtoms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


}
