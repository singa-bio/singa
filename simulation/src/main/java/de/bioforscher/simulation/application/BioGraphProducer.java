package de.bioforscher.simulation.application;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.modules.model.Simulation;
import de.bioforscher.simulation.util.GraphDrawingTool;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BioGraphProducer implements Runnable {

    private ConcurrentLinkedQueue<AutomatonGraph> queue;
    private Rectangle rectangle;
    private AutomatonGraph graph;
    private Simulation simulation;
    private int totalIterations;

    public BioGraphProducer(ConcurrentLinkedQueue<AutomatonGraph> queue, AutomatonGraph graph, int iterations) {
        this.queue = queue;
        this.graph = graph;
        this.rectangle = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
        this.totalIterations = iterations;
    }

    @Override
    public void run() {
        GraphDrawingTool gdt = new GraphDrawingTool(this.totalIterations, this.rectangle, this.graph);
        for (int i = 0; i < this.totalIterations; i++) {
            this.queue.add(gdt.arrangeGraph(i));
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
