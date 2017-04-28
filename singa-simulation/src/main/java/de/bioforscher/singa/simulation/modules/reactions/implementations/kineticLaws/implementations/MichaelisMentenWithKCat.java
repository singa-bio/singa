package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.Enzyme;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.EntityDependentKineticParameter;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameter;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import de.bioforscher.singa.units.UnitScaler;
import de.bioforscher.singa.units.quantities.MolarConcentration;
import de.bioforscher.singa.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.units.UnitProvider.PER_SECOND;

/**
 * @author cl
 */
public class MichaelisMentenWithKCat implements KineticLaw {

    private static final List<KineticParameterType> requiredParameters = new ArrayList<>();
    public static final KineticParameterType KCAT = addKineticParameter(KineticParameterType.CATALYTIC_CONSTANT);
    public static final KineticParameterType VMAX = addKineticParameter(KineticParameterType.MAXIMAL_VELOCITY);

    private ChemicalEntity substrate;
    private Enzyme enzyme;
    private EntityDependentKineticParameter<MolarConcentration> km;
    private KineticParameter<ReactionRate> kCat;
    private Quantity<ReactionRate> appliedKCat;

    public MichaelisMentenWithKCat(ChemicalEntity substrate, Enzyme enzyme) {
        this.substrate = substrate;
        this.enzyme = enzyme;
    }

    public MichaelisMentenWithKCat(ChemicalEntity substrate, Enzyme enzyme,
                                   EntityDependentKineticParameter<MolarConcentration> km,
                                   KineticParameter<ReactionRate> kCat) {
        this.substrate = substrate;
        this.enzyme = enzyme;
        this.km = km;
        this.kCat = kCat;
        prepareAppliedRateConstants();
    }

    private static KineticParameterType addKineticParameter(KineticParameterType parameterType) {
        requiredParameters.add(parameterType);
        return parameterType;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        // (KCAT * enzyme * substrate) / KM + substrate
        double substrate = node.getAvailableConcentration(this.substrate, section).getValue().doubleValue();
        double enzyme = node.getAvailableConcentration(this.enzyme, section).getValue().doubleValue();
        return Quantities.getQuantity(
                (this.appliedKCat.getValue().doubleValue() * enzyme * substrate)
                        / (this.km.getValue().getValue().doubleValue() + substrate), PER_SECOND);
    }

    @Override
    public void prepareAppliedRateConstants() {
        this.appliedKCat = UnitScaler.rescaleReactionRate(this.kCat.getValue(),
                EnvironmentalParameters.getInstance().getTimeStep());
    }

    @Override
    public List<KineticParameterType> getRequiredParameters() {
        return null;
    }
}
