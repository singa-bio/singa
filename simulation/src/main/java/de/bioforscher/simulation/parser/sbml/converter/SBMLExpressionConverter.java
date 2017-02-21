package de.bioforscher.simulation.parser.sbml.converter;

import de.bioforscher.simulation.model.parameters.SimulationParameter;
import de.bioforscher.simulation.model.rules.AppliedExpression;
import de.bioforscher.simulation.parser.sbml.FunctionReference;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;

import javax.measure.Unit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class SBMLExpressionConverter {

    private Map<String, Unit<?>> units;
    private Map<String, FunctionReference> functions;
    private Map<String, SimulationParameter<?>> globalParameters;

    private AppliedExpression currentExpression;

    public SBMLExpressionConverter(Map<String, Unit<?>> units, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.units = units;
        this.functions = functions;
        this.globalParameters = globalParameters;
    }

    public AppliedExpression convertRawExpression(ASTNode sbmlExpression, ListOf<LocalParameter> additionalParameters, Unit<?> resultUnit) {
        String expressionString = replaceFunction(sbmlExpression.toString());
        this.currentExpression = new AppliedExpression(expressionString, resultUnit);
        assignLocalParameters(additionalParameters);
        assignGlobalParameters(expressionString);
        return this.currentExpression;
    }

    public AppliedExpression convertRawExpression(ASTNode sbmlExpression, Unit<?> resultUnit) {
        String expressionString = replaceFunction(sbmlExpression.toString());
        this.currentExpression = new AppliedExpression(expressionString, resultUnit);
        assignGlobalParameters(expressionString);
        return this.currentExpression;
    }

    private String replaceFunction(String kineticLawString) {
        String replacedString = kineticLawString;
        for (String functionIdentifier : this.functions.keySet()) {
            if (kineticLawString.contains(functionIdentifier)) {
                replacedString = this.functions.get(functionIdentifier).replaceInEquation(replacedString);
            }
        }
        return replacedString;
    }

    private void assignLocalParameters(ListOf<LocalParameter> additionalParameters) {
        SBMLParameterConverter converter = new SBMLParameterConverter(this.units);
        for (LocalParameter parameter : additionalParameters) {
            this.currentExpression.setParameter(converter.convertLocalParameter(parameter));
        }
    }

    private void assignGlobalParameters(String kineticLawString) {
        for (String primaryIdentifier : this.globalParameters.keySet()) {
            Pattern pattern = Pattern.compile("(\\W|^)(" + primaryIdentifier + ")(\\W|$)");
            Matcher matcher = pattern.matcher(kineticLawString);
            if (matcher.find()) {
                this.currentExpression.setParameter(this.globalParameters.get(primaryIdentifier));
            }
        }
    }

}
