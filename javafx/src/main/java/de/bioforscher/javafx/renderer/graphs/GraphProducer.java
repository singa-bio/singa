package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.mathematics.graphs.model.Graph;
import javafx.scene.canvas.Canvas;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Christoph on 24/11/2016.
 */
public class GraphProducer implements Runnable {

    private final GraphCanvas canvas;
    private ConcurrentLinkedQueue<Graph> queue;
    private Graph graph;
    private int totalIterations;

    public GraphProducer(ConcurrentLinkedQueue<Graph> queue, Graph graph, GraphCanvas canvas, int iterations) {
        this.queue = queue;
        this.graph = graph;
        this.canvas = canvas;
        this.totalIterations = iterations;
    }

    @Override
    public void run() {
        GraphDrawingTool gdt = new GraphDrawingTool(this.totalIterations, this.graph, this.canvas);
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
