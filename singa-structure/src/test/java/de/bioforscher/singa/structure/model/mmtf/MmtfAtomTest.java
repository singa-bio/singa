package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
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

    private static Atom atom412;
    private static Atom atom5444;

    @BeforeClass
    public static void prepareData() throws IOException {
        Structure structure1C0A = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("1C0A"));
        // no offset to regular pdb file
        atom412 = structure1C0A.getAtom(412).get();
        // one offset to regular pdb file
        atom5444 = structure1C0A.getAtom(5444).get();
    }

    @Test
    public void getIdentifier() throws Exception {
        assertEquals(412, (int) atom412.getIdentifier());
        assertEquals(5444, (int) atom5444.getIdentifier());
    }

    @Test
    public void getAtomName() throws Exception {
        assertEquals("N2", atom412.getAtomName());
        assertEquals("CA", atom5444.getAtomName());
    }

    @Test
    public void getPosition() throws Exception {
        assertEquals(new Vector3D(62.20399856567383, -8.505999565124512, 22.573999404907227), atom412.getPosition());
        assertEquals(new Vector3D(70.31099700927734, 35.60300064086914, -10.967000007629395), atom5444.getPosition());
    }

    @Test
    public void getElement() throws Exception {
        assertEquals(ElementProvider.NITROGEN, atom412.getElement());
        assertEquals(ElementProvider.CARBON, atom5444.getElement());
    }

}