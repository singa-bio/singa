package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.elements.ElementProvider;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class MmtfAtomTest {

    private static Structure structure1C0A;
    private static Atom atom412;
    private static Atom atom5444;
    private static Atom atomToModify;

    @BeforeClass
    public static void prepareData() throws IOException {
        structure1C0A = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("1C0A"));
        // no offset to regular pdb file
        atom412 = structure1C0A.getAtom(412).get();
        // one offset to regular pdb file
        atom5444 = structure1C0A.getAtom(5444).get();

        atomToModify = structure1C0A.getAtom(600).get();
    }

    @Test
    public void getIdentifier() {
        assertEquals(412, (int) atom412.getAtomIdentifier());
        assertEquals(5444, (int) atom5444.getAtomIdentifier());
    }

    @Test
    public void getAtomName() {
        assertEquals("N2", atom412.getAtomName());
        assertEquals("CA", atom5444.getAtomName());
    }

    @Test
    public void getPosition() {
        assertEquals(new Vector3D(62.20399856567383, -8.505999565124512, 22.573999404907227), atom412.getPosition());
        assertEquals(new Vector3D(70.31099700927734, 35.60300064086914, -10.967000007629395), atom5444.getPosition());
    }

    @Test
    public void setPosition() {
        assertEquals(new Vector3D(64.802001953125, 24.027000427246094, 34.83599853515625), atomToModify.getPosition());
        atomToModify.setPosition(new Vector3D(1.0, 1.0, 1.0));
        // state change
        assertEquals(new Vector3D(1.0, 1.0, 1.0), atomToModify.getPosition());
        // get it again from the structure
        atomToModify = structure1C0A.getAtom(600).get();
        assertEquals(new Vector3D(1.0, 1.0, 1.0), atomToModify.getPosition());
    }

    @Test
    public void getElement() {
        assertEquals(ElementProvider.NITROGEN, atom412.getElement());
        assertEquals(ElementProvider.CARBON, atom5444.getElement());
    }

}