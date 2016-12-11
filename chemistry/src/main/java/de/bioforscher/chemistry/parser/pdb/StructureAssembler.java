package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.parser.pdb.tokens.ModelToken;
import de.bioforscher.chemistry.parser.pdb.tokens.TerminatorTokens;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.families.ResidueFamily;

import java.util.EnumMap;
import java.util.List;

import static de.bioforscher.chemistry.parser.pdb.tokens.AtomToken.*;

/**
 * @author cl
 */
public class StructureAssembler {

    private static StructureAssembler assembler = new StructureAssembler();

    private boolean containsModels;

    private Structure globalStructure;

    private StructuralModel currentModel;

    private String currentChainIdentifier;
    private String lastChainIdentifier;
    private Chain currentChain;

    private int currentResidueSerial;
    private int lastResidueSerial = Integer.MIN_VALUE;
    private ResidueFamily currentResidueFamily;
    private ResidueFamily lastResidueFamily;
    private EnumMap<AtomName, Atom> currentResidueAtoms;

    private Atom currentAtom;

    private StructureAssembler() {
        this.globalStructure = new Structure();
        this.currentResidueAtoms = new EnumMap<>(AtomName.class);
    }

    public static Structure assembleStructure(List<String> pdbLines) {
        assembler = new StructureAssembler();
        for (String currentLine : pdbLines) {
            if (RECORD_PATTERN.matcher(currentLine).matches()) {
                // order of operation is relevant
                // first extract and assemble chain
                assembler.currentChainIdentifier = CHAIN_IDENTIFIER.extract(currentLine);
                if (!assembler.currentChainIdentifier.equals(assembler.lastChainIdentifier)) {
                    assembler.assembleChain();
                }
                // then extract and assemble residue
                assembler.currentResidueSerial = Integer.valueOf(RESIDUE_SERIAL.extract(currentLine));
                assembler.currentResidueFamily = ResidueFamily.getResidueTypeByThreeLetterCode(RESIDUE_NAME.extract(currentLine))
                        .orElseThrow(IllegalArgumentException::new);
                if ((assembler.currentResidueSerial != assembler.lastResidueSerial) &&
                        (assembler.lastResidueSerial != Integer.MIN_VALUE)) {
                    assembler.assembleResidue();
                }
                // then assemble atom from current line
                assembler.currentAtom = assembleAtom(currentLine);
                assembler.currentResidueAtoms.put(assembler.currentAtom.getAtomName(), assembler.currentAtom);
                // finally change current and lasts
                assembler.lastResidueFamily = assembler.currentResidueFamily;
                assembler.lastChainIdentifier = assembler.currentChainIdentifier;
                assembler.lastResidueSerial = assembler.currentResidueSerial;
            } else if (ModelToken.RECORD_PATTERN.matcher(currentLine).matches()) {
                int modelSerial = Integer.parseInt(ModelToken.MODEL_SERIAL.extract(currentLine));
                assembler.currentModel = new StructuralModel(modelSerial);
                if (!assembler.containsModels) {
                    assembler.containsModels = true;
                    assembler.globalStructure.setContainingModels(true);
                }
            } else if (TerminatorTokens.MODEL_TERMINATOR.matcher(currentLine).matches()) {
                assembler.assembleChain();
                assembler.assembleModel();
            }
        }

        if(!assembler.currentResidueAtoms.isEmpty()){
            assembler.assembleResidue();
        }

        if (!assembler.currentChain.getSubstructures().isEmpty()) {
            assembler.assembleChain();
        }

        // connecting peptide backbone per chain if possible
        assembler.globalStructure.getSubstructures().forEach(firstLevel -> {
                    if (firstLevel instanceof Chain) {
                        ((Chain) firstLevel).connectChainBackbone();
                    } else if (firstLevel instanceof StructuralModel) {
                        firstLevel.getSubstructures().forEach(secondLevel -> {
                            ((Chain) secondLevel).connectChainBackbone();
                        });
                    }
                }
        );

        return assembler.globalStructure;

    }

    private void assembleModel() {
        this.globalStructure.addSubstructure(assembler.currentModel);
        this.lastChainIdentifier = " ";
    }

    private void assembleChain() {
        if (this.currentChain != null) {
            if (this.containsModels) {
                this.currentModel.addSubstructure(this.currentChain);
            } else {
                this.globalStructure.addSubstructure(this.currentChain);
            }
        }
        // TODO this is not nice
        this.currentChain = new Chain((int) this.currentChainIdentifier.charAt(0));
        this.currentChain.setChainIdentifier(this.currentChainIdentifier);
    }

    private void chooseLeafType() {

    }

    private void assembleResidue() {
        Residue currentResidue = LeafFactory.createResidueFromAtoms(this.lastResidueSerial, this.lastResidueFamily, this.currentResidueAtoms);
        // when no chain is present put the residue in the current global substructure
        if (this.currentChain != null) {
            this.currentChain.addSubstructure(currentResidue);
        }
        this.currentResidueAtoms.clear();
    }

}