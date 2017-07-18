package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.parser.pdb.ligands.LigandParserService;
import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.*;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.LeafFactory;
import de.bioforscher.singa.chemistry.physical.families.LigandFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.chemistry.physical.model.UniqueAtomIdentifer;
import de.bioforscher.singa.core.identifier.PDBIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class StructureCollector {

    private static final Logger logger = LoggerFactory.getLogger(StructureCollector.class);

    private String currentPDB = "0000";
    private StringBuilder titleBuilder = new StringBuilder();
    private int currentModel = 0;

    private Map<UniqueAtomIdentifer, Atom> atoms;
    private Map<LeafIdentifier, String> leafNames;

    // leaves that are hetatoms
    private Set<LeafIdentifier> hetAtoms;
    // leaves that are not part of the consecutive chain have to be noted
    private Set<LeafIdentifier> notInConsecutiveChain;
    private String currentChain;
    private Set<String> closedChains;

    private ContentTreeNode contentTree;

    private StructureParser.Reducer reducer;
    private List<String> pdbLines;

    public StructureCollector(List<String> pdbLines, StructureParser.Reducer reducer) {
        this.reducer = reducer;
        this.pdbLines = pdbLines;
        this.atoms = new HashMap<>();
        this.leafNames = new TreeMap<>();
        this.hetAtoms = new HashSet<>();
        this.notInConsecutiveChain = new HashSet<>();
        this.closedChains = new HashSet<>();
    }

    static Structure parse(List<String> pdbLines, StructureParser.Reducer reducer) throws StructureParserException {
        StructureCollector collector = new StructureCollector(pdbLines, reducer);
        collector.reduceLines();
        return collector.collectStructure();
    }

    private void reduceLines() throws StructureParserException {
        String firstLine = this.pdbLines.get(0);
        // parse meta information
        if (this.reducer.options.isInferringIdentifierFromFileName()) {
            String currentSource = this.reducer.sourceSelector.contentIterator.getCurrentSource();
            String identifier = PDBIdentifier.extractFirst(currentSource);
            if (identifier != null) {
                this.currentPDB = identifier;
            }
        } else {
            if (HeaderToken.RECORD_PATTERN.matcher(firstLine).matches()) {
                this.currentPDB = HeaderToken.ID_CODE.extract(firstLine);
            }
        }
        getTitle();
        if (this.reducer.parseMapping) {
            this.reducer.updatePdbIdentifer();
            this.reducer.updateChainIdentifier();
            reduceToChain(this.reducer.chainIdentifier);
            logger.info("Parsing structure {} chainIdentifier {}", this.reducer.pdbIdentifier, this.reducer.chainIdentifier);
        } else {
            if (!this.reducer.allModels) {
                // parse only specific model
                // reduce lines to specific model
                reduceToModel(this.reducer.modelIdentifier);
            }
            if (!this.reducer.allChains) {
                // parse only specific chainIdentifier
                // reduce lines to specific chainIdentifier
                reduceToChain(this.reducer.chainIdentifier);
            }
        }
    }

    private void getTitle() {
        if (this.reducer.options.isInferringTitleFromFileName()) {
            this.titleBuilder.append(this.reducer.sourceSelector.contentIterator.getCurrentSource());
        } else {
            boolean titleFound = false;
            for (String currentLine : this.pdbLines) {
                // check if title line
                if (TitleToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                    // if this is the first time such a line occurs, the title was found
                    if (!titleFound) {
                        titleFound = true;
                    }
                    // append title
                    this.titleBuilder.append(TitleToken.TEXT.extract(currentLine));
                } else {
                    // if title has been found and a line with another content is found
                    if (titleFound) {
                        // quit parsing title
                        return;
                    }
                }
            }
        }
    }

    private void reduceToModel(int modelIdentifier) {
        List<String> reducedList = new ArrayList<>();
        boolean collectLines = false;
        // for each line
        for (String currentLine : this.pdbLines) {
            // check if the correct model has begun
            if (ModelToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                int currentModel = Integer.valueOf(ModelToken.MODEL_SERIAL.extract(currentLine));
                // turn on collection of lines
                if (currentModel == modelIdentifier) {
                    this.currentModel = currentModel;
                    collectLines = true;
                    continue;
                }
            }
            // check if the terminator is hit prevent further parsing
            if (collectLines && TerminatorTokens.MODEL_TERMINATOR.matcher(currentLine).matches()) {
                break;
            }
            // collect lines if we are in the correct model
            if (collectLines) {
                reducedList.add(currentLine);
            }
        }
        this.pdbLines = reducedList;
    }

    private void reduceToChain(String chainIdentifier) {
        List<String> reducedList = new ArrayList<>();
        // for each line
        for (String currentLine : this.pdbLines) {
            // check if this is a atom line
            if (AtomToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                String currentChain = AtomToken.CHAIN_IDENTIFIER.extract(currentLine);
                // collect line if it has the correct chainIdentifier
                if (currentChain.equals(chainIdentifier)) {
                    reducedList.add(currentLine);
                }
            } else if (ModelToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                // keel lines that indicate models
                reducedList.add(currentLine);
            } else if (ChainTerminatorToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                // keel lines that indicate models
                reducedList.add(currentLine);
            }
        }
        this.pdbLines = reducedList;
    }

    private Structure collectStructure() {
        collectAtomInformation();
        createContentTree();

        Structure structure = new Structure();
        structure.setPdbIdentifier(this.contentTree.getIdentifier());
        structure.setTitle(this.titleBuilder.toString());

        logger.debug("creating structure");
        int chainGraphId = 0;
        for (ContentTreeNode modelNode : this.contentTree.getNodesFromLevel(ContentTreeNode.StructureLevel.MODEL)) {
            logger.debug("collecting chains for model {}", modelNode.getIdentifier());
            StructuralModel model = new StructuralModel(Integer.valueOf(modelNode.getIdentifier()));
            for (ContentTreeNode chainNode : modelNode.getNodesFromLevel(ContentTreeNode.StructureLevel.CHAIN)) {
                logger.trace("collecting leafs for chainIdentifier {}", chainNode.getIdentifier());
                Chain chain = new Chain(chainNode.getIdentifier());
                for (ContentTreeNode leafNode : chainNode.getNodesFromLevel(ContentTreeNode.StructureLevel.LEAF)) {
                    LeafSubstructure<?, ?> leafSubstructure = assignLeaf(leafNode, Integer.valueOf(modelNode.getIdentifier()), chainNode.getIdentifier());
                    if (this.hetAtoms.contains(leafSubstructure.getIdentifier())) {
                        leafSubstructure.setAnnotatedAsHetAtom(true);
                    }
                    if (this.notInConsecutiveChain.contains(leafSubstructure.getIdentifier())) {
                        chain.addSubstructure(leafSubstructure);
                    } else {
                        chain.addToConsecutivePart(leafSubstructure);
                    }
                }
                model.addSubstructure(chain);
            }
            structure.addBranchSubstructure(model);
        }
        if (this.reducer.options.isCreatingEdges()) {
            structure.getAllChains().forEach(Chain::connectChainBackbone);
        }
        return structure;
    }

    private void collectAtomInformation() {
        logger.debug("collecting information from {} PDB lines", this.pdbLines.size());
        for (String currentLine : this.pdbLines) {
            String currentRecordType = AtomToken.RECORD_TYPE.extract(currentLine);
            if (AtomToken.RECORD_PATTERN.matcher(currentRecordType).matches()) {
                UniqueAtomIdentifer identifier = createUniqueAtomIdentifier(currentLine);
                this.atoms.put(identifier, AtomToken.assembleAtom(currentLine));
                LeafIdentifier leafIdentifier = new LeafIdentifier(identifier.getPdbIdentifier(),
                        identifier.getModelIdentifier(), identifier.getChainIdentifier(),
                        identifier.getLeafIdentifer(), identifier.getLeafInsertionCode());
                this.currentChain = leafIdentifier.getChainIdentifier();
                if (currentRecordType.equals("HETATM")) {
                    this.hetAtoms.add(leafIdentifier);
                }
                // add everything before termination record to consecutive chain
                if (this.closedChains.contains(this.currentModel + "-" + this.currentChain)) {
                    this.notInConsecutiveChain.add(leafIdentifier);
                }
                this.leafNames.put(leafIdentifier, AtomToken.RESIDUE_NAME.extract(currentLine));
            } else if (currentRecordType.equals("MODEL")) {
                this.currentModel = Integer.valueOf(ModelToken.MODEL_SERIAL.extract(currentLine));
            } else if (currentRecordType.equals("TER")) {
                this.closedChains.add(this.currentModel + "-" + this.currentChain);
            }
        }
    }

    private void createContentTree() {
        logger.debug("creating content tree");
        this.contentTree = new ContentTreeNode(this.currentPDB, ContentTreeNode.StructureLevel.STRUCTURE);
        this.atoms.forEach((identifer, atom) -> this.contentTree.appendAtom(atom, identifer));
        if (this.atoms.isEmpty()) {
            throw new StructureParserException("could not reduce PDB-ID according to " + this.reducer);
        }
    }

    private UniqueAtomIdentifer createUniqueAtomIdentifier(String atomLine) {
        int atomSerial = Integer.valueOf(AtomToken.ATOM_SERIAL.extract(atomLine));
        String chain = AtomToken.CHAIN_IDENTIFIER.extract(atomLine);
        int leaf = Integer.valueOf(AtomToken.RESIDUE_SERIAL.extract(atomLine));
        char insertionCode = AtomToken.RESIDUE_INSERTION.extract(atomLine).charAt(0);
        return new UniqueAtomIdentifer(this.currentPDB, this.currentModel, chain, leaf, insertionCode, atomSerial);
    }

    private LeafSubstructure<?, ?> assignLeaf(ContentTreeNode leafNode, int modelIdentifier, String chainIdentifer) {
        // generate leaf pdbIdentifier
        LeafIdentifier leafIdentifier = new LeafIdentifier(this.currentPDB, modelIdentifier, chainIdentifer, Integer.valueOf(leafNode.getIdentifier()), leafNode.getInsertionCode());
        // get leaf name for leaf identifer
        String leafName = this.leafNames.get(leafIdentifier);
        // get atoms of this leaf
        Map<String, Atom> atoms = leafNode.getAtomMap();
        // log it
        logger.trace("creating leaf {}:{} for chainIdentifier {}", leafNode.getIdentifier(), leafName, chainIdentifer);
        // find most suitable implementation
        if (isPlainAminoAcid(leafName)) {
            AminoAcidFamily family = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(leafName).get();
            return createAminoAcid(leafIdentifier, family, atoms);
        }
        if (isPlainNucleotide(leafName)) {
            NucleotideFamily family = NucleotideFamily.getNucleotideByThreeLetterCode(leafName).get();
            return createNucleotide(leafIdentifier, family, atoms);
        }
        if (this.reducer.options.isRetrievingLigandInformation()) {
            return createLeafWithAdditionalInformation(leafIdentifier, leafName, atoms);
        } else {
            return createLeafWithoutAdditionalInformation(leafIdentifier, leafName, atoms);
        }
    }

    private boolean isPlainAminoAcid(String leafName) {
        return AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(leafName).isPresent();
    }

    private boolean isPlainNucleotide(String leafName) {
        return NucleotideFamily.getNucleotideByThreeLetterCode(leafName).isPresent();
    }

    private AminoAcid createAminoAcid(LeafIdentifier identifier, AminoAcidFamily family, Map<String, Atom> atoms) {
        return LeafFactory.createAminoAcidFromAtoms(identifier, family, atoms, this.reducer.options);
    }

    private Nucleotide createNucleotide(LeafIdentifier identifier, NucleotideFamily family, Map<String, Atom> atoms) {
        return LeafFactory.createNucleotideFromAtoms(identifier, family, atoms, this.reducer.options);
    }

    private LeafSubstructure<?, ?> createLeafWithoutAdditionalInformation(LeafIdentifier identifier, String leafName, Map<String, Atom> atoms) {
        LeafSubstructure<?, ?> substructure = new AtomContainer<>(identifier, new LigandFamily("?", leafName));
        atoms.values().forEach(substructure::addNode);
        return substructure;
    }

    private LeafSubstructure<?, ?> createLeafWithAdditionalInformation(LeafIdentifier identifier, String leafName, Map<String, Atom> atoms) {
        LeafSkeleton leafSkeleton;
        if (!this.reducer.skeletons.containsKey(leafName)) {
            leafSkeleton = LigandParserService.parseLeafSkeleton(leafName);
            this.reducer.skeletons.put(leafName, leafSkeleton);
        } else {
            leafSkeleton = this.reducer.skeletons.get(leafName);
        }
        return leafSkeleton.toRealLeafSubStructure(identifier, atoms);
    }


}
