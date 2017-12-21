package de.bioforscher.singa.simulation.model.rules;

import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;
import uk.co.cogitolearning.cogpar.*;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class AppliedExpression {

    private static final Logger logger = LoggerFactory.getLogger(AppliedExpression.class);

    private final ExpressionNode expression;
    private final String expressionString;
    private final Unit<?> resultUnit;
    private final Map<String, SimulationParameter> parameters;

    public AppliedExpression(String expression, Unit<?> resultUnit) {
        Parser parser = new Parser();
        expressionString = expression;
        this.expression = parser.parse(expression);
        parameters = new HashMap<>();
        this.resultUnit = resultUnit;
    }

    public Map<String, SimulationParameter> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void setParameters(List<SimulationParameter> parameters) {
        parameters.forEach(this::setParameter);
    }

    public void setParameter(SimulationParameter parameter) {
        final SetVariable variable = new SetVariable(parameter.getIdentifier().toString(), parameter.getValue());
        parameters.put(parameter.getIdentifier().toString(), parameter);
        expression.accept(variable);
    }

    public String getExpressionString() {
        return expressionString;
    }

    public void acceptValue(String parameter, double value) {
        final SetVariable variable = new SetVariable(parameter, value);
        if (parameters.containsKey(parameter)) {
            parameters.get(parameter).setValue(value);
            expression.accept(variable);
        }
    }

    public Quantity<?> evaluate() {
        double value = 0.0;
        try {
            value = expression.getValue();
        } catch (ParserException | EvaluationException e) {
            logger.error("Could not calculate expression for {}.", expression.toString(), e);
        }
        if (Double.isNaN(value)) {
            logger.error("Could not calculate expression for {}, value was NaN.", expression.toString());
        }
        return Quantities.getQuantity(value, resultUnit);
    }

}
