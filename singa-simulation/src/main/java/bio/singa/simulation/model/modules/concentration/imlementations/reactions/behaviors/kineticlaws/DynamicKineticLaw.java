package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.exceptions.ModuleCalculationException;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionEvent;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas.ReactantConcentration;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.parameters.Parameter;
import tech.units.indriya.quantity.Quantities;
import uk.co.cogitolearning.cogpar.*;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tech.units.indriya.AbstractUnit.ONE;

/**
 * @author cl
 */
public class DynamicKineticLaw implements KineticLaw {

    private Reaction reaction;

    /**
     * The expression that is evaluated.
     */
    private final ExpressionNode expression;

    /**
     * The original string of the expression.
     */
    private final String expressionString;

    /**
     * The features influencing the reaction.
     */
    private Map<String, AbstractScalableQuantitativeFeature> featureMap;

    /**
     * The parameters remaining constant in the course of the simulation.
     */
    private Map<String, Parameter> parameterMap;

    /**
     * The reactants involved in the reaction.
     */
    private Map<String, Reactant> concentrationMap;

    public DynamicKineticLaw(Reaction reaction, String kineticLawString) {
        this.reaction = reaction;
        expressionString = kineticLawString;
        expression = new ExpressionParser().parse(kineticLawString);
        featureMap = new HashMap<>();
        concentrationMap = new HashMap<>();
        parameterMap = new HashMap<>();
    }

    public void referenceReactant(String parameterIdentifier, Reactant reactant) {
        concentrationMap.put(parameterIdentifier, reactant);
    }

    public void referenceReactant(Reactant reactant) {
        concentrationMap.put(reactant.getEntity().getIdentifier().toString(), reactant);
    }

    public void referenceFeature(String parameterIdentifier, AbstractScalableQuantitativeFeature feature) {
        featureMap.put(parameterIdentifier, feature);
    }

    public void referenceFeature(AbstractScalableQuantitativeFeature feature) {
        featureMap.put(feature.getDescriptor(), feature);
    }

    public void referenceConstant(String parameterIdentifier, double constant) {
        parameterMap.put(parameterIdentifier, new Parameter<>(parameterIdentifier, Quantities.getQuantity(constant, ONE), Evidence.NO_EVIDENCE));
        expression.accept(new SetVariable(parameterIdentifier, constant));
    }

    public void referenceConstant(String parameterIdentifier, double constant, Evidence evidence) {
        parameterMap.put(parameterIdentifier, new Parameter<>(parameterIdentifier, Quantities.getQuantity(constant, ONE), evidence));
        expression.accept(new SetVariable(parameterIdentifier, constant));
    }

    public void referenceParameter(Parameter<?> parameter) {
        parameterMap.put(parameter.getIdentifier(), parameter);
    }

    public Map<String, AbstractScalableQuantitativeFeature> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, AbstractScalableQuantitativeFeature> featureMap) {
        this.featureMap = featureMap;
    }

    public Map<String, Reactant> getConcentrationMap() {
        return concentrationMap;
    }

    public void setConcentrationMap(Map<String, Reactant> concentrationMap) {
        this.concentrationMap = concentrationMap;
    }

    public String getExpressionString() {
        return expressionString;
    }

    public Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Parameter> parameterMap) {
        this.parameterMap = parameterMap;
    }

    @Override
    public double determineVelocity(ReactionEvent reactionEvent) {
        // set features
        for (Map.Entry<String, AbstractScalableQuantitativeFeature> entry : featureMap.entrySet()) {
            double value;
            if (reaction.getSupplier().isStrutCalculation()) {
                value = entry.getValue().getHalfScaledQuantity();
            } else {
                value = entry.getValue().getScaledQuantity();
            }
            SetVariable variable = new SetVariable(entry.getKey(), value);
            expression.accept(variable);
        }
        // set parameters
        for (Map.Entry<String, Parameter> entry : parameterMap.entrySet()) {
            entry.getValue().scale();
            Quantity<?> parameterQuantity;
            if (reaction.getSupplier().isStrutCalculation()) {
                parameterQuantity = entry.getValue().getHalfScaledQuantity();
            } else {
                parameterQuantity = entry.getValue().getScaledQuantity();
            }
            SetVariable variable = new SetVariable(entry.getKey(), parameterQuantity.getValue().doubleValue());
            expression.accept(variable);
        }
        // set concentrations
        for (Map.Entry<String, Reactant> entry : concentrationMap.entrySet()) {
            Reactant reactant = entry.getValue();
            List<ReactantConcentration> concentrations = reactionEvent.getUpdatableBehavior().collectReactants(concentrationMap.values());
            double concentration = 0.0;
            for (ReactantConcentration reactantConcentration : concentrations) {
                if (reactantConcentration.getReactant().equals(reactant)) {
                    concentration = reactantConcentration.getConcentration();
                    break;
                }
            }
            if (reactant.getPreferredConcentrationUnit() != null) {
                concentration = UnitRegistry.concentration(concentration).to(reactant.getPreferredConcentrationUnit()).getValue().doubleValue();
            }
            SetVariable variable = new SetVariable(entry.getKey(), concentration);
            expression.accept(variable);
        }
        // calculate
        return evaluate();
    }

    /**
     * Evaluates the expression and returns the result. If not all parameters have been set or the expression evaluates
     * to NaN an error is logged.
     *
     * @return The result of the evaluated expression.
     */
    private double evaluate() {
        double value;
        try {
            value = expression.getValue();
        } catch (ParserException | EvaluationException e) {
            throw new ModuleCalculationException("Could not calculate expression" + expressionString + ". " + e.getMessage());
        }
        if (Double.isNaN(value)) {
            throw new ModuleCalculationException("Could not calculate expression for " + expressionString + ", value was NaN.");
        }
        return value;
    }

}
