package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.LigandFamily;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.parser.pdb.ligands.LigandParserService;
import bio.singa.structure.parser.pdb.structures.LocalCIFRepository;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
import bio.singa.structure.parser.pdb.structures.iterators.StructureIterator;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.structure.model.oak.LeafIdentifier.*;
import static bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton.AssignedFamily.MODIFIED_AMINO_ACID;
import static bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton.AssignedFamily.MODIFIED_NUCLEOTIDE;

public class LeafSubstructureBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LeafSubstructureBuilder.class);

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

        AtomStep identifier(LeafIdentifier identifier);

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

        BuildStep atoms(Set<OakAtom> atoms);

    }

    public interface BuildStep {

        OakLeafSubstructure<?> build();

    }

    public static class GeneralLeafSubstructureBuilder implements ConsecutivePartStep, NameStep, IdentifierStep, IdentifierModelStep, IdentifierChainStep, IdentifierSerialStep, AtomStep, BuildStep {

        private boolean isInConsecutivePart;

        private StructuralFamily<?> family;
        private boolean isModified;
        private String name;

        private LeafIdentifier identifier;
        private String pdbIdentifier = DEFAULT_PDB_IDENTIFIER;
        private int model = DEFAULT_MODEL_IDENTIFIER;
        private String chain = DEFAULT_CHAIN_IDENTIFIER;
        private int serial;
        private char insertionCode = DEFAULT_INSERTION_CODE;

        private Set<OakAtom> atomSet;
        private Map<String, OakAtom> atomMap;

        private final StructureParserOptions options;
        private final LocalCIFRepository cifRepository;
        private final Map<String, LeafSkeleton> leafSkeletons;
        private LeafSkeleton leafSkeleton;


        public GeneralLeafSubstructureBuilder(StructureIterator iterator) {
            options = iterator.getReducer().getOptions();
            leafSkeletons = iterator.getSkeletons();
            cifRepository = iterator.getReducer().getLocalCIFRepository();
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
            Optional<AminoAcidFamily> aminoAcidFamilyOptional = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(threeLetterCode);
            if (aminoAcidFamilyOptional.isPresent()) {
                family = aminoAcidFamilyOptional.get();
                return true;
            }
            return false;
        }

        private boolean tryNucleotide(String threeLetterCode) {
            Optional<NucleotideFamily> nucleotideFamilyOptional = NucleotideFamily.getNucleotideByThreeLetterCode(threeLetterCode);
            if (nucleotideFamilyOptional.isPresent()) {
                family = nucleotideFamilyOptional.get();
                return true;
            }
            return false;
        }

        private void tryLigand(String threeLetterCode) {
            // check option
            if (options.isRetrievingLigandInformation()) {
                // check cache
                if (!leafSkeletons.containsKey(threeLetterCode)) {
                    // check local repo
                    if (cifRepository != null) {
                        // use local
                        leafSkeleton = LigandParserService.parseLeafSkeleton(threeLetterCode, cifRepository);
                    } else {
                        // use online
                        leafSkeleton = LigandParserService.parseLeafSkeleton(threeLetterCode);
                    }
                    // cache
                    leafSkeletons.put(threeLetterCode, leafSkeleton);
                } else {
                    // get from cache
                    leafSkeleton = leafSkeletons.get(threeLetterCode);
                }
            }
            // skeleton available
            if (leafSkeleton != null && isInConsecutivePart) {
                // determine modifications
                if (leafSkeleton.getAssignedFamily().equals(MODIFIED_AMINO_ACID)) {
                    family = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(leafSkeleton.getParent())
                            .orElse(AminoAcidFamily.UNKNOWN);
                    isModified = true;
                    return;
                } else if (leafSkeleton.getAssignedFamily().equals(MODIFIED_NUCLEOTIDE)) {
                    family = NucleotideFamily.getNucleotideByThreeLetterCode(leafSkeleton.getParent())
                            .orElse(NucleotideFamily.UNKNOWN);
                    isModified = true;
                    return;
                }
            }
            // use default
            family = new LigandFamily("?", threeLetterCode);
        }

        @Override
        public AtomStep identifier(LeafIdentifier identifier) {
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
            identifier = new LeafIdentifier(pdbIdentifier, model, chain, serial, insertionCode);
        }

        @Override
        public BuildStep atoms(Set<OakAtom> atoms) {
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
                    .map(OakAtom::getAtomName)
                    .distinct()
                    .count();
            return atomSet.size() == numberOfDistinctAtomNames;
        }

        private void prepareAtomMap() {
            atomMap = new HashMap<>();
            for (OakAtom oakAtom : atomSet) {
                atomMap.put(oakAtom.getAtomName(), oakAtom);
            }
        }

        @Override
        public OakLeafSubstructure<?> build() {
            // check whether atom names are distinct
            if (atomMap != null) {
                // atom names are unique
                if (family instanceof AminoAcidFamily) {
                    OakAminoAcid aminoAcid = new OakAminoAcid(identifier, (AminoAcidFamily) family);
                    atomSet.forEach(aminoAcid::addAtom);
                    if (!isModified) {
                        LeafSubstructureFactory.connectAminoAcid(aminoAcid, atomMap);
                    } else {
                        aminoAcid.setDivergingThreeLetterCode(name);
                        if (leafSkeleton != null && leafSkeleton.hasBonds()) {
                            leafSkeleton.connect(aminoAcid, atomMap);
                        }
                    }
                    return aminoAcid;
                } else if (family instanceof NucleotideFamily) {
                    OakNucleotide nucleotide = new OakNucleotide(identifier, (NucleotideFamily) family);
                    atomSet.forEach(nucleotide::addAtom);
                    if (!isModified) {
                        LeafSubstructureFactory.connectNucleotide(nucleotide, atomMap);
                    } else {
                        nucleotide.setDivergingThreeLetterCode(name);
                        if (leafSkeleton != null && leafSkeleton.hasBonds()) {
                            leafSkeleton.connect(nucleotide, atomMap);
                        }
                    }
                    return nucleotide;
                } else {
                    OakLigand ligand = new OakLigand(identifier, (LigandFamily) family);
                    atomSet.forEach(ligand::addAtom);
                    if (leafSkeleton != null) {
                        if (leafSkeleton.hasBonds()) {
                            leafSkeleton.connect(ligand, atomMap);
                        }
                        ligand.setName(leafSkeleton.getName());
                    }
                    return ligand;
                }
            } else {
                // at least one duplicated atom name
                // connections need to be assigned via CONECT records
                if (family instanceof AminoAcidFamily) {
                    OakAminoAcid aminoAcid = new OakAminoAcid(identifier, (AminoAcidFamily) family);
                    atomSet.forEach(aminoAcid::addAtom);
                    return aminoAcid;
                } else if (family instanceof NucleotideFamily) {
                    OakNucleotide nucleotide = new OakNucleotide(identifier, (NucleotideFamily) family);
                    atomSet.forEach(nucleotide::addAtom);
                    return nucleotide;
                } else {
                    OakLigand ligand = new OakLigand(identifier, (LigandFamily) family);
                    atomSet.forEach(ligand::addAtom);
                    if (leafSkeleton != null) {
                        ligand.setName(leafSkeleton.getName());
                    }
                    return ligand;
                }
            }
        }
    }

}
