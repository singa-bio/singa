package de.bioforscher.simulation.application;

import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.GraphAutomata;
import de.bioforscher.simulation.util.GraphDrawingTool;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BioGraphProducer implements Runnable {

    private ConcurrentLinkedQueue<AutomatonGraph> queue;
    private Rectangle rectangle;
    private AutomatonGraph graph;
    private GraphAutomata automata;
    private Jobs job;
    private int totalIterations;

    public BioGraphProducer(ConcurrentLinkedQueue<AutomatonGraph> queue, AutomatonGraph graph, GraphAutomata automata,
                            Rectangle rectangle, Jobs job, int iterations) {
        this.queue = queue;
        this.graph = graph;
        this.automata = automata;
        this.rectangle = rectangle;
        this.job = job;
        this.totalIterations = iterations;
    }

    public BioGraphProducer(ConcurrentLinkedQueue<AutomatonGraph> queue, AutomatonGraph graph, GraphAutomata automata,
                            double width, double height, Jobs job) {
        this.queue = queue;
        this.graph = graph;
        this.automata = automata;
        this.rectangle = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
        this.job = job;
        this.totalIterations = 0;
    }

    public BioGraphProducer(ConcurrentLinkedQueue<AutomatonGraph> queue, AutomatonGraph graph, double width,
                            double height, Jobs job, int iterations) {
        this.queue = queue;
        this.graph = graph;
        this.rectangle = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
        this.job = job;
        this.totalIterations = iterations;
    }

    @Override
    public void run() {

        if (this.job == Jobs.ARRANGE) {
            GraphDrawingTool gdt = new GraphDrawingTool(this.totalIterations, this.rectangle, this.graph);
            for (int i = 0; i < this.totalIterations; i++) {
                this.queue.add(gdt.arrangeGraph(i));
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (this.job == Jobs.SIMULATE) {
            while (true) {
                this.queue.add(this.automata.next());
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
