package bio.singa.structure.model.pdb;

import bio.singa.structure.model.families.AtomName;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.general.StructuralEntityFilter;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class AtomFilterTest {

    @Test
    void shouldApplyAtomFilter() {

        StructureParserOptions options = new StructureParserOptions();
        options.omitHydrogens(true);

        Structure structure = StructureParser.pdb()
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
        assertEquals(4, backboneAtoms.size());
        assertEquals(1, backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.N.getName())).count());
        assertEquals(1, backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.CA.getName())).count());
        assertEquals(1, backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.C.getName())).count());
        assertEquals(1, backboneAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.O.getName())).count());

        // check sidechain atoms
        assertEquals(3, sideChainAtoms.size());
        assertEquals(1, sideChainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.CB.getName())).count());
        assertEquals(1, sideChainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.CG1.getName())).count());
        assertEquals(1, sideChainAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.CG2.getName())).count());
    }

    @Test
    void shouldApplyAtomFilterByNames() {
        AminoAcid aminoAcid = StructuralFamilies.AminoAcids.getPrototype(StructuralFamilies.AminoAcids.METHIONINE);
        Predicate<Atom> atomFilter = StructuralEntityFilter.AtomFilter.hasAtomNames("CA", "SD");
        List<Atom> filteredAtoms = aminoAcid.getAllAtoms().stream()
                .filter(atomFilter)
                .collect(Collectors.toList());
        assertEquals(2, filteredAtoms.size());
        assertEquals(1, filteredAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.CA.getName())).count());
        assertEquals(1, filteredAtoms.stream().filter(atom -> Objects.equals(atom.getAtomName(), AtomName.SD.getName())).count());
    }
}