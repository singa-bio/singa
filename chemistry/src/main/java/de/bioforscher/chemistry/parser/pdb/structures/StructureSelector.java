package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.model.Structure;

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

        @Override
        public ChainStep model(int structuralModelId) {
            this.structuralModel = this.structure.getAllModels().stream()
                    .filter(structuralModel -> structuralModel.getIdentifier() == structuralModelId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no structural model with ID " + structuralModelId));
            return this;
        }

        @Override
        public StructuralModel selectModel() {
            return this.structuralModel;
        }

        @Override
        public ResidueStep chain(int chainId) {
            this.chain = this.structuralModel.getAllChains().stream()
                    .filter(chain -> chain.getIdentifier() == chainId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no chain with ID " + chainId));
            return this;
        }

        @Override
        public Chain selectChain() {
            return this.chain;
        }

        @Override
        public AminoAcidAtomStep aminoAcid(int aminoAcidId) {
            this.aminoAcid = this.chain.getLeafSubstructures().stream()
                    .filter(AminoAcid.class::isInstance)
                    .map(AminoAcid.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier() == aminoAcidId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no amino acid with ID " + aminoAcidId));
            return this;
        }

        @Override
        public AminoAcid selectAminoAcid() {
            return this.aminoAcid;
        }

        @Override
        public NucleotideAtomStep nucleotide(int nucleotideId) {
            this.nucleotide = this.chain.getLeafSubstructures().stream()
                    .filter(Nucleotide.class::isInstance)
                    .map(Nucleotide.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier() == nucleotideId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no nucleotide with ID " + nucleotideId));
            return this;
        }

        @Override
        public Nucleotide selectNucleotide() {
            return this.nucleotide;
        }

        @Override
        public AtomContainerAtomStep atomContainer(int atomContainerId) {
            this.atomContainer = this.chain.getLeafSubstructures().stream()
                    .filter(AtomContainer.class::isInstance)
                    .map(AtomContainer.class::cast)
                    .filter(leafSubstructure -> leafSubstructure.getIdentifier() == atomContainerId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no atom container with ID " + atomContainerId));
            return this;
        }

        @Override
        public AtomContainer selectAtomContainer() {
            return this.atomContainer;
        }

        @Override
        public SelectStep atom(int atomId) {
            if (this.aminoAcid != null) {
                this.atom = getAtomFromLeafSubstructure(this.aminoAcid, atomId);
            }
            if (this.nucleotide != null) {
                this.atom = getAtomFromLeafSubstructure(this.nucleotide, atomId);
            }
            if (this.atomContainer != null) {
                this.atom = getAtomFromLeafSubstructure(this.atomContainer, atomId);
            }
            return this;
        }

        private Atom getAtomFromLeafSubstructure(LeafSubstructure<?, ?> leafSubstructure, int atomId) {
            return leafSubstructure.getAllAtoms().stream()
                    .filter(atom -> atom.getIdentifier() == atomId)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("no atom container with ID " + atomId));
        }

        @Override
        public Atom selectAtom() {
            return this.atom;
        }
    }
}
