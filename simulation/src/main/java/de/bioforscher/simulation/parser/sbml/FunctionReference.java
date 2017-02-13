package de.bioforscher.simulation.parser.sbml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class FunctionReference {

    private String identifier;
    private String equation;
    private List<String> parameters;

    public FunctionReference(String identifier, String equation, List<String> parameters) {
        this.identifier = identifier;
        this.equation = equation;
        this.parameters = parameters;
    }

    public FunctionReference(String identifier, String functionMathString) {
        this.identifier = identifier;
        this.parameters = new ArrayList<>();
        // remove the lambda( ... )
        String content = functionMathString.substring(7, functionMathString.length() - 1);
        String[] split = content.split(",");
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                this.equation = split[i].trim();
            } else {
                this.parameters.add(split[i].trim());
            }
        }
    }

    public String replaceInEquation(String equationMathString) {
        String result = equationMathString;
        Pattern pattern = Pattern.compile(this.identifier + "\\((.[^\\)]+)\\)");
        Matcher matcher = pattern.matcher(equationMathString);
        while (matcher.find()) {
            // extract function parameters
            String[] functionParameters = matcher.group(1).split(",");
            // replace parameters with correct ones
            String preparedFunction = prepareEquation(functionParameters);
            // if not everything has to be replaced
            if (!matcher.group(0).equals(equationMathString)) {
                // add some braces
                preparedFunction = "(" + preparedFunction + ")";
            }
            // assign result
            result = result.replace(matcher.group(0), preparedFunction);
        }
        return result;
    }

    private String prepareEquation(String[] functionParameters) {
        String replacedFunction = this.equation;
        for (int i = 0; i < this.parameters.size(); i++) {
            replacedFunction = replacedFunction.replace(this.parameters.get(i), functionParameters[i].trim());
        }
        return replacedFunction;
    }


}
