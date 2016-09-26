package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.*;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Christoph on 23.06.2016.
 */
public class PDBToStructure {

    public static SubStructure parseAminoAcidAtoms(List<String> lines) {

        Iterator<String> iterator = lines.iterator();
        List<SubStructure> residues = new ArrayList<>();
        SubStructure currentResidue = null;
        int lastResidueNumber = Integer.MIN_VALUE;
        while (iterator.hasNext()) {
            String currentLine = iterator.next();
            int currentResidueNumber = Integer.valueOf(AtomToken.extractValueFromPDBLine(currentLine, AtomToken.RESIDUE_SERIAL));
            if (currentResidueNumber > lastResidueNumber) {
                lastResidueNumber = currentResidueNumber;
                if (currentResidue != null) {
                    residues.add(currentResidue);
                    currentResidue.connectByDistance();
                    System.out.println(currentResidue);
                }
                currentResidue = new SubStructure(extractAtomInformation(currentLine));
            } else
                currentResidue.addNode(extractAtomInformation(currentLine));
        }

        return null;
    }

    public static Structure parseResidues(List<String> lines) {
        Iterator<String> iterator = lines.iterator();
        Structure structure = new Structure();

        EnumMap<AtomName, Atom> currentResidueAtoms = new EnumMap<>(AtomName.class);
        int lastResidueNumber = Integer.MIN_VALUE;
        int currentResidueNumber = Integer.MIN_VALUE;
        ResidueType lastResidueType = null;
        ResidueType currentResidueType = null;

        while (iterator.hasNext()) {
            String currentLine = iterator.next();
            // get residue serial number (identifier)
            currentResidueNumber = Integer.valueOf(AtomToken.extractValueFromPDBLine(currentLine, AtomToken.RESIDUE_SERIAL));
            currentResidueType = ResidueType.getResidueTypeByThreeLetterCode(AtomToken.extractValueFromPDBLine(currentLine, AtomToken.RESIDUE_NAME))
                                            .orElseThrow(IllegalArgumentException::new);
            // if this is the next residue
            if (currentResidueNumber > lastResidueNumber) {
                // and there are atoms left in the current residue list
                if (!currentResidueAtoms.isEmpty()) {
                    // collect atoms from last residue
                    structure.addSubstructure(ResidueFactory.createResidueFromAtoms(lastResidueNumber, lastResidueType,
                            currentResidueAtoms));
                    currentResidueAtoms.clear();
                }
                // finally update residue serial number (identifier) and type
                lastResidueNumber = currentResidueNumber;
                lastResidueType = currentResidueType;
            }
            // and parse the current line
            AtomName atomName = AtomName.getAtomNameFromString(AtomToken.extractValueFromPDBLine(currentLine,
                    AtomToken.ATOM_NAME)).orElseThrow(IllegalArgumentException::new);
            currentResidueAtoms.put(atomName, extractAtomInformation(currentLine));
        }
        // add last residue
        structure.addSubstructure(ResidueFactory.createResidueFromAtoms(lastResidueNumber, lastResidueType,
                currentResidueAtoms));
        return structure;
    }


    public static Atom extractAtomInformation(String atomLine) {

        String elementSymbol = AtomToken.extractValueFromPDBLine(atomLine, AtomToken.ELEMENT_SYMBOL);
        Element element = ElementProvider.getElementBySymbol(elementSymbol).orElseThrow(IllegalArgumentException::new);

        Double x = Double.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.X_COORDINATE));
        Double y = Double.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.Y_COORDINATE));
        Double z = Double.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.Z_COORDINATE));
        Vector3D coordinates = new Vector3D(x, y, z);

        Integer atomSerial = Integer.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.ATOM_SERIAL));
        // FIXME: this is done twice
        AtomName atomName = AtomName.getAtomNameFromString(AtomToken.extractValueFromPDBLine(atomLine,
                AtomToken.ATOM_NAME)).orElseThrow(IllegalArgumentException::new);

        return new Atom(atomSerial, element, atomName, coordinates);
    }
}