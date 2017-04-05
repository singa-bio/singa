package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model;

import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.units.quantities.ReactionRate;

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
