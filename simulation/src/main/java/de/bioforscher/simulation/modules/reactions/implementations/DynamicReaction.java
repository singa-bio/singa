package de.bioforscher.simulation.modules.reactions.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
import de.bioforscher.simulation.modules.reactions.model.CatalyticReactant;
import de.bioforscher.simulation.modules.reactions.model.Reaction;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class DynamicReaction extends Reaction {

    private List<CatalyticReactant> catalyticReactants;
    private DynamicKineticLaw kineticLaw;

    public DynamicReaction(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
        this.catalyticReactants = new ArrayList<>();
        this.kineticLaw.prepareAppliedRateConstants();
    }

    public DynamicKineticLaw getKineticLaw() {
        return this.kineticLaw;
    }

    public void setKineticLaw(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    public List<CatalyticReactant> getCatalyticReactants() {
        return catalyticReactants;
    }

    public void setCatalyticReactants(List<CatalyticReactant> catalyticReactants) {
        this.catalyticReactants = catalyticReactants;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        Quantity<ReactionRate> reactionRateQuantity = this.kineticLaw.calculateAcceleration(node);
        System.out.println(reactionRateQuantity);
        return reactionRateQuantity;
    }

    @Override
    public Set<ChemicalEntity> collectAllReferencedEntities() {
        return this.getStoichiometricReactants().stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toSet());
    }
}
