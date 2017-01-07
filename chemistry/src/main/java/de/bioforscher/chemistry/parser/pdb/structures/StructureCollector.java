package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.parser.pdb.ligands.LigandParserService;
import de.bioforscher.chemistry.parser.pdb.structures.tokens.AtomToken;
import de.bioforscher.chemistry.parser.pdb.structures.tokens.ModelToken;
import de.bioforscher.chemistry.parser.pdb.structures.tokens.TitleToken;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.families.LigandFamily;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.UniqueAtomIdentifer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static de.bioforscher.chemistry.parser.pdb.structures.tokens.AtomToken.*;

/**
 * @author cl
 */
public class StructureCollector {

    private static final Logger logger = LoggerFactory.getLogger(StructureCollector.class);

    private String currentPDB = "0000";
    private int currentModel = 0;

    private Map<UniqueAtomIdentifer, Atom> atoms;
    private Map<LeafIdentifier, String> leafNames;

    private Map<String, String> typeMemory;

    public StructureCollector() {
        this.atoms = new HashMap<>();
        this.leafNames = new TreeMap<>();
        this.typeMemory = new HashMap<>();
    }

    // TODO here, the atom serial is parsed twice, once creating the identifer and once creating the atom

    public static Structure collectStructure(List<String> pdbLines, String chainId) {
        StructureCollector collector = new StructureCollector();
        logger.debug("collecting content from {} PDB lines", pdbLines.size());
        for (String currentLine : pdbLines) {
            if (AtomToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                UniqueAtomIdentifer identifier = collector.createUniqueAtomIdentifier(currentLine);
                collector.atoms.put(identifier, AtomToken.assembleAtom(currentLine));
                collector.leafNames.put(new LeafIdentifier(identifier.getChainIdentifer(), identifier.getLeafIdentifer()), RESIDUE_NAME.extract(currentLine));
            } else if (ModelToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                collector.currentModel = Integer.valueOf(ModelToken.MODEL_SERIAL.extract(currentLine));
            } else if (TitleToken.RECORD_PATTERN.matcher((currentLine)).matches()) {
                collector.currentPDB = TitleToken.ID_CODE.extract(currentLine);
            }
        }

        logger.debug("grouping lines by content");
        PDBParsingTreeNode root = new PDBParsingTreeNode(collector.currentPDB, PDBParsingTreeNode.StructureLevel.STRUCTURE);
        collector.atoms.forEach((identifer, atom) -> root.appendAtom(atom, identifer));

        Structure structure = new Structure();
        structure.setPdbID(root.getIdentifier());

        logger.debug("creating structure");
        int chainGraphId = 0;
        for (PDBParsingTreeNode modelNode : root.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.MODEL)) {
            logger.debug("collecting chains for model {}", modelNode.getIdentifier());
            StructuralModel model = new StructuralModel(Integer.valueOf(modelNode.getIdentifier()));
            for (PDBParsingTreeNode chainNode : modelNode.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.CHAIN)) {
                if (chainNode.getIdentifier().matches(chainId)) {
                    logger.trace("collecting leafs for chain {}", chainNode.getIdentifier());
                    Chain chain = new Chain(chainGraphId++);
                    chain.setChainIdentifier(chainNode.getIdentifier());
                    for (PDBParsingTreeNode leafNode : chainNode.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.LEAF)) {
                        String leafName = collector.leafNames.get(new LeafIdentifier(chainNode.getIdentifier(), Integer.valueOf(leafNode.getIdentifier())));
                        logger.trace("creating leaf {}:{} for chain {}", leafNode.getIdentifier(), leafName, chainNode.getIdentifier());

                        LeafIdentifier leafIdentifier = new LeafIdentifier(collector.currentPDB, collector.currentModel, chainNode.getIdentifier(), Integer.valueOf(leafNode.getIdentifier()));

                        Optional<ResidueFamily> residueFamily = ResidueFamily.getResidueTypeByThreeLetterCode(leafName);
                        EnumMap<AtomName, Atom> atoms = leafNode.getAtomMap();
                        if (residueFamily.isPresent()) {
                            Residue residue = LeafFactory.createResidueFromAtoms(leafIdentifier, residueFamily.get(), atoms);
                            chain.addSubstructure(residue);
                        } else {
                            Optional<NucleotideFamily> nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(leafName);
                            if (nucleotideFamily.isPresent()) {
                                chain.addSubstructure(collector.createNucleotide(leafName, leafIdentifier, nucleotideFamily.get(), atoms));
                            } else {

                                if (!collector.typeMemory.containsKey(leafName)) {
                                    try {
                                        collector.typeMemory.put(leafName, LigandParserService.parseLigandTypeById(leafName));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (collector.typeMemory.get(leafName).equals("RNA LINKING")) {
                                    chain.addSubstructure(collector.createNucleotide(leafName, leafIdentifier, NucleotideFamily.MODIFIED_NUCLEOTIDE, atoms));
                                } else {
                                    AtomContainer<LigandFamily> container = new AtomContainer<>(leafIdentifier, LigandFamily.UNKNOWN);
                                    container.setName(leafName);
                                    leafNode.getAtomMap().forEach((key, value) -> container.addNode(value));
                                    chain.addSubstructure(container);
                                }

                            }
                        }
                    }
                    model.addSubstructure(chain);
                }
                structure.addSubstructure(model);
            }
        }
        structure.getAllChains().forEach(Chain::connectChainBackbone);
        return structure;
    }




    private UniqueAtomIdentifer createUniqueAtomIdentifier(String atomLine) {
        int atomSerial = Integer.valueOf(ATOM_SERIAL.extract(atomLine));
        String chain = CHAIN_IDENTIFIER.extract(atomLine);
        int leaf = Integer.valueOf(RESIDUE_SERIAL.extract(atomLine));
        return new UniqueAtomIdentifer(this.currentPDB, this.currentModel, chain, leaf, atomSerial);
    }

    private Nucleotide createNucleotide(String leafName, LeafIdentifier leafIdentifier, NucleotideFamily nucleotideFamily, EnumMap<AtomName, Atom> atoms) {
        Nucleotide nucleotide = LeafFactory.createNucleotideFromAtoms(leafIdentifier , nucleotideFamily, atoms);
        if (nucleotideFamily == NucleotideFamily.MODIFIED_NUCLEOTIDE) {
            nucleotide.setName(leafName);
        }
        return nucleotide;
    }

}
