package de.bioforscher.chemistry.parser.pdb.tokens;

import de.bioforscher.chemistry.parser.pdb.PDBParsingTreeNode;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.UniqueAtomIdentifer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static de.bioforscher.chemistry.parser.pdb.tokens.AtomToken.*;

/**
 * Created by leberech on 07/12/16.
 */
public class StructureCollector {

    private String currentPDB = "0000";
    private int currentModel = 0;

    private Map<UniqueAtomIdentifer, Atom> atoms;
    private Map<UniqueAtomIdentifer, String> leafStructure;

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
                collector.leafStructure.put(identifier, RESIDUE_NAME.extract(currentLine));
            }
        }

        // collector.atoms.forEach( (key,value) -> System.out.println(key+" "+value));
        // collector.leafStructure.forEach( (key,value) -> System.out.println(key+" "+value));

        PDBParsingTreeNode root = new PDBParsingTreeNode(collector.currentPDB, PDBParsingTreeNode.StructureLevel.STRUCTURE);
        collector.atoms.forEach((key, value) -> {
            root.appendAtom(key, value);
            System.out.println("Next Atom");
        });

        return null;
    }

    private void traverseAtoms() {


    }

    private void handleChain() {


    }

    private void handleLeafStructure() {


    }

    private UniqueAtomIdentifer createUniqueIdentifier(String atomLine){
        int atomSerial = Integer.valueOf(ATOM_SERIAL.extract(atomLine));
        String chain = CHAIN_IDENTIFIER.extract(atomLine);
        int leaf = Integer.valueOf(RESIDUE_SERIAL.extract(atomLine));
        return new UniqueAtomIdentifer(this.currentPDB, this.currentModel, chain, leaf, atomSerial);
    }

}
