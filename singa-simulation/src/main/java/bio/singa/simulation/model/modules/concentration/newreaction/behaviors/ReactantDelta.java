package bio.singa.simulation.model.modules.concentration.newreaction.behaviors;

import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;

/**
 * @author cl
 */
public class ReactantDelta {

    private final ConcentrationDeltaIdentifier identifier;
    private final double delta;

    public ReactantDelta(ConcentrationDeltaIdentifier identifier, double delta) {
        this.identifier = identifier;
        this.delta = delta;
    }

    public ConcentrationDeltaIdentifier getIdentifier() {
        return identifier;
    }

    public double getDelta() {
        return delta;
    }

}
