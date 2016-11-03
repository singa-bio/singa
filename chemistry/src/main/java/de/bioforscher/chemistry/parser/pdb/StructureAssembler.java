package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.*;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import static de.bioforscher.chemistry.parser.pdb.AtomToken.*;

/**
 * Created by Christoph on 23.06.2016.
 */
public class StructureAssembler {

    private static StructureAssembler assembler = new StructureAssembler();

    private SubStructure globalStructure;

    private char currentChainIdentifier;
    private char lastChainIdentifier;
    private Chain currentChain;

    private int currentResidueSerial;
    private int lastResidueSerial = Integer.MIN_VALUE;
    private ResidueType currentResidueType;
    private ResidueType lastResidueType;
    private EnumMap<AtomName, Atom> currentResidueAtoms;

    private Atom currentAtom;

    private StructureAssembler() {
        this.globalStructure = new SubStructure(0);
        this.currentResidueAtoms = new EnumMap<>(AtomName.class);
    }

    public static SubStructure assembleStructure(List<String> pdbLines) {
        for (String currentLine : pdbLines) {
            if (AtomToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                // order of operation is relevant
                // first extract and assemble chain
                String extractedChain = CHAIN_IDENTIFIER.extract(currentLine);
                if (!extractedChain.isEmpty()) {
                    assembler.currentChainIdentifier = extractedChain.charAt(0);
                    if ((assembler.currentChainIdentifier != assembler.lastChainIdentifier)
                            || (assembler.currentChain == null)) {
                        assembler.assembleChain();
                    }
                }
                // then extract and assemble residue
                assembler.currentResidueSerial = Integer.valueOf(RESIDUE_SERIAL.extract(currentLine));
                assembler.currentResidueType = ResidueType.getResidueTypeByThreeLetterCode(RESIDUE_NAME.extract(currentLine))
                        .orElseThrow(IllegalArgumentException::new);
                if ((assembler.currentResidueSerial != assembler.lastResidueSerial) &&
                        (assembler.lastResidueSerial != Integer.MIN_VALUE)) {
                    assembler.assembleResidue();
                }
                // then assemble atom from current line
                assembler.currentAtom = assembleAtom(currentLine);
                assembler.currentResidueAtoms.put(assembler.currentAtom.getAtomName(), assembler.currentAtom);
                // finally change current and lasts
                assembler.lastResidueType = assembler.currentResidueType;
                assembler.lastChainIdentifier = assembler.currentChainIdentifier;
                assembler.lastResidueSerial = assembler.currentResidueSerial;
            }
        }
        // connecting peptide backbone per chain if possible
        assembler.globalStructure.getSubstructures().forEach(substructure -> {
                    if (substructure instanceof Chain) {
                        ((Chain) substructure).connectChainBackbone();
                    }
                }
        );

        return assembler.globalStructure;

    }

    private void assembleChain() {
        this.currentChain = new Chain((int) this.currentChainIdentifier);
        this.currentChain.setChainIdentifier(this.currentChainIdentifier);
        this.globalStructure.addSubstructure(this.currentChain);
    }

    private void assembleResidue() {
        Residue currentResidue = ResidueFactory.createResidueFromAtoms(this.lastResidueSerial, this.lastResidueType, this.currentResidueAtoms);
        // when no chain is present put the residue in the current global substructure
        if (this.currentChain != null) {
            this.currentChain.addSubstructure(currentResidue);
        } else {
            this.globalStructure.addSubstructure(currentResidue);
        }
        this.currentResidueAtoms.clear();
    }

}