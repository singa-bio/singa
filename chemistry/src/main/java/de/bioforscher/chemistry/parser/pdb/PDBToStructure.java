package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.Atom;
import de.bioforscher.chemistry.physical.SubStructure;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
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
                currentResidue = new SubStructure(toAtom(currentLine));
            } else
                currentResidue.addNode(toAtom(currentLine));
        }

        return null;
    }

    public static Atom toAtom(String atomLine) {

        String elementSymbol = AtomToken.extractValueFromPDBLine(atomLine, AtomToken.ELEMENT_SYMBOL);
        Element element = ElementProvider.getElementBySymbol(elementSymbol).orElseThrow(IllegalArgumentException::new);

        Double x = Double.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.X_COORDINATE));
        Double y = Double.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.Y_COORDINATE));
        Double z = Double.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.Z_COORDINATE));
        Vector3D coordiantes = new Vector3D(x, y, z);

        Integer atomSerial = Integer.valueOf(AtomToken.extractValueFromPDBLine(atomLine, AtomToken.ATOM_SERIAL));
        String atomName = AtomToken.extractValueFromPDBLine(atomLine, AtomToken.ATOM_NAME);

        return new Atom(atomSerial, element, atomName, coordiantes);
    }
}