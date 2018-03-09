package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.sequence.algorithms.alignment.NeedlemanWunschGraph;
import de.bioforscher.singa.sequence.model.ProteinSequence;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import javafx.application.Application;

/**
 * @author cl
 */
public class DynamicProgrammingVisualizer {

    public static void main(String[] args) {

        ProteinSequence first = ProteinSequence.of("ASTHILM");
        ProteinSequence second = ProteinSequence.of("ASTGLM");

        NeedlemanWunschGraph graph = new NeedlemanWunschGraph(SubstitutionMatrix.BLOSUM_45, first, second);

        GraphDisplayApplication.graph = graph.getGraph();
        Application.launch(GraphDisplayApplication.class);
    }

}
