package de.bioforscher.simulation.modules.reactions.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations.MichaelisMentenWithKCat;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.EntityDependentKineticParameter;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameter;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import de.bioforscher.simulation.modules.reactions.model.Reaction;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class BiochemicalReaction extends Reaction {

    private Enzyme enzyme;
    private KineticLaw kineticLaw;

    public BiochemicalReaction(KineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
        this.kineticLaw.prepareAppliedRateConstants();
    }

    public BiochemicalReaction(Enzyme enzyme) {
        this.enzyme = enzyme;
        this.kineticLaw = new MichaelisMentenWithKCat(enzyme.getSubstrates().iterator().next(), enzyme,
                new EntityDependentKineticParameter<>(KineticParameterType.MICHAELIS_CONSTANT,
                        enzyme.getMichaelisConstant(), enzyme.getSubstrates().iterator().next()),
                new KineticParameter<>(KineticParameterType.CATALYTIC_CONSTANT, enzyme.getTurnoverNumber()));
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        return this.kineticLaw.calculateAcceleration(node);
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        Set<ChemicalEntity<?>> referencedEntities = this.getStoichiometricReactants().stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toSet());
        referencedEntities.add(this.enzyme);
        return referencedEntities;
    }

}
