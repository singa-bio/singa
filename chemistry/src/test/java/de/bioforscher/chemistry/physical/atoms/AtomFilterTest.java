package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.model.StructuralEntityFilter;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.parser.pdb.structures.StructureSources.PDB_ONLINE;
import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class AtomFilterTest {

    @Test
    public void shouldApplyAtomFilter() throws IOException {
        LeafFactory.setToOmitHydrogens(true);
        Structure structure = StructureParser.from(PDB_ONLINE)
                .identifier("4HHB")
                .everything()
                .parse();

        // valine
        AminoAcid branchSubstructure = structure.getAllResidues().get(0);

        List<Atom> backboneAtoms = branchSubstructure.getAllAtoms().stream()
                                               .filter(StructuralEntityFilter.AtomFilter.isBackbone())
                                               .collect(Collectors.toList());
        List<Atom> sidechainAtoms = branchSubstructure.getAllAtoms().stream()
                                                .filter(StructuralEntityFilter.AtomFilter.isSidechain())
                                                .collect(Collectors.toList());

        // check backbone atoms
        assertTrue(backboneAtoms.size() == 4);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.N.getName())).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CA.getName())).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.C.getName())).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.O.getName())).count() == 1);

        // check sidechain atoms
        assertTrue(sidechainAtoms.size() == 3);
        assertTrue(sidechainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CB.getName())).count() == 1);
        assertTrue(sidechainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CG1.getName())).count() == 1);
        assertTrue(sidechainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CG2.getName())).count() == 1);
    }
}