package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.families.ResidueFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller substructures that can be connected with edges.
 */
public class Structure {

    /**
     * The substructures of the graph.
     */
    private Map<Integer, BranchSubstructure<?>> substructures;

    private boolean containingModels;

    public Structure() {
        this.substructures = new TreeMap<>();
    }

    /**
     * Returns true, if this Structure contains {@link StructuralModel}s.
     *
     * @return true, if this Structure contains {@link StructuralModel}s.
     */
    public boolean isContainingModels() {
        return this.containingModels;
    }

    /**
     * Sets whether this Structure contains {@link StructuralModel}s.
     *
     * @param containingModels true, if this Structure contains {@link StructuralModel}s.
     */
    public void setContainingModels(boolean containingModels) {
        this.containingModels = containingModels;
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
        if (this.isContainingModels()) {
            return this.getSubstructures().stream()
                    .map(StructuralModel.class::cast)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalStateException("This structure does not contain models.");
        }
    }

    public List<Chain> getAllChains() {
        if (this.isContainingModels()) {
            return this.getAllModels().stream()
                    .map(StructuralModel::getAllChains)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } else {
            return this.getSubstructures().stream()
                    .map(Chain.class::cast)
                    .collect(Collectors.toList());
        }
    }

    public List<Residue> getAllResidues() {
        return this.substructures.values().stream()
                .map(BranchSubstructure::getResidues)
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

        ResidueFactory.setToOmitHydrogens(true);
        Structure structure = PDBParserService.parseProteinById("4HHB");

        if (structure.isContainingModels()) {
            structure.getAllModels().forEach(model -> {
                System.out.println("Model "+model.getIdentifier()+":");
                model.getAllChains().forEach( chain -> {
                    System.out.println(" Chain "+chain.getName()+":");
                    chain.getResidues().forEach( residue -> {
                        System.out.println("  Residue "+residue.toString()+"");
                        residue.getAllAtoms().forEach(atom -> System.out.println("   Atom "+atom.getIdentifier()));
                    });
                });

            });
        } else {
            structure.getAllChains().forEach( chain -> {
                System.out.println("Chain "+chain.getName()+":");
                chain.getResidues().forEach( residue -> {
                    System.out.println(" Residue "+residue.toString()+"");
                    residue.getAllAtoms().forEach(atom -> System.out.println("  Atom "+atom.getIdentifier()));
                });
            });
        }
    }


}
