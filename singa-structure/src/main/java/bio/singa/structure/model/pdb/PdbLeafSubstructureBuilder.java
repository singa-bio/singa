package bio.singa.structure.model.pdb;

import bio.singa.structure.io.ccd.LeafSkeletonFactory;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.io.general.iterators.StructureIterator;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.LeafSkeleton;
import bio.singa.structure.model.interfaces.LigandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static bio.singa.structure.model.pdb.PdbLeafIdentifier.*;

public class PdbLeafSubstructureBuilder {

    private static final Logger logger = LoggerFactory.getLogger(PdbLeafSubstructureBuilder.class);

    public static ConsecutivePartStep create(StructureIterator iterator) {
        return new GeneralLeafSubstructureBuilder(iterator);
    }

    public interface ConsecutivePartStep {

        NameStep inConsecutivePart(boolean isInConsecutivePart);

    }

    public interface NameStep {

        IdentifierStep name(String threeLetterCode);

    }

    public interface IdentifierStep {

        AtomStep identifier(PdbLeafIdentifier identifier);

        IdentifierModelStep pdb(String pdbIdentifier);

    }

    public interface IdentifierModelStep extends IdentifierChainStep {

        IdentifierChainStep model(int modelIdentifier);

    }

    public interface IdentifierChainStep {

        IdentifierSerialStep chain(String chainIdentifier);

    }

    public interface IdentifierSerialStep {

        AtomStep serial(String serialWithInsertionCode);

        AtomStep serial(int serialWithoutInsertionCode);

    }

    public interface AtomStep {

        BuildStep atoms(Set<PdbAtom> atoms);

    }

    public interface BuildStep {

        PdbLeafSubstructure build();

    }

    public static class GeneralLeafSubstructureBuilder implements ConsecutivePartStep, NameStep, IdentifierStep, IdentifierModelStep, IdentifierChainStep, IdentifierSerialStep, AtomStep, BuildStep {

        private boolean isInConsecutivePart;

        private StructuralFamily family;
        private String name;

        private PdbLeafIdentifier identifier;
        private String pdbIdentifier = DEFAULT_PDB_IDENTIFIER;
        private int model = DEFAULT_MODEL_IDENTIFIER;
        private String chain = DEFAULT_CHAIN_IDENTIFIER;
        private int serial;
        private char insertionCode = DEFAULT_INSERTION_CODE;

        private Set<PdbAtom> atomSet;
        private Map<String, PdbAtom> atomMap;

        private final StructureParserOptions options;
        private final LeafSkeletonFactory leafSkeletonFactory;
        private LeafSkeleton leafSkeleton;

        public GeneralLeafSubstructureBuilder(StructureIterator iterator) {
            options = iterator.getOptions();
            leafSkeletonFactory = iterator.getLeafSkeletonFactory();
        }

        @Override
        public NameStep inConsecutivePart(boolean isInConsecutivePart) {
            this.isInConsecutivePart = isInConsecutivePart;
            return this;
        }

        @Override
        public IdentifierStep name(String threeLetterCode) {
            name = threeLetterCode;
            if (isInConsecutivePart) {
                if (tryAminoAcid(threeLetterCode)) {
                    return this;
                }
                if (tryNucleotide(threeLetterCode)) {
                    return this;
                }
            }
            tryLigand(threeLetterCode);
            return this;
        }

        private boolean tryAminoAcid(String threeLetterCode) {
            Optional<StructuralFamily> aminoAcidFamilyOptional = StructuralFamilies.AminoAcids.get(threeLetterCode);
            if (aminoAcidFamilyOptional.isPresent()) {
                family = aminoAcidFamilyOptional.get();
                return true;
            }
            return false;
        }

        private boolean tryNucleotide(String threeLetterCode) {
            Optional<StructuralFamily> nucleotideFamilyOptional = StructuralFamilies.Nucleotides.get(threeLetterCode);
            if (nucleotideFamilyOptional.isPresent()) {
                family = nucleotideFamilyOptional.get();
                return true;
            }
            return false;
        }

        private void tryLigand(String threeLetterCode) {
            leafSkeleton = leafSkeletonFactory.getLeafSkeleton(threeLetterCode);
            // use default
            family = leafSkeleton.getStructuralFamily();
        }

        @Override
        public AtomStep identifier(PdbLeafIdentifier identifier) {
            this.identifier = identifier;
            return this;
        }

        @Override
        public IdentifierModelStep pdb(String pdbIdentifier) {
            this.pdbIdentifier = pdbIdentifier;
            return this;
        }

        @Override
        public IdentifierChainStep model(int modelIdentifier) {
            model = modelIdentifier;
            return this;
        }

        @Override
        public IdentifierSerialStep chain(String chainIdentifier) {
            chain = chainIdentifier;
            return this;
        }

        @Override
        public AtomStep serial(String serialWithInsertionCode) {
            if (serialWithInsertionCode.substring(serialWithInsertionCode.length() - 1).matches("[A-Za-z]")) {
                insertionCode = serialWithInsertionCode.charAt(serialWithInsertionCode.length() - 1);
                serial = Integer.parseInt(serialWithInsertionCode.substring(0, serialWithInsertionCode.length() - 1));
            } else {
                serial = Integer.parseInt(serialWithInsertionCode);
            }
            createIdentifier();
            return this;
        }

        @Override
        public AtomStep serial(int serialWithoutInsertionCode) {
            serial = serialWithoutInsertionCode;
            createIdentifier();
            return this;
        }

        private void createIdentifier() {
            identifier = new PdbLeafIdentifier(pdbIdentifier, model, chain, serial, insertionCode);
        }

        @Override
        public BuildStep atoms(Set<PdbAtom> atoms) {
            if (options.isOmittingHydrogen()) {
                atomSet = atoms.stream()
                        .filter(atom -> atom.getElement().getProtonNumber() != 1)
                        .collect(Collectors.toSet());
            } else {
                atomSet = atoms;
            }
            if (atomNamesAreUnique()) {
                prepareAtomMap();
            }
            return this;
        }

        private boolean atomNamesAreUnique() {
            long numberOfDistinctAtomNames = atomSet.stream()
                    .map(PdbAtom::getAtomName)
                    .distinct()
                    .count();
            return atomSet.size() == numberOfDistinctAtomNames;
        }

        private void prepareAtomMap() {
            atomMap = new HashMap<>();
            for (PdbAtom pdbAtom : atomSet) {
                atomMap.put(pdbAtom.getAtomName(), pdbAtom);
            }
        }

        @Override
        public PdbLeafSubstructure build() {
            // check whether atom names are distinct
            if (atomMap != null) {
                // atom names are unique
                if (isAminoAcid()) {
                    PdbAminoAcid aminoAcid = new PdbAminoAcid(identifier, family);
                    atomSet.forEach(aminoAcid::addAtom);
                    if (leafSkeleton == null || leafSkeleton.getParent().isEmpty()) {
                        PdbLeafSubstructureFactory.connectAminoAcid(aminoAcid, atomMap);
                    } else {
                        aminoAcid.setDivergingThreeLetterCode(name);
                        if (leafSkeleton != null && leafSkeleton.hasBonds()) {
                            leafSkeleton.connect(aminoAcid, atomMap);
                        }
                    }
                    return aminoAcid;
                } else if (isNucleotide()) {
                    PdbNucleotide nucleotide = new PdbNucleotide(identifier, family);
                    atomSet.forEach(nucleotide::addAtom);
                    if (leafSkeleton == null || leafSkeleton.getParent().isEmpty()) {
                        PdbLeafSubstructureFactory.connectNucleotide(nucleotide, atomMap);
                    } else {
                        nucleotide.setDivergingThreeLetterCode(name);
                        if (leafSkeleton != null && leafSkeleton.hasBonds()) {
                            leafSkeleton.connect(nucleotide, atomMap);
                        }
                    }
                    return nucleotide;
                } else {
                    PdbLigand ligand = new PdbLigand(identifier, family);
                    atomSet.forEach(ligand::addAtom);
                    if (leafSkeleton != null) {
                        if (leafSkeleton.hasBonds()) {
                            leafSkeleton.connect(ligand, atomMap);
                        }
                        ligand.setName(leafSkeleton.getName());
                        ligand.setInchi(leafSkeleton.getInchi());
                    }
                    return ligand;
                }
            } else {
                // at least one duplicated atom name
                // connections need to be assigned via CONECT records
                if (isAminoAcid()) {
                    PdbAminoAcid aminoAcid = new PdbAminoAcid(identifier, family);
                    atomSet.forEach(aminoAcid::addAtom);
                    return aminoAcid;
                } else if (isNucleotide() ) {
                    PdbNucleotide nucleotide = new PdbNucleotide(identifier, family);
                    atomSet.forEach(nucleotide::addAtom);
                    return nucleotide;
                } else {
                    PdbLigand ligand = new PdbLigand(identifier, family);
                    atomSet.forEach(ligand::addAtom);
                    if (leafSkeleton != null) {
                        ligand.setName(leafSkeleton.getName());
                        ligand.setInchi(leafSkeleton.getInchi());
                    }
                    return ligand;
                }
            }
        }

        private boolean isNucleotide() {
            return StructuralFamilies.Nucleotides.isNucleotide(family) || (leafSkeleton != null && leafSkeleton.getLigandType().equals(LigandType.NUCLEIC_ACID));
        }

        private boolean isAminoAcid() {
            return StructuralFamilies.AminoAcids.isAminoAcid(family) || (leafSkeleton != null && leafSkeleton.getLigandType().equals(LigandType.PROTEIN));
        }
    }

}
