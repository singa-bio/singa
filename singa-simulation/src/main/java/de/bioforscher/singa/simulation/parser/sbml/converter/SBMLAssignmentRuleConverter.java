package de.bioforscher.singa.simulation.parser.sbml.converter;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AppliedExpression;
import de.bioforscher.singa.simulation.model.rules.AssignmentRule;
import de.bioforscher.singa.simulation.parser.sbml.FunctionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Unit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class SBMLAssignmentRuleConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLAssignmentRuleConverter.class);

    // requirements
    private final Map<String, Unit<?>> units;
    private final Map<String, ChemicalEntity> entities;

    private final SBMLExpressionConverter expressionConverter;

    public SBMLAssignmentRuleConverter(Map<String, Unit<?>> units, Map<String, ChemicalEntity> entities, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.units = units;
        this.entities = entities;
        expressionConverter = new SBMLExpressionConverter(units, functions, globalParameters);
    }

    public AssignmentRule convertAssignmentRule(org.sbml.jsbml.AssignmentRule sbmlAssignmentRule) {
        String unitIdentifier = sbmlAssignmentRule.getDerivedUnitDefinition().getId();
        Unit<?> parameterUnit;
        if (unitIdentifier.equalsIgnoreCase("dimensionless") || unitIdentifier.isEmpty()) {
            parameterUnit = ONE;
        } else {
            parameterUnit = units.get(unitIdentifier);
        }
        final AppliedExpression appliedExpression = expressionConverter.convertRawExpression(sbmlAssignmentRule.getMath(), parameterUnit);
        final ChemicalEntity targetEntity = entities.get(sbmlAssignmentRule.getVariable());
        AssignmentRule assignmentRule = new AssignmentRule(targetEntity, appliedExpression);
        // find referenced entities
        for (String identifier : entities.keySet()) {
            Pattern pattern = Pattern.compile("(\\W|^)(" + identifier + ")(\\W|$)");
            Matcher matcher = pattern.matcher(sbmlAssignmentRule.getMath().toString());
            if (matcher.find()) {
                assignmentRule.referenceChemicalEntityToParameter(identifier, entities.get(identifier));
            }
        }
        return assignmentRule;
    }

}
