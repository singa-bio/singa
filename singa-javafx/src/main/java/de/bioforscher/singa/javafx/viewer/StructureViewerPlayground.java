package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import javafx.application.Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 */
public class StructureViewerPlayground {
    public static void main(String[] args) throws IOException {



//        OakStructure structure = (OakStructure) StructureParser.online()
//                .pdbIdentifier("1C0A")
//                .everything()
//                .parse();
//        StructureViewer.structure = null;
//        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;

        List<Sphere> spheres = new ArrayList<>();
        spheres.add(new Sphere(new Vector3D(1.0, 2.0, 3.0), 3.0));
        spheres.add(new Sphere(new Vector3D(4.0, 5.0, 6.0), 4.0));
        StructureViewer.spheres = spheres;

        Application.launch(StructureViewer.class);
    }
}
