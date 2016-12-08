package de.bioforscher.chemistry.parser.pdb.tokens;

import de.bioforscher.chemistry.parser.pdb.PDBParsingTreeNode;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.families.LigandFamily;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.chemistry.physical.families.ResidueFactory;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.UniqueAtomIdentifer;

import java.util.*;

import static de.bioforscher.chemistry.parser.pdb.tokens.AtomToken.*;

/**
 * Created by leberech on 07/12/16.
 */
public class StructureCollector {

    private String currentPDB = "0000";
    private int currentModel = 0;

    private Map<UniqueAtomIdentifer, Atom> atoms;
    private Map<String, String> leafStructure;

    public StructureCollector() {
        this.atoms = new TreeMap<>();
        this.leafStructure = new TreeMap<>();
    }

    // TODO here, the atom serial is parsed twice, once creating the identifer and once creating the atom

    public static Structure collectStructure(List<String> pdbLines) {
        StructureCollector collector = new StructureCollector();
        for (String currentLine : pdbLines) {
            if (RECORD_PATTERN.matcher(currentLine).matches()) {
                UniqueAtomIdentifer identifier = collector.createUniqueIdentifier(currentLine);
                collector.atoms.put(identifier, AtomToken.assembleAtom(currentLine));
                collector.leafStructure.put(String.valueOf(identifier.getAtomSerial()), RESIDUE_NAME.extract(currentLine));
            }
        }

        PDBParsingTreeNode root = new PDBParsingTreeNode(collector.currentPDB, PDBParsingTreeNode.StructureLevel.STRUCTURE);
        collector.atoms.forEach(root::appendAtom);

        Map<String, String> leafNames = root.getLeafNames(collector.leafStructure);

        Structure structure = new Structure();

        int chainId= 0;
        for (PDBParsingTreeNode chainNode: root.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.CHAIN)) {
            Chain chain = new Chain(chainId++);
            chain.setChainIdentifier(chainNode.getIdentifier());
            for (PDBParsingTreeNode leafNode : chainNode.getNodesFromLevel(PDBParsingTreeNode.StructureLevel.LEAF)) {
                String leafName = leafNames.get(leafNode.getIdentifier());
                System.out.println("creating " + leafNode.getIdentifier() + ":" + leafName);
                Optional<ResidueFamily> residueFamily = ResidueFamily.getResidueTypeByThreeLetterCode(leafName);
                if (residueFamily.isPresent()) {
                    // parse as residue
                    System.out.println(" as Residue");
                    chain.addSubstructure(ResidueFactory.createResidueFromAtoms(Integer.valueOf(leafNode.getIdentifier()), residueFamily.get(), leafNode.getAtomMap()));
                } else {
                    // parse as container
                    System.out.println(" as AtomContainer");
                    AtomContainer<LigandFamily> container =  new AtomContainer<>(Integer.valueOf(leafNode.getIdentifier()), LigandFamily.UNKNOWN);
                    leafNode.getAtomMap().forEach((key, value) -> container.addNode(value));
                    chain.addSubstructure(container);
                }
            }
            structure.addSubstructure(chain);
        }

        Map<Atom, UniqueAtomIdentifer> identifierMap = new HashMap<>();
        collector.atoms.forEach((identifer, atom) -> identifierMap.put(atom, identifer));
        structure.setIdentiferMap(identifierMap);

        return structure;
    }

    private boolean isNucleicAcid(PDBParsingTreeNode chainNode) {
        if (chainNode.getLevel() != PDBParsingTreeNode.StructureLevel.CHAIN) {
            return false;
        }


        return false;
    }

    private UniqueAtomIdentifer createUniqueIdentifier(String atomLine) {
        int atomSerial = Integer.valueOf(ATOM_SERIAL.extract(atomLine));
        String chain = CHAIN_IDENTIFIER.extract(atomLine);
        int leaf = Integer.valueOf(RESIDUE_SERIAL.extract(atomLine));
        return new UniqueAtomIdentifer(this.currentPDB, this.currentModel, chain, leaf, atomSerial);
    }

}
