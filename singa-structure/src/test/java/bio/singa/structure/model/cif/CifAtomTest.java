package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.Structure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CifAtomTest {

    private static Structure structure1C0A;
    private static Atom atom412;
    private static Atom atom5444;
    private static Atom atomToModify;

    @BeforeAll
    static void initialize() {
        structure1C0A = StructureParser.cif()
                .pdbIdentifier("1C0A")
                .everything().parse();
        // no offset to regular pdb file
        atom412 = structure1C0A.getAtom(412).get();
        // one offset to regular pdb file
        atom5444 = structure1C0A.getAtom(5444).get();

        atomToModify = structure1C0A.getAtom(600).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(412, atom412.getAtomIdentifier());
        assertEquals(5444, atom5444.getAtomIdentifier());
    }

    @Test
    void getAtomName() {
        assertEquals("N2", atom412.getAtomName());
        assertEquals("CA", atom5444.getAtomName());
    }

    @Test
    void getPosition() {
        assertEquals(new Vector3D(62.204, -8.506, 22.574), atom412.getPosition());
        assertEquals(new Vector3D(70.311, 35.603, -10.967), atom5444.getPosition());
    }

    @Test
    void setPosition() {
        assertEquals(new Vector3D(64.802, 24.027, 34.836), atomToModify.getPosition());
        atomToModify.setPosition(new Vector3D(1.0, 1.0, 1.0));
        // state change
        assertEquals(new Vector3D(1.0, 1.0, 1.0), atomToModify.getPosition());
        // get it again from the structure
        atomToModify = structure1C0A.getAtom(600).get();
        assertEquals(new Vector3D(1.0, 1.0, 1.0), atomToModify.getPosition());
    }

    @Test
    void getElement() {
        assertEquals(ElementProvider.NITROGEN, atom412.getElement());
        assertEquals(ElementProvider.CARBON, atom5444.getElement());
    }

}