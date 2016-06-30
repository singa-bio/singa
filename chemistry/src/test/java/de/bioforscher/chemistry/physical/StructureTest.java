package de.bioforscher.chemistry.physical;

import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.mathematics.vectors.Vector3D;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Christoph on 17.06.2016.
 */
public class StructureTest {

    private SubStructure trp;

    @Before
    public void shouldCreateSubstructure() {
        trp = new SubStructure();
        trp.addNode(new Atom(1, ElementProvider.NITROGEN, "N", new Vector3D(109.622, 32.123, 34.299)));
        trp.addNode(new Atom(2, ElementProvider.CARBON, "CA", new Vector3D(110.230, 30.823, 34.018)));
        trp.addNode(new Atom(3, ElementProvider.CARBON, "C", new Vector3D(111.129, 30.378, 35.173)));

    }

    @Test
    public void shouldConnectCloseAtoms() {
        trp.connectByDistance();
        System.out.println(trp);
    }

}