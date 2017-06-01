package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AppliedExpression;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import de.bioforscher.singa.units.UnitProvider;
import de.bioforscher.singa.units.quantities.MolarConcentration;
import de.bioforscher.singa.units.quantities.ReactionRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class DynamicKineticLaw implements KineticLaw {

    private static final Logger logger = LoggerFactory.getLogger(DynamicKineticLaw.class);

    private AppliedExpression expression;
    private Map<ChemicalEntity, String> entityReference;

    private double appliedScale = 70;

    public DynamicKineticLaw(AppliedExpression expression) {
        this.expression = expression;
        this.entityReference = new HashMap<>();
    }

    public AppliedExpression getExpression() {
        return this.expression;
    }

    public void referenceChemicalEntityToParameter(String parameterIdentifier, ChemicalEntity entity) {
        this.entityReference.put(entity, parameterIdentifier);
        // FIXME this is not done correctly
        this.expression.setParameter(new SimulationParameter<>(parameterIdentifier, Quantities.getQuantity(0.0, MOLE_PER_LITRE)));
    }

    public Map<ChemicalEntity, String> getEntityReference() {
        return this.entityReference;
    }

    public double getAppliedScale() {
        return this.appliedScale;
    }

    public void setAppliedScale(double appliedScale) {
        this.appliedScale = appliedScale;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : this.entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = node.getAvailableConcentration(entry.getKey(), section);
            final String parameterName = this.entityReference.get(entry.getKey());
            this.expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        // FIXME scale depending on time step
        return Quantities.getQuantity(this.expression.evaluate().getValue().doubleValue() / this.appliedScale, UnitProvider.PER_SECOND);
    }

    @Override
    public void prepareAppliedRateConstants() {
        // FIXME scale depending on time step
    }

    @Override
    public List<KineticParameterType> getRequiredParameters() {
        return null;
    }

}
