package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public interface Model {

    int getIdentifier();

    List<Chain> getAllChains();

    Chain getFirstChain();

    Optional<Chain> getChain(String chainIdentifier);

    List<LeafSubstructure<?>> getAllLeafSubstructures();

    Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier);

    List<AminoAcid> getAllAminoAcids();

    Optional<AminoAcid> getAminoAcid(LeafIdentifier leafIdentifier);

    List<Nucleotide> getAllNucleotides();

    Optional<Nucleotide> getNucleotide(LeafIdentifier leafIdentifier);

    List<Ligand> getAllLigands();

    Optional<Ligand> getLigand(LeafIdentifier leafIdentifier);

    List<Atom> getAllAtoms();

    Optional<Atom> getAtom(int atomIdentifier);

}
