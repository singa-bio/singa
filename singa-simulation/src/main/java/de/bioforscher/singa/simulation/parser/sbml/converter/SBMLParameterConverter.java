package de.bioforscher.singa.simulation.parser.sbml.converter;

import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Unit;
import java.util.HashMap;
import java.util.Map;

import static tec.units.ri.AbstractUnit.ONE;

/**
 * Converts JSBML Parameters to SiNGA Parameters.
 * @author cl
 */
public class SBMLParameterConverter {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParameterConverter.class);

    private Map<String, Unit<?>> units;

    public SBMLParameterConverter(Map<String, Unit<?>> units) {
        this.units = units;
    }

    public Map<String, SimulationParameter<?>> convertSimulationParameters(ListOf<Parameter> sbmlParameters) {
        Map<String, SimulationParameter<?>> parameters = new HashMap<>();
        for (Parameter parameter: sbmlParameters) {
            parameters.put(parameter.getId(), convertSimulationParameter(parameter));
        }
        return parameters;
    }

    public SimulationParameter<?> convertSimulationParameter(Parameter sbmlParameter) {
        return convertParameter(sbmlParameter.getId(), sbmlParameter.getValue(), sbmlParameter.getUnits());
    }

    public SimulationParameter<?> convertLocalParameter(LocalParameter sbmlLocalParameter) {
        return convertParameter(sbmlLocalParameter.getId(), sbmlLocalParameter.getValue(), sbmlLocalParameter.getUnits());
    }

    private SimulationParameter<?> convertParameter(String primaryIdentifier, double value, String unit) {
        Unit<?> parameterUnit;
        if (unit.equalsIgnoreCase("dimensionless") || unit.isEmpty()) {
            parameterUnit = ONE;
        } else {
            parameterUnit = units.get(unit);
        }
        SimulationParameter<?> simulationParameter = new SimulationParameter<>(primaryIdentifier,
                Quantities.getQuantity(value, parameterUnit));
        logger.debug("Set parameter {} to {}.", simulationParameter.getIdentifier(), simulationParameter.getQuantity());
        return simulationParameter;
    }




}
