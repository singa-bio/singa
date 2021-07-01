package bio.singa.structure.parser.pdb.structures;

import bio.singa.core.utility.DoubleMatcher;
import bio.singa.core.utility.Pair;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.features.identifiers.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.*;
import bio.singa.structure.parser.pdb.structures.iterators.StructureIterator;
import bio.singa.structure.parser.pdb.structures.iterators.StructureReducer;
import bio.singa.structure.parser.pdb.structures.tokens.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;

import static bio.singa.features.identifiers.LeafIdentifier.*;

/**
 * The actual processing of pdb files. This class collects all required information form the a list of lines from a pdb
 * file.
 *
 * @author cl
 */
public class StructureCollector {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(StructureCollector.class);
    /**
     * The string builder building the title.
     */
    private final StringBuilder titleBuilder = new StringBuilder();
    /**
     * A cache of all atoms identified by unique atom identifiers.
     */
    private final Map<UniqueAtomIdentifier, OakAtom> atoms;
    /**
     * A cache of all leafs and their three letter codes.
     */
    private final Map<LeafIdentifier, String> leafCodes;
    /**
     * Remembers all leafs that have been parsed from HETATM entries.
     */
    private final Set<LeafIdentifier> hetAtoms;
    /**
     * Remembers all leafs the were part of the consecutive part of the chain.
     */
    private final Set<LeafIdentifier> notInConsecutiveChain;
    /**
     * Chains that have already been terminated by a terminate record.
     */
    private final Set<String> closedChains;
    /**
     * The reducer containing the information of what should be parsed and how it should be done.
     */
    private final StructureIterator iterator;
    /**
     * The currently parsed pdb file.
     */
    private String currentPDB = DEFAULT_PDB_IDENTIFIER;
    /**
     * The current model.
     */
    private int currentModel = DEFAULT_MODEL_IDENTIFIER;
    /**
     * The current chain.
     */
    private String currentChain = DEFAULT_CHAIN_IDENTIFIER;
    /**
     * The root node of the content tree.
     */
    private ContentTreeNode contentTree;
    /**
     * The list of relevant pdb lines.
     */
    private List<String> pdbLines;

    private List<String> linkLines;
    private List<String> connectionLines;
    private List<String> sequenceAdviceLines;

    /**
     * References Pair of ThreeLetterCode and AttributeType to AttributeValue
     */
    private Map<Pair<String>, String> ligandPropertyRemarks;

    private double resolution;

    /**
     * Creates a new structure collector to extract structural information from pdb lines and reducing information.
     *
     * @param pdbLines The lines of a pdb file.
     * @param iterator The information on what should be parsed and how it should be done.
     */
    private StructureCollector(List<String> pdbLines, StructureIterator iterator) {
        this.iterator = iterator;
        this.pdbLines = pdbLines;
        atoms = new HashMap<>();
        leafCodes = new HashMap<>();
        hetAtoms = new HashSet<>();
        notInConsecutiveChain = new HashSet<>();
        closedChains = new HashSet<>();
        linkLines = new ArrayList<>();
        connectionLines = new ArrayList<>();
        sequenceAdviceLines = new ArrayList<>();
        ligandPropertyRemarks = new HashMap<>();
    }

    /**
     * parses a structure from pdb lines and reducing information.
     *
     * @param pdbLines The lines of a pdb file.
     * @param iterator The information on what should be parsed and how it should be done.
     * @return The resulting structure.
     * @throws StructureParserException if any problem occur during parsing.
     */
    public static Structure parse(List<String> pdbLines, StructureIterator iterator) throws StructureParserException {
        StructureCollector collector = new StructureCollector(pdbLines, iterator);
        collector.reduceLines();
        return collector.collectStructure();
    }

    /**
     * Removes trailing whitespaces.
     *
     * @param source The original string.
     * @return The original string without trailing white spaces.
     */
    public static String trimEnd(String source) {
        int pos = source.length() - 1;
        while ((pos >= 0) && Character.isWhitespace(source.charAt(pos))) {
            pos--;
        }
        pos++;
        return (pos < source.length()) ? source.substring(0, pos) : source;
    }

    /**
     * Reduces the lines as described in the {@link StructureReducer}.
     *
     * @throws StructureParserException if any problem occur during reducing.
     */
    private void reduceLines() throws StructureParserException {
        String firstLine = pdbLines.get(0);
        // parse meta information
        if (iterator.getReducer().getOptions().isInferringIdentifierFromFileName()) {
            String identifier = iterator.getCurrentPdbIdentifier();
            if (identifier != null) {
                currentPDB = identifier;
            }
        } else {
            if (HeaderToken.RECORD_PATTERN.matcher(firstLine).matches()) {
                currentPDB = HeaderToken.ID_CODE.extract(firstLine);
                if (currentPDB.isEmpty()) {
                    currentPDB = DEFAULT_PDB_IDENTIFIER;
                }
            }
        }
        getTitle();
        // TODO add option for INCHI from Remark
        getAdditionalInformation();
        if (iterator.getReducer().getOptions().enforceConnection()) {
            getLinks();
            getConnections();
        }
        if (iterator.hasChain()) {
            reduceToChain(iterator.getCurrentChainIdentifier());
            logger.info("Parsing structure {} chain {}", iterator.getCurrentPdbIdentifier(), iterator.getCurrentChainIdentifier());
        } else {
            if (iterator.getReducer().isReducingModels()) {
                // parse only specific model
                // reduce lines to specific model
                reduceToModel(iterator.getReducer().getModelIdentifier());
            }
            if (iterator.getReducer().isReducingChains()) {
                // parse only specific chainIdentifier
                // reduce lines to specific chainIdentifier
                reduceToChain(iterator.getReducer().getChainIdentifier());
            }
        }
    }

    /**
     * Extracts the title from tha pdb header.
     * FIXME: this requires an extra iteration over all pdb lines
     */
    private void getTitle() {
        if (iterator.getReducer().getOptions().isInferringTitleFromFileName()) {
            String currentSource = iterator.getCurrentSource();
            titleBuilder.append(Paths.get(currentSource).getFileName().toString().replaceFirst("[.][^.]+$", ""));
        } else {
            boolean titleFound = false;
            for (String currentLine : pdbLines) {
                // check if title line
                if (TitleToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                    // if this is the first time such a line occurs, the title was found
                    if (!titleFound) {
                        titleFound = true;
                    }
                    // append title
                    titleBuilder.append(trimEnd(TitleToken.TEXT.extract(currentLine)));
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

    private void getAdditionalInformation() {
        String refThreeLetterCode = null;
        String refAttribute = null;
        StringBuilder refValue = new StringBuilder();
        for (String currentLine : pdbLines) {
            // REMARK 80
            if (Remark80Token.REMARK_80.matcher(currentLine).matches()) {
                // if this is the first time such a line occurs, the title was found
                String content = Remark80Token.REMARK_CONTENT.extract(currentLine);
                if (content.trim().isEmpty() && refThreeLetterCode == null) {
                    continue;
                }
                if (refThreeLetterCode == null) {
                    refThreeLetterCode = trimEnd(content);
                    continue;
                }
                if (refAttribute == null) {
                    refAttribute = trimEnd(content);
                    continue;
                }
                if (content.isEmpty() && !refThreeLetterCode.isEmpty()) {
                    Pair<String> keyPair = new Pair<>(refThreeLetterCode, refAttribute);
                    ligandPropertyRemarks.put(keyPair, refValue.toString());
                    // reset values
                    refThreeLetterCode = null;
                    refAttribute = null;
                    refValue = new StringBuilder();
                    continue;
                }
                // append values
                refValue.append(trimEnd(content));
            }
            // REMARK 2
            if (Remark2Token.REMARK_2.matcher(currentLine).matches()) {
                String content = Remark2Token.REMARK_CONTENT.extract(currentLine).trim();
                if (content.trim().isEmpty()) {
                    continue;
                }
                if (DoubleMatcher.containsDouble(content)) {
                    resolution = Double.parseDouble(content);
                } else {
                    resolution = -1;
                }
            }
            // SEQADV
            if (SequenceAdviceToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                sequenceAdviceLines.add(currentLine);
            }
            if (AtomToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                break;
            }
        }
    }

    /**
     * Extract all lines that contain conect entries.
     * FIXME: this requires an extra iteration over all pdb lines
     */
    private void getLinks() {
        boolean linksFound = false;
        for (String currentLine : pdbLines) {
            // check if title line
            if (LinkToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                // if this is the first time such a line occurs, the links were found
                if (!linksFound) {
                    linksFound = true;
                }
                // append title
                linkLines.add(currentLine);
            } else {
                // if title has been found and a line with another content is found
                if (linksFound) {
                    // quit parsing title
                    return;
                }
            }
        }
    }

    private void getConnections() {
        boolean connectionFound = false;
        for (String currentLine : pdbLines) {
            // check if title line
            if (ConnectionToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                // if this is the first time such a line occurs, the connections were found
                if (!connectionFound) {
                    connectionFound = true;
                }
                // append title
                connectionLines.add(currentLine);
            } else {
                // if title has been found and a line with another content is found
                if (connectionFound) {
                    // quit parsing title
                    return;
                }
            }
        }
    }

    /**
     * Keeps only lines, that belong to a certain model.
     *
     * @param modelIdentifier The identifier of the model.
     */
    private void reduceToModel(int modelIdentifier) {
        List<String> reducedList = new ArrayList<>();
        boolean collectLines = false;
        // for each line
        for (String currentLine : pdbLines) {
            // check if the correct model has begun
            if (ModelToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                int currentModel = Integer.parseInt(ModelToken.MODEL_SERIAL.extract(currentLine));
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
        pdbLines = reducedList;
    }

    /**
     * Keeps only lines, that belong to a certain model.
     *
     * @param chainIdentifier The identifier of the model.
     */
    private void reduceToChain(String chainIdentifier) {
        List<String> reducedList = new ArrayList<>();
        // for each line
        for (String currentLine : pdbLines) {
            // check if this is a atom line
            if (AtomToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                String currentChain = AtomToken.CHAIN_IDENTIFIER.extract(currentLine);
                // collect line if it has the correct chainIdentifier
                if (currentChain.equals(chainIdentifier)) {
                    reducedList.add(currentLine);
                }
            } else if (ModelToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                // keep lines that indicate models
                reducedList.add(currentLine);
            } else if (ChainTerminatorToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                if (ChainTerminatorToken.CHAIN_IDENTIFIER.extract(currentLine).equals(chainIdentifier)) {
                    // keep lines that indicate models
                    reducedList.add(currentLine);
                }
            }
        }
        pdbLines = reducedList;
    }

    /**
     * Collects and creates the actual structure from all remaining lines.
     *
     * @return The parsed structure.
     */
    private Structure collectStructure() {
        collectAtomInformation();
        createContentTree();

        logger.debug("Creating structure for {}", contentTree.getIdentifier());

        OakStructure structure = new OakStructure();
        if (contentTree.getIdentifier().isEmpty()) {
            contentTree.setIdentifier(DEFAULT_PDB_IDENTIFIER);
        }
        structure.setPdbIdentifier(contentTree.getIdentifier());
        structure.setTitle(titleBuilder.toString());
        structure.setResolution(resolution);
        for (ContentTreeNode modelNode : contentTree.getNodesFromLevel(ContentTreeNode.StructureLevel.MODEL)) {
            logger.debug("Collecting chains for model {}", modelNode.getIdentifier());
            OakModel model = new OakModel(Integer.parseInt(modelNode.getIdentifier()));
            for (ContentTreeNode chainNode : modelNode.getNodesFromLevel(ContentTreeNode.StructureLevel.CHAIN)) {
                logger.trace("Collecting leafs for chain {}", chainNode.getIdentifier());
                OakChain chain = new OakChain(chainNode.getIdentifier());
                for (ContentTreeNode leafNode : chainNode.getNodesFromLevel(ContentTreeNode.StructureLevel.LEAF)) {
                    LeafIdentifier leafIdentifier = new LeafIdentifier(currentPDB,
                            model.getModelIdentifier(),
                            chain.getChainIdentifier(),
                            Integer.parseInt(leafNode.getIdentifier()),
                            leafNode.getInsertionCode());
                    OakLeafSubstructure<?> leafSubstructure = LeafSubstructureBuilder.create(iterator)
                            .name(leafCodes.get(leafIdentifier))
                            .identifier(leafIdentifier)
                            .atoms(leafNode.getAtoms())
                            .build();
                    if (hetAtoms.contains(leafSubstructure.getIdentifier())) {
                        leafSubstructure.setAnnotatedAsHetAtom(true);
                    }
                    if (notInConsecutiveChain.contains(leafSubstructure.getIdentifier())) {
                        chain.addLeafSubstructure(leafSubstructure);
                    } else {
                        chain.addLeafSubstructure(leafSubstructure, true);
                    }
                }
                model.addChain(chain);
            }
            structure.addModel(model);
        }
        // connect backbone
        if (iterator.getReducer().getOptions().isCreatingEdges()) {
            structure.getAllChains().stream()
                    .map(OakChain.class::cast).forEach(OakChain::connectChainBackbone);
        }
        // process link entries
        if (iterator.getReducer().getOptions().enforceConnection()) {
            annotateLinks(structure);
            annotateConnections(structure);
        }
        annotateSeqenceAdvice(structure);
        UniqueAtomIdentifier lastAtom = Collections.max(atoms.keySet());
        structure.setLastAddedAtomIdentifier(lastAtom.getAtomSerial());
        postProcessProperties();
        return structure;
    }

    /**
     * Create and assign links to the structure.
     *
     * @param structure The structure to be annotated.
     */
    private void annotateLinks(OakStructure structure) {
        for (String linkLine : linkLines) {
            LinkEntry linkEntry = LinkToken.assembleLinkEntry(structure, linkLine);
            if (linkEntry != null) {
                structure.addLinkEntry(linkEntry);
            }
        }
    }

    private void annotateConnections(OakStructure structure) {
        for (String connectionLine : connectionLines) {
            ConnectionToken.assignConnections(structure, connectionLine);
        }
    }

    private void annotateSeqenceAdvice(OakStructure structure) {
        for (String connectionLine : sequenceAdviceLines) {
            SequenceAdviceToken.assignSequenceAdvice(structure, connectionLine);
        }
    }

    /**
     * Collects information from atom and hetatm lines.
     */
    private void collectAtomInformation() {
        logger.debug("Collecting information from {} PDB lines", pdbLines.size());
        for (String currentLine : pdbLines) {
            String currentRecordType = AtomToken.RECORD_TYPE.extract(currentLine);
            if (AtomToken.RECORD_PATTERN.matcher(currentRecordType).matches()) {
                // TODO move this to reducer?
                if (!iterator.getReducer().getOptions().isHeteroAtoms() && currentRecordType.equals("HETATM")) {
                    continue;
                }
                String alternativeLocation = AtomToken.ALTERNATE_LOCATION_INDICATOR.extract(currentLine);
                if (!alternativeLocation.trim().isEmpty() && !alternativeLocation.equals("A")) {
                    continue;
                }
                UniqueAtomIdentifier identifier = createUniqueAtomIdentifier(currentLine);
                atoms.put(identifier, AtomToken.assembleAtom(currentLine));
                LeafIdentifier leafIdentifier = new LeafIdentifier(identifier.getPdbIdentifier(),
                        identifier.getModelIdentifier(), identifier.getChainIdentifier(),
                        identifier.getLeafSerial(), identifier.getLeafInsertionCode());
                currentChain = leafIdentifier.getChainIdentifier();
                if (currentRecordType.equals("HETATM")) {
                    hetAtoms.add(leafIdentifier);
                }
                // add everything before termination record to consecutive chain
                if (closedChains.contains(currentModel + "-" + currentChain)) {
                    notInConsecutiveChain.add(leafIdentifier);
                }
                leafCodes.put(leafIdentifier, AtomToken.RESIDUE_NAME.extract(currentLine));
            } else if (currentRecordType.equals("MODEL")) {
                currentModel = Integer.parseInt(ModelToken.MODEL_SERIAL.extract(currentLine));
            } else if (currentRecordType.equals("TER")) {
                closedChains.add(currentModel + "-" + currentChain);
            }
        }
    }

    /**
     * Places each atom in the content tree.
     */
    private void createContentTree() {
        logger.debug("Creating content tree.");
        contentTree = new ContentTreeNode(currentPDB, ContentTreeNode.StructureLevel.STRUCTURE);
        atoms.forEach((identifer, atom) -> contentTree.appendAtom(atom, identifer));
        if (atoms.isEmpty()) {
            throw new StructureParserException("Unable to apply the reduction, supplied with the reducer: " + iterator);
        }
    }

    /**
     * Creates a unique atom identifier for the given atom line.
     *
     * @param atomLine The atom line.
     * @return An unique atom identifier.
     */
    private UniqueAtomIdentifier createUniqueAtomIdentifier(String atomLine) {
        int atomSerial = Integer.parseInt(AtomToken.ATOM_SERIAL.extract(atomLine));
        String chain = AtomToken.CHAIN_IDENTIFIER.extract(atomLine);
        int leaf = Integer.parseInt(AtomToken.RESIDUE_SERIAL.extract(atomLine));
        String insertion = AtomToken.RESIDUE_INSERTION.extract(atomLine);
        char insertionCode = insertion.isEmpty() ? DEFAULT_INSERTION_CODE : insertion.charAt(0);
        return new UniqueAtomIdentifier(currentPDB, currentModel, chain, leaf, insertionCode, atomSerial);
    }

    private void postProcessProperties() {
        for (Map.Entry<Pair<String>, String> entry : ligandPropertyRemarks.entrySet()) {
            Pair<String> key = entry.getKey();
            String threeLetterCode = key.getFirst();
            String propertyType = key.getSecond();
            String content = entry.getValue();
            if (propertyType.equals("INCHI")) {
                LeafSkeleton leafSkeleton = iterator.getSkeletons().get(threeLetterCode);
                if (leafSkeleton == null) {
                    logger.warn("parsed property for {}, but no suitable leaf was present", threeLetterCode);
                    continue;
                }
                leafSkeleton.setInchi(content);
            }
        }
    }

}
