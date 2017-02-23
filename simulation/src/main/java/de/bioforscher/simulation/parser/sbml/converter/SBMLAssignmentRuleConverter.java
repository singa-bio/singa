package de.bioforscher.simulation.parser.sbml.converter;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.rules.AppliedExpression;
import de.bioforscher.simulation.model.rules.AssignmentRule;
import de.bioforscher.simulation.model.parameters.SimulationParameter;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
import de.bioforscher.simulation.parser.sbml.FunctionReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Unit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tec.units.ri.AbstractUnit.ONE;

/**
 * @author cl
 */
public class SBMLAssignmentRuleConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLAssignmentRuleConverter.class);

    // requirements
    private Map<String, Unit<?>> units;
    private Map<String, ChemicalEntity> entities;

    private SBMLExpressionConverter expressionConverter;

    public SBMLAssignmentRuleConverter(Map<String, Unit<?>> units, Map<String, ChemicalEntity> entities, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.units = units;
        this.entities = entities;
        this.expressionConverter = new SBMLExpressionConverter(units, functions, globalParameters);
    }

    public AssignmentRule convertAssignmentRule(org.sbml.jsbml.AssignmentRule sbmlAssignmentRule) {
        String unitIdentifier = sbmlAssignmentRule.getDerivedUnitDefinition().getId();
        Unit<?> parameterUnit;
        if (unitIdentifier.equalsIgnoreCase("dimensionless") || unitIdentifier.isEmpty()) {
            parameterUnit = ONE;
        } else {
            parameterUnit = this.units.get(unitIdentifier);
        }
        final AppliedExpression appliedExpression = this.expressionConverter.convertRawExpression(sbmlAssignmentRule.getMath(), parameterUnit);
        final ChemicalEntity targetEntity = this.entities.get(sbmlAssignmentRule.getVariable());
        AssignmentRule assignmentRule = new AssignmentRule(targetEntity, appliedExpression);
        // find referenced entities
        for (String identifier : this.entities.keySet()) {
            Pattern pattern = Pattern.compile("(\\W|^)(" + identifier + ")(\\W|$)");
            Matcher matcher = pattern.matcher(sbmlAssignmentRule.getMath().toString());
            if (matcher.find()) {
                assignmentRule.referenceChemicalEntityToParameter(identifier, this.entities.get(identifier));
            }
        }
        return assignmentRule;
    }

}
