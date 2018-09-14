package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.rules.AppliedExpression;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.cogitolearning.cogpar.*;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic kinetic laws allow for the definition of reaction kinetics based on equations. Equations are supplied to the
 * Kinetic law as {@link AppliedExpression}s.
 *
 * @author cl
 */
public class KineticLaw {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AppliedExpression.class);

    /**
     * The expression that is evaluated.
     */
    private final ExpressionNode expression;

    /**
     * The features used as parameters
     */
    private Map<String, ScalableQuantityFeature> featureMap;

    /**
     * The concentrations used as parameters
     */
    private Map<String, Reactant> concentrationMap;

    public KineticLaw(String law) {
        ExpressionParser parser = new ExpressionParser();
        expression = parser.parse(law);
        featureMap = new HashMap<>();
        concentrationMap = new HashMap<>();
    }

    public void referenceReactant(String parameterIdentifier, Reactant reactant) {
        concentrationMap.put(parameterIdentifier, reactant);
    }

    public void referenceReactant(Reactant reactant) {
        concentrationMap.put(reactant.getEntity().getIdentifier().toString(), reactant);
    }

    public void referenceFeature(String parameterIdentifier, ScalableQuantityFeature feature) {
        featureMap.put(parameterIdentifier, feature);
    }

    public void referenceFeature(ScalableQuantityFeature feature) {
        featureMap.put(feature.getSymbol(), feature);
    }

    public void referenceConstant(String parameterIdentifier, double constant) {
        expression.accept(new SetVariable(parameterIdentifier, constant));
    }

    public Map<String, ScalableQuantityFeature> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, ScalableQuantityFeature> featureMap) {
        this.featureMap = featureMap;
    }

    public Map<String, Reactant> getConcentrationMap() {
        return concentrationMap;
    }

    public void setConcentrationMap(Map<String, Reactant> concentrationMap) {
        this.concentrationMap = concentrationMap;
    }

    /**
     * Calculates the velocity of the reaction based on the entities in the concentration container.
     *
     * @param concentrationContainer The concentration container.
     * @return The velocity.
     */
    public double calculateVelocity(ConcentrationContainer concentrationContainer, boolean isStrutCalculation) {
        // set feature parameters
        for (Map.Entry<String, ScalableQuantityFeature> featureEntry : featureMap.entrySet()) {
            Quantity<?> featureQuantity;
            if (isStrutCalculation) {
                featureQuantity = featureEntry.getValue().getHalfScaledQuantity();
            } else {
                featureQuantity = featureEntry.getValue().getScaledQuantity();
            }
            featureEntry.getValue().getFeatureContent().getValue().doubleValue();
            SetVariable variable = new SetVariable(featureEntry.getKey(), featureQuantity.getValue().doubleValue());
            expression.accept(variable);
        }
        // set concentration parameters
        for (Map.Entry<String, Reactant> reactantEntry : concentrationMap.entrySet()) {
            Quantity<MolarConcentration> concentration = concentrationContainer.get(reactantEntry.getValue().getPrefferedTopology(), reactantEntry.getValue().getEntity());
            SetVariable variable = new SetVariable(reactantEntry.getKey(), concentration.getValue().doubleValue());
            expression.accept(variable);
        }
        // calculate
        return evaluate();
    }


    /**
     * Evaluates the expression and returns the result. If not all parameters have been set or the expression evaluates
     * to NaN an error is logged.
     * @return The result of the evaluated expression.
     */
    private double evaluate() {
        double value = 0.0;
        try {
            return expression.getValue();
        } catch (ParserException | EvaluationException e) {
            logger.error("Could not calculate expression for {}.", expression.toString(), e);
        }
        if (Double.isNaN(value)) {
            logger.error("Could not calculate expression for {}, value was NaN.", expression.toString());
        }
        return 0.0;
    }

}
