package de.bioforscher.simulation.modules.reactions.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
import de.bioforscher.simulation.modules.reactions.model.Reaction;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class DynamicReaction extends Reaction {

    private DynamicKineticLaw kineticLaw;

    public DynamicReaction(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
        this.kineticLaw.prepareAppliedRateConstants();
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        return this.kineticLaw.calculateAcceleration(node);
    }

    @Override
    public Set<ChemicalEntity> collectAllReferencedEntities() {
        return this.getStoichiometricReactants().stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toSet());
    }
}
