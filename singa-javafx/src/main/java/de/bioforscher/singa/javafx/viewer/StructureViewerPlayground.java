package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.javafx.geometry.AbacusVisualization;
import de.bioforscher.singa.mathematics.algorithms.geometry.Abacus;
import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.structure.algorithms.StructureToSphere;
import de.bioforscher.singa.structure.model.oak.OakStructure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import javafx.application.Application;

import java.io.IOException;
import java.util.List;

/**
 * @author fk
 */
public class StructureViewerPlayground {

    public static void main(String[] args) throws IOException {

        OakStructure structure = (OakStructure) StructureParser.online()
                .pdbIdentifier("4TRX")
                .model(1)
                .chainIdentifier("A")
                .parse();
        StructureViewer.structure = structure;
        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;

        final List<Sphere> spheres = StructureToSphere.convert(structure);
        StructureViewer.spheres = spheres;

        Abacus abacus = new Abacus(spheres);
        abacus.calcualte();
        AbacusVisualization visualization = new AbacusVisualization(abacus.getSlices(), abacus.getScale(), abacus.getxMin(), abacus.getyMin(), abacus.getzMin());
        StructureViewer.cubes = visualization.getZSlice(50);

        Application.launch(StructureViewer.class);
    }

}
