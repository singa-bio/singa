package bio.singa.simulation.model.modules.displacement;

import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class DisplacementDelta {

    /**
     * The module, that calculated this delta.
     */
    private final DisplacementBasedModule module;

    /**
     * The delta vector.
     */
    private final Vector2D deltaVector;

    public DisplacementDelta(DisplacementBasedModule module, Vector2D deltaVector) {
        this.module = module;
        this.deltaVector = deltaVector;
    }

    public DisplacementBasedModule getModule() {
        return module;
    }

    public Vector2D getDeltaVector() {
        return deltaVector;
    }

    @Override
    public String toString() {
        return "DisplacementDelta{" +
                "module=" + module +
                ", deltaVector=" + deltaVector +
                '}';
    }
}
