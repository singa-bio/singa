package de.bioforscher.singa.simulation.parser.sbml;

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
        parameters = new ArrayList<>();
        // remove the lambda( ... )
        String content = functionMathString.substring(7, functionMathString.length() - 1);
        String[] split = content.split(",");
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                equation = split[i].trim();
            } else {
                parameters.add(split[i].trim());
            }
        }
    }

    public String replaceInEquation(String equationMathString) {
        Pattern pattern = Pattern.compile(identifier + "\\((.[^\\)]+)\\)");
        Matcher matcher = pattern.matcher(equationMathString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String[] functionParameters = matcher.group(1).split(",");
            // replace parameters with correct ones
            String preparedFunction = prepareEquation(functionParameters);
            // if not everything has to be replaced
            if (!matcher.group(0).equals(equationMathString)) {
                // add some braces
                preparedFunction = "(" + preparedFunction + ")";
            }
            // leave prefix and suffix alone only replace parameter identifier with actual value
            matcher.appendReplacement(sb, preparedFunction);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String prepareEquation(String[] functionParameters) {
        String replacedFunction = equation;
        for (int i = 0; i < parameters.size(); i++) {
            Pattern pattern = Pattern.compile("(\\W|^)(" + parameters.get(i) + ")(\\W|$)");
            Matcher matcher = pattern.matcher(replacedFunction);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1) +functionParameters[i].trim() + matcher.group(3));
            }
            matcher.appendTail(sb);
            replacedFunction = sb.toString();
        }
        return replacedFunction;
    }
}
