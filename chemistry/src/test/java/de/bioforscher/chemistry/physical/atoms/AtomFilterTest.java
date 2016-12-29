package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.parser.pdb.structures.PDBParserService;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Structure;
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
        LeafFactory.setToOmitHydrogens(true);
        Structure structure = PDBParserService.parseProteinById("4HHB");

        // valine
        Residue branchSubstructure = structure.getAllResidues().get(0);

        List<Atom> backboneAtoms = branchSubstructure.getAllAtoms().stream()
                                               .filter(AtomFilter.isBackbone())
                                               .collect(Collectors.toList());
        List<Atom> sidechainAtoms = branchSubstructure.getAllAtoms().stream()
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