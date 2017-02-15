package de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.simulation.util.SimulationExampleProvider;
import de.bioforscher.units.UnitProvider;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;
import uk.co.cogitolearning.cogpar.*;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class DynamicKineticLaw implements KineticLaw {

    private static final Logger logger = LoggerFactory.getLogger(DynamicKineticLaw.class);

    private ExpressionNode expression;
    private List<SetVariable> localParameters;
    private Map<ChemicalEntity, String> entityReference;

    private String expressionString;

    private double appliedScale = 70;

    public DynamicKineticLaw(String expression) {
        Parser parser = new Parser();
        this.expressionString = expression;
        this.expression = parser.parse(expression);
        this.localParameters = new ArrayList<>();
        this.entityReference = new HashMap<>();
    }

    public void setLocalParameter(String parameterName, double value) {
        final SetVariable variable = new SetVariable(parameterName, value);
        this.localParameters.add(variable);
        this.expression.accept(variable);
    }

    public void referenceChemicalEntityToParameter(String parameterName, ChemicalEntity entity) {
        this.entityReference.put(entity, parameterName);
    }

    public Map<ChemicalEntity, String> getEntityReference() {
        return this.entityReference;
    }

    public void setEntityReference(Map<ChemicalEntity, String> entityReference) {
        this.entityReference = entityReference;
    }

    public double getAppliedScale() {
        return this.appliedScale;
    }

    public void setAppliedScale(double appliedScale) {
        this.appliedScale = appliedScale;
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : this.entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = node.getConcentration(entry.getKey());
            final String parameterName = this.entityReference.get(entry.getKey());
            this.expression.accept(new SetVariable(parameterName, concentration.getValue().doubleValue()));
        }

        double value = 0.0;
        try {
            value = this.expression.getValue();
        } catch (ParserException | EvaluationException e) {
            logger.error("Could not calculate acceleration for {}.", this.expression.toString(), e);
        }
        if (Double.isNaN(value)) {
            logger.error("Could not calculate acceleration for {}.", this.expression.toString());
        }
        return Quantities.getQuantity(value / this.appliedScale, UnitProvider.PER_SECOND);
    }

    @Override
    public void prepareAppliedRateConstants() {

    }

    @Override
    public List<KineticParameterType> getRequiredParameters() {
        return null;
    }

    @Override
    public String toString() {
        return "DynamicKineticLaw{" +
                "expressionString='" + this.expressionString + '\'' +
                '}';
    }
}
