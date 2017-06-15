package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model;

import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author leberech
 */
public interface KineticLaw {

    Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section);

    void prepareAppliedRateConstants();

    List<KineticParameterType> getRequiredParameters();

}
