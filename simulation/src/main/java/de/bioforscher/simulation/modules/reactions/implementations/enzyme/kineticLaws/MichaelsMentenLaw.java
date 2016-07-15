package de.bioforscher.simulation.modules.reactions.implementations.enzyme.kineticLaws;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static de.bioforscher.units.UnitDictionary.PER_SECOND;

/**
 * Created by Christoph on 15.07.2016.
 */
public final class MichaelsMentenLaw implements KineticLaw {

    private static final List<KineticParameterType> requiredParameters = new ArrayList<>();
    public static final KineticParameterType KM = addKineticParameter(KineticParameterType.MICHAELIS_CONSTANT);
    public static final KineticParameterType VMAX = addKineticParameter(KineticParameterType.MAXIMAL_VELOCITY);

    private ChemicalEntity substrate;
    private EntityDependentKineticParameter km;
    private KineticParameter vMax;

    public MichaelsMentenLaw(ChemicalEntity substrate) {
        this.substrate = substrate;
    }

    public MichaelsMentenLaw(ChemicalEntity substrate, EntityDependentKineticParameter km, KineticParameter vMax) {
        this.substrate = substrate;
        this.km = km;
        this.vMax = vMax;
        prepareAppliedRateConstants();
    }

    public void prepareAppliedRateConstants() {
        // TODO prepare all kinetic parameters that depend on time (scale them)
    }

    private static KineticParameterType addKineticParameter(KineticParameterType parameterType) {
        requiredParameters.add(parameterType);
        return parameterType;
    }

    public ChemicalEntity getSubstrate() {
        return this.substrate;
    }

    public void setSubstrate(ChemicalEntity substrate) {
        this.substrate = substrate;
    }

    public EntityDependentKineticParameter getKm() {
        return this.km;
    }

    public void setKm(EntityDependentKineticParameter km) {
        this.km = km;
    }

    public KineticParameter getVMax() {
        return this.vMax;
    }

    public void setVMax(KineticParameter vMax) {
        this.vMax = vMax;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        // (VMAX * substrate) / KM + substrate
        double substrate = node.getConcentration(this.substrate).getValue().doubleValue();
        return Quantities.getQuantity(
                (this.vMax.getValue() * substrate) / (this.km.getValue() + substrate), PER_SECOND);
    }

    @Override
    public List<KineticParameterType> getRequiredParameters() {
        return requiredParameters;
    }

}
