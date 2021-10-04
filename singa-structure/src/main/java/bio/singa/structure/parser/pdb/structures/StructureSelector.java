package bio.singa.structure.parser.pdb.structures;


import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.general.StructuralMotif;

import java.util.NoSuchElementException;

/**
 * @author fk
 */
public class StructureSelector {

    private StructureSelector() {

    }

    public static StructuralModelStep selectFrom(Structure structure) {
        return new Selector(structure);
    }

    public static ChainStep selectFrom(Model structuralModel) {
        return new Selector(structuralModel);
    }

    public static ResidueStep selectFrom(Chain chain) {
        return new Selector(chain);
    }

    public static StructuralModelStep selectFrom(StructuralMotif structuralMotif) {
        return new Selector(structuralMotif);
    }

    public static AtomStep selectFrom(AminoAcid aminoAcid) {
        return new Selector(aminoAcid);
    }

    public static AtomStep selectFrom(Nucleotide nucleotide) {
        return new Selector(nucleotide);
    }

    public static AtomStep selectFrom(Ligand atomContainer) {
        return new Selector(atomContainer);
    }

    public interface StructuralModelStep {
        ChainStep model(int structuralModelId);
    }

    public interface ChainStep {

        ResidueStep chain(String chainIdentifier);

        Model selectModel();
    }

    public interface ResidueStep {
        AminoAcidAtomStep aminoAcid(int aminoAcidId);

        NucleotideAtomStep nucleotide(int nucleotideId);

        AtomContainerAtomStep atomContainer(int ligandId);

        Chain selectChain();
    }

    public interface AminoAcidAtomStep extends AtomStep {
        AminoAcid selectAminoAcid();
    }

    public interface NucleotideAtomStep extends AtomStep {
        Nucleotide selectNucleotide();
    }

    public interface AtomContainerAtomStep extends AtomStep {
        Ligand selectAtomContainer();
    }

    public interface AtomStep {
        SelectStep atom(int atomId);
    }

    public interface SelectStep {
        Atom selectAtom();
    }

    public static class Selector implements StructuralModelStep, ChainStep, ResidueStep, AtomStep, AminoAcidAtomStep, NucleotideAtomStep, AtomContainerAtomStep, SelectStep {

        private Structure structure;
        private Model structuralModel;
        private Chain chain;
        private StructuralMotif structuralMotif;
        private AminoAcid aminoAcid;
        private Nucleotide nucleotide;
        private Ligand atomContainer;
        private Atom atom;

        public Selector(Structure structure) {
            this.structure = structure;
        }

        public Selector(Model structuralModel) {
            this.structuralModel = structuralModel;
        }

        public Selector(Chain chain) {
            this.chain = chain;
        }

        public Selector(AminoAcid aminoAcid) {
            this.aminoAcid = aminoAcid;
        }

        public Selector(Nucleotide nucleotide) {
            this.nucleotide = nucleotide;
        }

        public Selector(Ligand atomContainer) {
            this.atomContainer = atomContainer;
        }

        // FIXME the selector is currently malfunctioning for structural motifs
        public Selector(StructuralMotif structuralMotif) {
            this.structuralMotif = structuralMotif;
        }

        @Override
        public ChainStep model(int identifier) {
            structuralModel = structure.getAllModels().stream()
                    .filter(structuralModel -> structuralModel.getModelIdentifier() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no structural model with ID " + identifier));
            return this;
        }

        @Override
        public Model selectModel() {
            return structuralModel;
        }

        @Override
        public ResidueStep chain(String identifier) {
            chain = structuralModel.getAllChains().stream()
                    .filter(chain -> chain.getChainIdentifier().equals(identifier))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no chainIdentifier with index " + identifier));
            return this;
        }

        @Override
        public Chain selectChain() {
            return chain;
        }

        @Override
        public AminoAcidAtomStep aminoAcid(int identifier) {
            aminoAcid = chain.getAllLeafSubstructures().stream()
                    .filter(AminoAcid.class::isInstance)
                    .map(AminoAcid.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no amino acid with ID " + identifier));
            return this;
        }

        @Override
        public AminoAcid selectAminoAcid() {
            return aminoAcid;
        }

        @Override
        public NucleotideAtomStep nucleotide(int identifier) {
            nucleotide = chain.getAllLeafSubstructures().stream()
                    .filter(Nucleotide.class::isInstance)
                    .map(Nucleotide.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no nucleotide with ID " + identifier));
            return this;
        }

        @Override
        public Nucleotide selectNucleotide() {
            return nucleotide;
        }

        @Override
        public AtomContainerAtomStep atomContainer(int identifier) {
            atomContainer = chain.getAllLeafSubstructures().stream()
                    .filter(Ligand.class::isInstance)
                    .map(Ligand.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no atom container with ID " + identifier));
            return this;
        }

        @Override
        public Ligand selectAtomContainer() {
            return atomContainer;
        }

        @Override
        public SelectStep atom(int identifier) {
            if (aminoAcid != null) {
                atom = getAtomFromLeafSubstructure(aminoAcid, identifier);
            }
            if (nucleotide != null) {
                atom = getAtomFromLeafSubstructure(nucleotide, identifier);
            }
            if (atomContainer != null) {
                atom = getAtomFromLeafSubstructure(atomContainer, identifier);
            }
            return this;
        }

        private Atom getAtomFromLeafSubstructure(LeafSubstructure leafSubstructure, int atomId) {
            return leafSubstructure.getAllAtoms().stream()
                    .filter(atom -> atom.getAtomIdentifier() == atomId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no atom with ID " + atomId));
        }

        @Override
        public Atom selectAtom() {
            return atom;
        }
    }
}
