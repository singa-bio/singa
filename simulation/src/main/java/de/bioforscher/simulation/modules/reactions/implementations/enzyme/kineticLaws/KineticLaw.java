package de.bioforscher.simulation.modules.reactions.implementations.enzyme.kineticLaws;

import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.List;

/**
 * Created by Christoph on 14.07.2016.
 */
public interface KineticLaw {

    Quantity<ReactionRate> calculateAcceleration(BioNode node);

    List<KineticParameterType> getRequiredParameters();

}
