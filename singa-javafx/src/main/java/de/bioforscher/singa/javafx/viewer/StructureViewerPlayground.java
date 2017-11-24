package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.structure3d.Structure3D;
import de.bioforscher.singa.javafx.geometry.AbacusVisualization;
import de.bioforscher.singa.mathematics.algorithms.geometry.Abacus;
import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.structure.model.oak.*;
import javafx.application.Application;

import java.io.IOException;
import java.util.List;

/**
 * @author fk
 */
public class StructureViewerPlayground {

    public static void main(String[] args) throws IOException {

        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;

        final Species species = ChEBIParserService.parse("CHEBI:15422");

        final Structure3D feature = species.getFeature(Structure3D.class);
        OakStructure structure = new OakStructure();
        OakModel model = new OakModel(1);
        OakChain chain = new OakChain("A");
        chain.addLeafSubstructure((OakLigand)feature.getFeatureContent());
        model.addChain(chain);
        structure.addModel(model);
        StructureViewer.structure = structure;

        final List<Sphere> spheres = Structures.convertToSpheres(structure);
        StructureViewer.spheres = spheres;

        Abacus abacus = new Abacus();
        abacus.setSpheres(spheres);
        abacus.calculate();
        AbacusVisualization visualization = new AbacusVisualization(abacus.getSlices(), abacus.getScale(), abacus.getxMin(), abacus.getyMin(), abacus.getzMin());
        StructureViewer.cubes = visualization.getZSlice(50);



        Application.launch(StructureViewer.class);
    }

}
