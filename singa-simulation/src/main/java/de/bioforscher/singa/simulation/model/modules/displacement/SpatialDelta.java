package de.bioforscher.singa.simulation.model.modules.displacement;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class SpatialDelta {

    /**
     * The module, that calculated this delta.
     */
    private final DisplacementBasedModule module;

    /**
     * The delta vector.
     */
    private final Vector2D deltaVector;

    public SpatialDelta(DisplacementBasedModule module, Vector2D deltaVector) {
        this.module = module;
        this.deltaVector = deltaVector;
    }

    public DisplacementBasedModule getModule() {
        return module;
    }

    public Vector2D getDeltaVector() {
        return deltaVector;
    }

}
