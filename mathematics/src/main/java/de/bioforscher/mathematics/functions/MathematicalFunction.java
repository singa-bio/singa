package de.bioforscher.mathematics.functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph on 09.08.2016.
 */
public class MathematicalFunction {

    private Map<String, Variable> variables;
    private Set<Term> components;

    public MathematicalFunction() {
        this.variables = new HashMap<>();
        this.components = new HashSet<>();
    }

    public Variable defineVariable(String name) {
        final Variable variable = new Variable(name);
        this.variables.put(name, variable);
        return variable;
    }



}
