package de.bioforscher.singa.chemistry.physical.atoms;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class AtomFilterTest {

    @Test
    public void shouldApplyAtomFilter() throws IOException {

        StructureParserOptions options = new StructureParserOptions();
        options.omitHydrogens(true);

        Structure structure = StructureParser.online()
                .pdbIdentifier("4HHB")
                .everything()
                .setOptions(options)
                .parse();

        // valine
        AminoAcid branchSubstructure = structure.getAllAminoAcids().get(0);

        List<Atom> backboneAtoms = branchSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isBackbone())
                .collect(Collectors.toList());
        List<Atom> sideChainAtoms = branchSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isSideChain())
                .collect(Collectors.toList());

        // check backbone atoms
        assertTrue(backboneAtoms.size() == 4);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.N.getName())).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CA.getName())).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.C.getName())).count() == 1);
        assertTrue(backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.O.getName())).count() == 1);

        // check sidechain atoms
        assertTrue(sideChainAtoms.size() == 3);
        assertTrue(sideChainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CB.getName())).count() == 1);
        assertTrue(sideChainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CG1.getName())).count() == 1);
        assertTrue(sideChainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CG2.getName())).count() == 1);
    }

    @Test
    public void shouldApplyAtomFilterByNames() {
        AminoAcid aminoAcid = AminoAcidFamily.METHIONINE.getPrototype();
        Predicate<Atom> atomFilter = StructuralEntityFilter.AtomFilter.hasAtomNames("CA", "SD");
        List<Atom> filteredAtoms = aminoAcid.getAllAtoms().stream()
                .filter(atomFilter)
                .collect(Collectors.toList());
        assertTrue(filteredAtoms.size() == 2);
        assertTrue(filteredAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.CA.getName())).count() == 1);
        assertTrue(filteredAtoms.stream().filter(atom -> Objects.equals(atom.getAtomNameString(), AtomName.SD.getName())).count() == 1);
    }
}