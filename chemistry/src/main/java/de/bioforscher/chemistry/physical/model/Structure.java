package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.proteins.Chain;
import de.bioforscher.chemistry.physical.proteins.Residue;
import de.bioforscher.chemistry.physical.proteins.ResidueFactory;

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
    private Map<Integer, SubStructure> substructures;

    private boolean containingModels;

    public Structure() {
        this.substructures = new HashMap<>();
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

    public Collection<SubStructure> getSubstructures() {
        return this.substructures.values();
    }

    /**
     * Adds a predefined {@link SubStructure} to this Structure. This {@link SubStructure} needs to have a unique
     * identifier, with which it can be addressed.
     *
     * @param subStructure The {@link SubStructure} to add.
     */
    public void addSubstructure(SubStructure subStructure) {
        this.substructures.put(subStructure.getIdentifier(), subStructure);
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
                .map(SubStructure::getResidues)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Atom> getAllAtoms() {
        return this.substructures.values().stream()
                .map(SubStructure::getAllAtoms)
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
                        System.out.println("  Residue "+residue.getName()+"");
                        residue.getAllAtoms().forEach(atom -> System.out.println("   Atom "+atom.getIdentifier()));
                    });
                });

            });
        } else {
            structure.getAllChains().forEach( chain -> {
                System.out.println("Chain "+chain.getName()+":");
                chain.getResidues().forEach( residue -> {
                    System.out.println(" Residue "+residue.getName()+"");
                    residue.getAllAtoms().forEach(atom -> System.out.println("  Atom "+atom.getIdentifier()));
                });
            });
        }
    }


}
