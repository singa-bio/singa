package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.*;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public class MmtfStructure implements Structure {

    private StructureDataInterface data;

    public MmtfStructure(StructureDataInterface data) {
        this.data = data;
    }

    @Override
    public String getPdbIdentifier() {
        return data.getStructureId();
    }

    @Override
    public String getTitle() {
        return data.getTitle();
    }

    @Override
    public List<Model> getAllModels() {
        List<Model> models = new ArrayList<>();
        for (int modelIdentifier = 0; modelIdentifier < data.getNumModels(); modelIdentifier++) {
            models.add(new MmtfModel(data, modelIdentifier));
        }
        return models;
    }

    @Override
    public Model getFirstModel() {
        return new MmtfModel(data, 0);
    }

    @Override
    public Optional<Model> getModel(int modelIdentifier) {
        try {
            return Optional.of(new MmtfModel(data, modelIdentifier));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> chains = new ArrayList<>();
        List<Model> allModels = getAllModels();
        for (Model model : allModels) {
            chains.addAll(model.getAllChains());
        }
        return chains;
    }

    @Override
    public Chain getFirstChain() {
        return getFirstModel().getFirstChain();
    }

    @Override
    public Optional<Chain> getChain(int modelIdentifier, String chainIdentifier) {
        Optional<Model> modelOptional = getModel(modelIdentifier);
        if (!modelOptional.isPresent()) {
            return Optional.empty();
        }
        return modelOptional.get().getChain(chainIdentifier);
    }

    @Override
    public List<LeafSubstructure<?>> getAllLeafSubstructures() {
        List<LeafSubstructure<?>> leafSubstructures = new ArrayList<>();
        List<Chain> allChains = getAllChains();
        for (Chain chain : allChains) {
            leafSubstructures.addAll(chain.getAllLeafSubstructures());
        }
        return leafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        Optional<Chain> chainOptional = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        if (!chainOptional.isPresent()) {
            return Optional.empty();
        }
        return chainOptional.get().getLeafSubstructure(leafIdentifier);
    }

    @Override
    public List<AminoAcid> getAllAminoAcids() {
        List<AminoAcid> aminoAcids = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
                if (leafSubstructure instanceof  AminoAcid) {
                    aminoAcids.add((AminoAcid) leafSubstructure);
                }
        }
        return aminoAcids;
    }

    @Override
    public Optional<AminoAcid> getAminoAcid(LeafIdentifier leafIdentifier) {
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

    @Override
    public List<Nucleotide> getAllNucleotides() {
        List<Nucleotide> nucleotides = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof  Nucleotide) {
                nucleotides.add((Nucleotide) leafSubstructure);
            }
        }
        return nucleotides;
    }

    @Override
    public Optional<Nucleotide> getNucleotide(LeafIdentifier leafIdentifier) {
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

    @Override
    public List<Ligand> getAllLigands() {
        List<Ligand> ligands = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            if (leafSubstructure instanceof  Ligand) {
                ligands.add((Ligand) leafSubstructure);
            }
        }
        return ligands;
    }

    @Override
    public Optional<Ligand> getLigand(LeafIdentifier leafIdentifier) {
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

    @Override
    public List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        for (LeafSubstructure<?> leafSubstructure : getAllLeafSubstructures()) {
            atoms.addAll(leafSubstructure.getAllAtoms());
        }
        return atoms;
    }

    @Override
    public Optional<Atom> getAtom(int atomIdentifier) {
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
