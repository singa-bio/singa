package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.proteins.Residue;

import java.util.*;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller substructures that can be connected with edges.
 */
public class Structure {

    /**
     * The substructures of the graph.
     */
    private Map<Integer, SubStructure> substructures;

    public Structure() {
        this.substructures = new HashMap<>();
    }

    /**
     * Adds a predefined Substructure to this Structure. This Substructure needs to have a unique identifier, with which
     * it can be addressed.
     *
     * @param subStructure
     */
    public void addSubstructure(SubStructure subStructure) {
        this.substructures.put(subStructure.getIdentifier(), subStructure);
    }

    public SortedMap<Integer, Residue> getResidues() {
        SortedMap<Integer, Residue> residues = new TreeMap<>();
        for (SubStructure subStructure: this.substructures.values()) {
            if (subStructure instanceof Residue) {
                Residue residue = (Residue)subStructure;
                residues.put(residue.getIdentifier(), residue);
            }
        }
        return residues;
    }


}
