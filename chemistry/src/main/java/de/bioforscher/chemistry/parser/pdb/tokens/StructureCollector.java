package de.bioforscher.chemistry.parser.pdb.tokens;

import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.parser.pdb.PDBParsingTreeNode;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.families.LigandFamily;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.UniqueAtomIdentifer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.bioforscher.chemistry.parser.pdb.tokens.AtomToken.*;

/**
 * @author cl
 */
public class StructureCollector {

    private static final Logger logger = LoggerFactory.getLogger(StructureCollector.class);

    private String currentPDB = "0000";
    private int currentModel = 0;

    private Map<Atom, UniqueAtomIdentifer> atoms;
    private Map<String, String> leafStructure;

    public StructureCollector() {
        this.atoms = new HashMap<>();
        this.leafStructure = new TreeMap<>();
    }

    // TODO here, the atom serial is parsed twice, once creating the identifer and once creating the atom

    public static Structure collectStructure(List<String> pdbLines, String chainId) {
        StructureCollector collector = new StructureCollector();
        logger.debug("collecting content from {} pdblines", pdbLines.size());
        for (String currentLine : pdbLines) {
            if (RECORD_PATTERN.matcher(currentLine).matches()) {
                UniqueAtomIdentifer identifier = collector.createUniqueIdentifier(currentLine);
                collector.atoms.put(AtomToken.assembleAtom(currentLine), identifier);
                collector.leafStructure.put(String.valueOf(identifier.getAtomSerial()), RESIDUE_NAME.extract(currentLine));
            }
        }

        logger.debug("grouping lines by content");
        PDBParsingTreeNode root = new PDBParsingTreeNode(collector.currentPDB, PDBParsingTreeNode.StructureLevel.STRUCTURE);
        collector.atoms.forEach(root::appendAtom);

        Map<String, String> leafNames = root.getLeafNames(collector.leafStructure);

        Structure structure = new Structure();

        logger.debug("creating structure");
        int graphId = 0;
        for (PDBParsingTreeNode chainNode : root.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.CHAIN)) {
            if (chainNode.getIdentifier().matches(chainId)) {
                logger.trace("collecting leafs for chain {}", chainNode.getIdentifier());
                Chain chain = new Chain(graphId++);
                chain.setChainIdentifier(chainNode.getIdentifier());
                for (PDBParsingTreeNode leafNode : chainNode.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.LEAF)) {
                    String leafName = leafNames.get(leafNode.getIdentifier());
                    logger.trace("creating leaf {}:{} for chain {}", leafNode.getIdentifier(), leafName, chainNode.getIdentifier());
                    Optional<ResidueFamily> residueFamily = ResidueFamily.getResidueTypeByThreeLetterCode(leafName);
                    EnumMap<AtomName, Atom> atoms = leafNode.getAtomMap();
                    Map<Atom, UniqueAtomIdentifer> identifierMap = new HashMap<>();
                    collector.atoms.forEach(identifierMap::put);
                    if (residueFamily.isPresent()) {
                        Residue residue = LeafFactory.createResidueFromAtoms(Integer.valueOf(leafNode.getIdentifier()), residueFamily.get(), atoms);
                        residue.setIdentiferMap(identifierMap);
                        chain.addSubstructure(residue);
                    } else {
                        AtomContainer<LigandFamily> container = new AtomContainer<>(Integer.valueOf(leafNode.getIdentifier()), LigandFamily.UNKNOWN);
                        container.setName(leafName);
                        leafNode.getAtomMap().forEach((key, value) -> container.addNode(value));
                        container.setIdentiferMap(identifierMap);
                        chain.addSubstructure(container);
                    }
                }
                structure.addSubstructure(chain);
            }
        }

        return structure;
    }

    private UniqueAtomIdentifer createUniqueIdentifier(String atomLine) {
        int atomSerial = Integer.valueOf(ATOM_SERIAL.extract(atomLine));
        String chain = CHAIN_IDENTIFIER.extract(atomLine);
        int leaf = Integer.valueOf(RESIDUE_SERIAL.extract(atomLine));
        return new UniqueAtomIdentifer(this.currentPDB, this.currentModel, chain, leaf, atomSerial);
    }

}
