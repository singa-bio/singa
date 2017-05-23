package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.Structure;

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

    public static ChainStep selectFrom(StructuralModel structuralModel) {
        return new Selector(structuralModel);
    }

    public static ResidueStep selectFrom(Chain chain) {
        return new Selector(chain);
    }

    public static ResidueStep selectFrom(StructuralMotif structuralMotif) {
        return new Selector(structuralMotif);
    }

    public static AtomStep selectFrom(AminoAcid aminoAcid) {
        return new Selector(aminoAcid);
    }

    public static AtomStep selectFrom(Nucleotide nucleotide) {
        return new Selector(nucleotide);
    }

    public static AtomStep selectFrom(AtomContainer atomContainer) {
        return new Selector(atomContainer);
    }

    public interface StructuralModelStep {
        ChainStep model(int structuralModelId);
    }

    public interface ChainStep {
        ResidueStep chain(int chainId);

        ResidueStep chain(String chainIdentifier);

        StructuralModel selectModel();
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
        AtomContainer selectAtomContainer();
    }

    public interface AtomStep {
        SelectStep atom(int atomId);
    }

    public interface SelectStep {
        Atom selectAtom();
    }

    public static class Selector implements StructuralModelStep, ChainStep, ResidueStep, AtomStep, AminoAcidAtomStep, NucleotideAtomStep, AtomContainerAtomStep, SelectStep {

        private Structure structure;
        private StructuralModel structuralModel;
        private Chain chain;
        private StructuralMotif structuralMotif;
        private AminoAcid aminoAcid;
        private Nucleotide nucleotide;
        private AtomContainer<?> atomContainer;
        private Atom atom;

        public Selector(Structure structure) {
            this.structure = structure;
        }

        public Selector(StructuralModel structuralModel) {
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

        public Selector(AtomContainer atomContainer) {
            this.atomContainer = atomContainer;
        }

        public Selector(StructuralMotif structuralMotif) {
            this.structuralMotif = structuralMotif;
        }

        @Override
        public ChainStep model(int identifier) {
            this.structuralModel = this.structure.getAllModels().stream()
                    .filter(structuralModel -> structuralModel.getIdentifier() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no structural model with ID " + identifier));
            return this;
        }

        @Override
        public StructuralModel selectModel() {
            return this.structuralModel;
        }

        @Override
        public ResidueStep chain(int identifier) {
            this.chain = this.structuralModel.getAllChains().stream()
                    .filter(chain -> chain.getIdentifier() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no chain with index " + identifier));
            return this;
        }

        @Override
        public ResidueStep chain(String chainIdentifier) {
            this.chain = this.structuralModel.getAllChains().stream()
                    .filter(chain -> chain.getChainIdentifier().equals(chainIdentifier))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no chain with ID " + chainIdentifier));
            return this;
        }

        @Override
        public Chain selectChain() {
            return this.chain;
        }

        @Override
        public AminoAcidAtomStep aminoAcid(int identifier) {
            this.aminoAcid = this.chain.getLeafSubstructures().stream()
                    .filter(AminoAcid.class::isInstance)
                    .map(AminoAcid.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no amino acid with ID " + identifier));
            return this;
        }

        @Override
        public AminoAcid selectAminoAcid() {
            return this.aminoAcid;
        }

        @Override
        public NucleotideAtomStep nucleotide(int identifier) {
            this.nucleotide = this.chain.getLeafSubstructures().stream()
                    .filter(Nucleotide.class::isInstance)
                    .map(Nucleotide.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no nucleotide with ID " + identifier));
            return this;
        }

        @Override
        public Nucleotide selectNucleotide() {
            return this.nucleotide;
        }

        @Override
        public AtomContainerAtomStep atomContainer(int identifier) {
            this.atomContainer = this.chain.getLeafSubstructures().stream()
                    .filter(AtomContainer.class::isInstance)
                    .map(AtomContainer.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier() == identifier)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no atom container with ID " + identifier));
            return this;
        }

        @Override
        public AtomContainer selectAtomContainer() {
            return this.atomContainer;
        }

        @Override
        public SelectStep atom(int identifier) {
            if (this.aminoAcid != null) {
                this.atom = getAtomFromLeafSubstructure(this.aminoAcid, identifier);
            }
            if (this.nucleotide != null) {
                this.atom = getAtomFromLeafSubstructure(this.nucleotide, identifier);
            }
            if (this.atomContainer != null) {
                this.atom = getAtomFromLeafSubstructure(this.atomContainer, identifier);
            }
            return this;
        }

        private Atom getAtomFromLeafSubstructure(LeafSubstructure<?, ?> leafSubstructure, int atomId) {
            return leafSubstructure.getAllAtoms().stream()
                    .filter(atom -> atom.getIdentifier() == atomId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no atom with ID " + atomId));
        }

        @Override
        public Atom selectAtom() {
            return this.atom;
        }
    }
}
