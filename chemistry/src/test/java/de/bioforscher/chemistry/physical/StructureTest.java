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
        this.trp = new SubStructure(0);
        this.trp.addNode(new Atom(1, ElementProvider.NITROGEN, AtomName.N, new Vector3D(109.622, 32.123, 34.299)));
        this.trp.addNode(new Atom(2, ElementProvider.CARBON, AtomName.CA, new Vector3D(110.230, 30.823, 34.018)));
        this.trp.addNode(new Atom(3, ElementProvider.CARBON, AtomName.C, new Vector3D(111.129, 30.378, 35.173)));
    }

    @Test
    public void shouldConnectCloseAtoms() {
        this.trp.connectByDistance();
        System.out.println(this.trp);
    }

    @Test
    public void handleSubstructures() {

        // global structure
        Structure s = new Structure();

        // add single atoms to structure
        Atom a1 = new Atom(0, ElementProvider.HYDROGEN, AtomName.H, new Vector3D(0,0,1));
        Atom a2 = new Atom(1, ElementProvider.HYDROGEN, AtomName.H, new Vector3D(0,0,2));
        Atom a3 = new Atom(2, ElementProvider.HYDROGEN, AtomName.H, new Vector3D(0,0,3));
        s.addNode(a1);
        s.addNode(a2);
        s.addNode(a3);
        // and connect them with bonds
        s.connect(0, a1, a2, Bond.class);
        s.connect(1, a2, a3, Bond.class);
        s.connect(2, a1, a3, Bond.class);

        // substructure
        SubStructure ss1 = new SubStructure(3);
        // add single atoms to sub structure
        Atom a4 = new Atom(3, ElementProvider.CARBON, AtomName.C, new Vector3D(0,1,0));
        Atom a5 = new Atom(4, ElementProvider.CARBON, AtomName.C, new Vector3D(0,2,0));
        Atom a6 = new Atom(5, ElementProvider.CARBON, AtomName.C, new Vector3D(0,3,0));
        ss1.addNode(a4);
        ss1.addNode(a5);
        ss1.addNode(a6);
        // and connect them with bonds
        ss1.connect(0, a4, a5, Bond.class);
        ss1.connect(1, a5, a6, Bond.class);
        ss1.connect(2, a4, a6, Bond.class);

        // add substructure
        s.addSubstructure(ss1);
        // and connect atom to stom within substructure
        s.connect(3, a1, a4, Bond.class);

        System.out.println(s.toString());

    }

}