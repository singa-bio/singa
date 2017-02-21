package de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model;

import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author leberech
 */
public interface KineticLaw {

    Quantity<ReactionRate> calculateAcceleration(BioNode node);

    void prepareAppliedRateConstants();

    List<KineticParameterType> getRequiredParameters();

}
