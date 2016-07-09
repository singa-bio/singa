package de.bioforscher.simulation.modules.reactions.model;

import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 08.07.2016.
 */
public abstract class Reaction {

    private List<StoichiometricReactant> stoichiometricReactants;

    public Reaction() {
        this.stoichiometricReactants = new ArrayList<>();
    }

    public List<StoichiometricReactant> getStoichiometricReactants() {
        return this.stoichiometricReactants;
    }

    public void setStoichiometricReactants(List<StoichiometricReactant> stoichiometricReactants) {
        this.stoichiometricReactants = stoichiometricReactants;
    }

    public abstract Quantity<ReactionRate> calculateAcceleration(BioNode node);

}
