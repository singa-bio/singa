package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.chemistry.physical.proteins.ResidueFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Created by fkaiser on 10.11.16.
 */
public class AtomFilterTest {

    @Test
    public void shouldApplyAtomFilter() throws IOException {
        ResidueFactory.setToOmitHydrogens(true);
        Structure structure = PDBParserService.parseProteinById("4HHB");

        // valine
        SubStructure subStructure = structure.getAllChains().get(0).getSubstructures().get(0);

        List<Atom> backboneAtoms = subStructure.getAllAtoms().stream()
                                               .filter(AtomFilter.isBackbone())
                                               .collect(Collectors.toList());
        List<Atom> sidechainAtoms = subStructure.getAllAtoms().stream()
                                                .filter(AtomFilter.isSidechain())
                                                .collect(Collectors.toList());

        // check backbone atoms
        assertTrue(backboneAtoms.size() == 4);
        assertTrue(backboneAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.N).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.CA).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.C).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.O).count() == 1);

        // check sidechain atoms
        assertTrue(sidechainAtoms.size() == 3);
        assertTrue(sidechainAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.CB).count() == 1);
        assertTrue(sidechainAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.CG1).count() == 1);
        assertTrue(sidechainAtoms.stream().filter(atom -> atom.getAtomName() == AtomName.CG2).count() == 1);
    }
}