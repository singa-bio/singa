package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.Enzyme;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations.MichaelisMentenWithKCat;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.EntityDependentKineticParameter;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameter;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;
import de.bioforscher.singa.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.singa.units.quantities.ReactionRate;

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
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        return this.kineticLaw.calculateAcceleration(node, section);
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
