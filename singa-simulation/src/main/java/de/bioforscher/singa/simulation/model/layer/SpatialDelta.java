package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.modules.model.VesicleModule;

/**
 * @author cl
 */
public class SpatialDelta {

    /**
     * The module, that calculated this delta.
     */
    private final VesicleModule module;

    /**
     * The delta vector.
     */
    private final Vector2D deltaVector;

    public SpatialDelta(VesicleModule module, Vector2D deltaVector) {
        this.module = module;
        this.deltaVector = deltaVector;
    }

    public VesicleModule getModule() {
        return module;
    }

    public Vector2D getDeltaVector() {
        return deltaVector;
    }

}
