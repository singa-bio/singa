package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
import de.bioforscher.singa.simulation.modules.reactions.model.CatalyticReactant;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

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
        return this.catalyticReactants;
    }

    public void setCatalyticReactants(List<CatalyticReactant> catalyticReactants) {
        this.catalyticReactants = catalyticReactants;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        return this.kineticLaw.calculateAcceleration(node, section);
    }

}
