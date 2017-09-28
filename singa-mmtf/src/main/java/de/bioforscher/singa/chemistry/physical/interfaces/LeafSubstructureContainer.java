package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public interface LeafSubstructureContainer extends AtomContainer {

    List<LeafSubstructure<?>> getAllLeafSubstructures();

    Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier);

    default List<AminoAcid> getAllAminoAcids() {
        List<AminoAcid> aminoAcids = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof  AminoAcid) {
                aminoAcids.add((AminoAcid) leafSubstructure);
            }
        }
        return aminoAcids;
    }

    default Optional<AminoAcid> getAminoAcid(LeafIdentifier leafIdentifier) {
        Optional<LeafSubstructure<?>> leafSubstructureOptional = getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            return Optional.empty();
        }
        final LeafSubstructure<?> leafSubstructure = leafSubstructureOptional.get();
        if (!(leafSubstructure instanceof AminoAcid)) {
            return Optional.empty();
        }
        return Optional.of((AminoAcid) leafSubstructure);
    }

    default List<Nucleotide> getAllNucleotides() {
        List<Nucleotide> nucleotides = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof  Nucleotide) {
                nucleotides.add((Nucleotide) leafSubstructure);
            }
        }
        return nucleotides;
    }

    default Optional<Nucleotide> getNucleotide(LeafIdentifier leafIdentifier) {
        Optional<LeafSubstructure<?>> leafSubstructureOptional = getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            return Optional.empty();
        }
        final LeafSubstructure<?> leafSubstructure = leafSubstructureOptional.get();
        if (!(leafSubstructure instanceof Nucleotide)) {
            return Optional.empty();
        }
        return Optional.of((Nucleotide) leafSubstructure);
    }

    default List<Ligand> getAllLigands() {
        List<Ligand> ligands = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof  Ligand) {
                ligands.add((Ligand) leafSubstructure);
            }
        }
        return ligands;
    }

    default Optional<Ligand> getLigand(LeafIdentifier leafIdentifier) {
        Optional<LeafSubstructure<?>> leafSubstructureOptional = getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            return Optional.empty();
        }
        final LeafSubstructure<?> leafSubstructure = leafSubstructureOptional.get();
        if (!(leafSubstructure instanceof Ligand)) {
            return Optional.empty();
        }
        return Optional.of((Ligand) leafSubstructure);
    }

    default List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            atoms.addAll(leafSubstructure.getAllAtoms());
        }
        return atoms;
    }

    default Optional<Atom> getAtom(int atomIdentifier) {
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            for (Atom atom : leafSubstructure.getAllAtoms()) {
                if (atom.getIdentifier() == atomIdentifier) {
                    return Optional.of(atom);
                }
            }
        }
        return Optional.empty();
    }

}
