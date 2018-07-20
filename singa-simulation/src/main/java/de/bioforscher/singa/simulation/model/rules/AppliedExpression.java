package de.bioforscher.singa.simulation.model.rules;

import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;
import uk.co.cogitolearning.cogpar.*;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Applied expressions encapsulate formulas in given in String form. Additionally {@link SimulationParameter}s
 * need to be defined that are substituted into the expression and resolved for their values.
 *
 * @author cl
 */
public class AppliedExpression {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AppliedExpression.class);

    /**
     * The expression that is evaluated.
     */
    private final ExpressionNode expression;

    /**
     * The original string of the expression.
     */
    private final String expressionString;

    /**
     * The resulting unit of the evaluated expression.
     */
    private final Unit<?> resultUnit;

    /**
     * The parameters to be substituted into the expression.
     */
    private final Map<String, SimulationParameter> parameters;

    /**
     * Creates an expression from the given string.
     *
     * @param expression The string that is converted into the expression;
     * @param resultUnit The unit resulting from the calculation.
     */
    public AppliedExpression(String expression, Unit<?> resultUnit) {
        ExpressionParser parser = new ExpressionParser();
        expressionString = expression;
        this.expression = parser.parse(expression);
        parameters = new HashMap<>();
        this.resultUnit = resultUnit;
    }

    /**
     * Returns the parameters of the expression.
     * @return The parameters of the expression.
     */
    public Map<String, SimulationParameter> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Sets a parameter of the expression.
     * @param parameter A parameter that is substituted into the expression
     */
    public void setParameter(SimulationParameter parameter) {
        final SetVariable variable = new SetVariable(parameter.getIdentifier().toString(), parameter.getValue());
        parameters.put(parameter.getIdentifier().toString(), parameter);
        expression.accept(variable);
    }

    /**
     * Returns the expression string.
     * @return The expression string.
     */
    public String getExpressionString() {
        return expressionString;
    }

    /**
     * Replaces a value for the parameter with the given identifier. The parameter needs to be set first.
     * @param parameter The parameter to set.
     * @param value The new value of the parameter.
     */
    public void acceptValue(String parameter, double value) {
        final SetVariable variable = new SetVariable(parameter, value);
        if (parameters.containsKey(parameter)) {
            parameters.get(parameter).setValue(value);
            expression.accept(variable);
        }
    }

    /**
     * Evaluates the expression and returns the result. If not all parameters have been set or the expression evaluates
     * to NaN an error is logged.
     * @return The result of the evaluated expression.
     */
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
