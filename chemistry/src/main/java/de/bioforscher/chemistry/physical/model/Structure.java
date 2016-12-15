package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller substructures that can be connected with edges.
 */
public class Structure {

    private String pdbID;

    /**
     * The substructures of the graph.
     */
    private Map<Integer, BranchSubstructure<?>> substructures;

    public Structure() {
        this.substructures = new TreeMap<>();
    }

    public List<BranchSubstructure<?>> getSubstructures() {
        return new ArrayList<>(this.substructures.values());
    }

    /**
     * Adds a predefined {@link BranchSubstructure} to this Structure. This {@link BranchSubstructure} needs to have a unique
     * identifier, with which it can be addressed.
     *
     * @param branchSubstructure The {@link BranchSubstructure} to add.
     */
    public void addSubstructure(BranchSubstructure<?> branchSubstructure) {
        this.substructures.put(branchSubstructure.getIdentifier(), branchSubstructure);
    }

    public List<StructuralModel> getAllModels() {
        return this.getSubstructures().stream()
                .map(StructuralModel.class::cast)
                .collect(Collectors.toList());
    }

    public List<Chain> getAllChains() {
        return this.getAllModels().stream()
                .map(StructuralModel::getAllChains)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Residue> getAllResidues() {
        return this.substructures.values().stream()
                .map(BranchSubstructure::getResidues)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<LeafSubstructure<?, ?>> getAllLeafs() {
        return this.substructures.values().stream()
                .map(BranchSubstructure::getLeafSubstructures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Atom> getAllAtoms() {
        return this.substructures.values().stream()
                .map(BranchSubstructure::getAllAtoms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {

        LeafFactory.setToOmitHydrogens(true);
        Structure structure = PDBParserService.parseProteinById("4HHB");
        structure.getAllModels().forEach(model -> {
            System.out.println("Model " + model.getIdentifier() + ":");
            model.getAllChains().forEach(chain -> {
                System.out.println(" Chain " + chain.getName() + ":");
                chain.getResidues().forEach(residue -> {
                    System.out.println("  Residue " + residue.toString() + "");
                    residue.getAllAtoms().forEach(atom -> System.out.println("   Atom " + atom.getIdentifier()));
                });
            });

        });
    }

    public String getPdbID() {
        return pdbID;
    }

    public void setPdbID(String pdbID) {
        this.pdbID = pdbID;
    }
}
