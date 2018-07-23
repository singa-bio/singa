package bio.singa.simulation.parser.sbml.converter;

import bio.singa.simulation.model.parameters.SimulationParameter;
import bio.singa.simulation.model.rules.AppliedExpression;
import bio.singa.simulation.parser.sbml.FunctionReference;
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

    private final Map<String, Unit<?>> units;
    private final Map<String, FunctionReference> functions;
    private final Map<String, SimulationParameter<?>> globalParameters;

    private AppliedExpression currentExpression;

    public SBMLExpressionConverter(Map<String, Unit<?>> units, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.units = units;
        this.functions = functions;
        this.globalParameters = globalParameters;
    }

    public AppliedExpression convertRawExpression(ASTNode sbmlExpression, ListOf<LocalParameter> additionalParameters, Unit<?> resultUnit) {
        String expressionString = replaceFunction(sbmlExpression.toString());
        currentExpression = new AppliedExpression(expressionString, resultUnit);
        assignLocalParameters(additionalParameters);
        assignGlobalParameters(expressionString);
        return currentExpression;
    }

    public AppliedExpression convertRawExpression(ASTNode sbmlExpression, Unit<?> resultUnit) {
        String expressionString = replaceFunction(sbmlExpression.toString());
        currentExpression = new AppliedExpression(expressionString, resultUnit);
        assignGlobalParameters(expressionString);
        return currentExpression;
    }

    private String replaceFunction(String kineticLawString) {
        String replacedString = kineticLawString;
        for (String functionIdentifier : functions.keySet()) {
            if (kineticLawString.contains(functionIdentifier)) {
                replacedString = functions.get(functionIdentifier).replaceInEquation(replacedString);
            }
        }
        return replacedString;
    }

    private void assignLocalParameters(ListOf<LocalParameter> additionalParameters) {
        SBMLParameterConverter converter = new SBMLParameterConverter(units);
        for (LocalParameter parameter : additionalParameters) {
            currentExpression.setParameter(converter.convertLocalParameter(parameter));
        }
    }

    private void assignGlobalParameters(String kineticLawString) {
        for (String primaryIdentifier : globalParameters.keySet()) {
            Pattern pattern = Pattern.compile("(\\W|^)(" + primaryIdentifier + ")(\\W|$)");
            Matcher matcher = pattern.matcher(kineticLawString);
            if (matcher.find()) {
                currentExpression.setParameter(globalParameters.get(primaryIdentifier));
            }
        }
    }

}
