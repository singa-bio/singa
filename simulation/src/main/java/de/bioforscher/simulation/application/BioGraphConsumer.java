package de.bioforscher.simulation.application;

import de.bioforscher.simulation.application.renderer.GraphRenderer;
import de.bioforscher.simulation.model.AutomatonGraph;
import javafx.animation.AnimationTimer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BioGraphConsumer extends AnimationTimer {

    ConcurrentLinkedQueue<AutomatonGraph> queue;
    GraphRenderer renderer;

    public BioGraphConsumer(ConcurrentLinkedQueue<AutomatonGraph> queue, GraphRenderer renderer) {
        this.queue = queue;
        this.renderer = renderer;
    }

    @Override
    public void handle(long arg0) {
        AutomatonGraph g;
        while ((g = this.queue.poll()) != null) {
            this.renderer.drawBio(g);
        }

    }

}
