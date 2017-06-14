package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.UnitScaler;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.EntityDependentKineticParameter;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameter;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.singa.features.units.UnitProvider.PER_SECOND;

/**
 * @author cl
 */
public final class MichaelsMentenWithKM implements KineticLaw {

    private static final List<KineticParameterType> requiredParameters = new ArrayList<>();
    public static final KineticParameterType KM = addKineticParameter(KineticParameterType.MICHAELIS_CONSTANT);
    public static final KineticParameterType VMAX = addKineticParameter(KineticParameterType.MAXIMAL_VELOCITY);

    private ChemicalEntity substrate;
    private EntityDependentKineticParameter<MolarConcentration> km;
    private KineticParameter<ReactionRate> vMax;
    private Quantity<ReactionRate> appliedVMax;

    public MichaelsMentenWithKM(ChemicalEntity substrate) {
        this.substrate = substrate;
    }

    public MichaelsMentenWithKM(ChemicalEntity substrate, EntityDependentKineticParameter<MolarConcentration> km,
                                KineticParameter<ReactionRate> vMax) {
        this.substrate = substrate;
        this.km = km;
        this.vMax = vMax;
        prepareAppliedRateConstants();
    }

    private static KineticParameterType addKineticParameter(KineticParameterType parameterType) {
        requiredParameters.add(parameterType);
        return parameterType;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        // (VMAX * substrate) / KM + substrate
        double substrate = node.getAvailableConcentration(this.substrate, section).getValue().doubleValue();
        return Quantities.getQuantity(
                (this.appliedVMax.getValue().doubleValue() * substrate)
                        / (this.km.getValue().getValue().doubleValue() + substrate), PER_SECOND);
    }

    @Override
    public void prepareAppliedRateConstants() {
        this.appliedVMax = UnitScaler.rescaleReactionRate(this.vMax.getValue(),
                EnvironmentalParameters.getInstance().getTimeStep());
    }


    @Override
    public List<KineticParameterType> getRequiredParameters() {
        return requiredParameters;
    }

    public ChemicalEntity getSubstrate() {
        return this.substrate;
    }

    public void setSubstrate(ChemicalEntity substrate) {
        this.substrate = substrate;
    }

    public EntityDependentKineticParameter<MolarConcentration> getKm() {
        return this.km;
    }

    public void setKm(EntityDependentKineticParameter<MolarConcentration> km) {
        this.km = km;
    }

    public KineticParameter<ReactionRate> getVMax() {
        return this.vMax;
    }

    public void setVMax(KineticParameter<ReactionRate> vMax) {
        this.vMax = vMax;
    }

}
