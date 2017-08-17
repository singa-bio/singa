package de.bioforscher.singa.simulation.parser.sbml.converter;

import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AppliedExpression;
import de.bioforscher.singa.simulation.modules.reactions.implementations.DynamicKineticLaw;
import de.bioforscher.singa.simulation.parser.sbml.FunctionReference;
import org.sbml.jsbml.KineticLaw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Unit;
import java.util.Map;

import static tec.units.ri.AbstractUnit.ONE;

/**
 * Converts JSBML KineticLaws to SiNGA KineticLaws
 *
 * @author cl
 */
public class SBMLKineticLawConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLKineticLawConverter.class);

    // requirements
    private Map<String, Unit<?>> units;

    private SBMLExpressionConverter expressionConverter;

    public SBMLKineticLawConverter(Map<String, Unit<?>> units, Map<String, FunctionReference> functions, Map<String, SimulationParameter<?>> globalParameters) {
        this.units = units;
        this.expressionConverter = new SBMLExpressionConverter(this.units, functions, globalParameters);
    }

    public DynamicKineticLaw convertKineticLaw(KineticLaw sbmlKineticLaw) {
        if (!sbmlKineticLaw.getMath().toString().equals("NaN")) {
            String unitIdentifier = sbmlKineticLaw.getDerivedUnitDefinition().getId();
            Unit<?> parameterUnit;
            if (unitIdentifier.equalsIgnoreCase("dimensionless") || unitIdentifier.isEmpty()) {
                parameterUnit = ONE;
            } else {
                parameterUnit = this.units.get(unitIdentifier);
            }
            logger.debug("Creating kinetic law with expression {} ...", sbmlKineticLaw.getMath().toString());
            AppliedExpression appliedExpression = this.expressionConverter.convertRawExpression(sbmlKineticLaw.getMath(), sbmlKineticLaw.getListOfLocalParameters(), parameterUnit);
            return new DynamicKineticLaw(appliedExpression);
        } else {
            logger.warn("Could not parse a valid expression for this reaction.");
            return null;
        }
    }


}
