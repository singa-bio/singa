package de.bioforscher.simulation.application;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ExperimentalTikZConverter {

    public static void exportGraphToTikZ(AutomatonGraph graph) {

        StringBuilder sb = new StringBuilder();

        Enzyme gfp = new Enzyme.Builder("P42212").name("GFP").molarMass(26886.0).build();

        for (BioNode node : graph.getNodes()) {
            if (node.getConcentration(gfp).getValue().doubleValue() == 0.0) {
                sb.append("\\node[q1 node] ");
            } else {
                sb.append("\\node[q2 node] ");
            }
            sb.append("(").append(node.getIdentifier()).append(") at (").append(node.getPosition().getX() / 20 * 1.5).append(",").append(node.getPosition().getY() / 20 * 1.5).append(") {};\n");
        }

        sb.append("\n");

        for (BioEdge edge : graph.getEdges()) {
            sb.append("\\draw (").append(edge.getSource().getIdentifier()).append(") -- (").append(edge.getTarget().getIdentifier()).append(");\n");
        }

        sb.append("\n");

        Path path = Paths.get("data/tikz_rendering.txt");
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
