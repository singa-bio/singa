package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.families.LigandFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.core.identifier.PDBIdentifier;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.*;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller substructures that can be connected with edges.
 */
public class Structure {

    /**
     * The logger.
     */
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
     * The identifier that has been added last.
     */
    private int lastAddedAtomIdentifier;

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
     *
     * @param pdbIdentifier The PDB Identifier.
     */
    public void setPdbIdentifier(String pdbIdentifier) {
        if (PDBIdentifier.PATTERN.matcher(pdbIdentifier).matches()) {
            this.pdbIdentifier = pdbIdentifier.toLowerCase();
        } else {
            logger.warn("The identifier {} is not a valid PDB identifier. No identifier has been set.", pdbIdentifier);
        }
    }

    /**
     * Sets the last added atom identifier.
     *
     * @param lastAddedAtomIdentifier
     */
    public void setLastAddedAtomIdentifier(int lastAddedAtomIdentifier) {
        this.lastAddedAtomIdentifier = lastAddedAtomIdentifier;
    }

    /**
     * Returns the title of the structure.
     *
     * @return The title of the structure.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title of the structure.
     *
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
    public StructuralModel getFirstModel() {
        return getBranchSubstructures().stream()
                .filter(isModel())
                .map(StructuralModel.class::cast)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("The structure does not contain a model."));
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
     * Returns the first {@link Chain} found in this structure.
     *
     * @return The first {@link Chain} found.
     */
    public Chain getFirstChain() {
        return getFirstModel().getFirstChain();
    }

    /**
     * Returns an {@link Optional} of the {@link Chain} with the given identifier from the first model in the
     * structure.
     *
     * @return An {@link Optional} encapsulating the {@link Chain} found.
     */
    public Optional<Chain> getChain(String chainIdentifier) {
        return getFirstModel().getAllChains().stream()
                .filter(hasIdentifier(chainIdentifier))
                .findAny();
    }

    /**
     * Returns all {@link LeafSubstructure}s referenced anywhere in this structure.
     *
     * @return All leaf substructures.
     */
    public List<LeafSubstructure<?, ?>> getAllLeafSubstructures() {
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
     * Returns all {@link Nucleotide}s referenced anywhere in this structure.
     *
     * @return All amino acids.
     */
    public List<Nucleotide> getAllNucleotides() {
        return this.branchSubstructures.values().stream()
                .map(BranchSubstructure::getNucleotides)
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

    /**
     * Returns the leaf with the given leaf identifier.
     *
     * @param identifier The leaf identifier.
     * @return The associated leaf.
     */
    public Optional<LeafSubstructure<?, ?>> getLeafSubstructure(LeafIdentifier identifier) {
        for (StructuralModel model : getAllModels()) {
            for (Chain chain : model.getAllChains()) {
                for (LeafSubstructure<?, ?> leafSubstructure : chain.getLeafSubstructures()) {
                    if (leafSubstructure.getIdentifier().equals(identifier)) {
                        return Optional.of(leafSubstructure);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the atom with the given atom serial.
     *
     * @param atomSerial The atom serial.
     * @return The atom and its identifier.
     */
    public Optional<Map.Entry<UniqueAtomIdentifer, Atom>> getAtom(int atomSerial) {
        for (StructuralModel model : getAllModels()) {
            for (Chain chain : model.getAllChains()) {
                for (LeafSubstructure<?, ?> leafSubstructure : chain.getLeafSubstructures()) {
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        if (atom.getIdentifier().equals(atomSerial)) {
                            UniqueAtomIdentifer identifier = new UniqueAtomIdentifer(this.pdbIdentifier, model.getIdentifier(),
                                    chain.getIdentifier(), leafSubstructure.getIdentifier().getSerial(), leafSubstructure.getIdentifier().getInsertionCode(),
                                    atomSerial);
                            return Optional.of(new AbstractMap.SimpleEntry<>(identifier, atom));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Structure{" +
                "pdbIdentifier='" + this.pdbIdentifier + '\'' +
                ", title='" + this.title + '\'' +
                '}';
    }

    /**
     * Adds a pseudo atom (preferably for interactions) to the first model of this structure.
     *
     * @param chain The chain to add the atom to.
     * @param threeLetterCode The three letter code of the created leaf substructure.
     * @param position The position of the atom.
     */
    public void addPseudoAtom(String chain, String threeLetterCode, Vector3D position) {
        Chain leafChain = getFirstModel().getAllChains().stream()
                .filter(c -> c.getIdentifier().equals(chain))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not find given chain to add an atom to."));
        AtomContainer<LigandFamily> container = new AtomContainer<>(leafChain.getNextLeafIdentifier(), new LigandFamily(threeLetterCode));
        this.lastAddedAtomIdentifier++;
        container.addNode(new RegularAtom(this.lastAddedAtomIdentifier, ElementProvider.UNKOWN, "CA", position));
        leafChain.addSubstructure(container);
    }
}
