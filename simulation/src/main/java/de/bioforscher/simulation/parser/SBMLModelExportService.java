package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.GraphAutomata;
import de.bioforscher.simulation.util.BioGraphUtilities;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitName;
import de.bioforscher.units.UnitPrefix;
import de.bioforscher.units.UnitUtilitys;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SBMLModelExportService {

    private static final Logger log = Logger.getLogger(SBMLModelExportService.class.getName());

    private Document document;
    private Element root;

    private GraphAutomata automata;

    public SBMLModelExportService(GraphAutomata automata) throws ParserConfigurationException {
        log.log(Level.FINER, "Setting up xml documnent.");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        this.document = docBuilder.newDocument();
        this.automata = automata;
        createRootElement();
    }

    private void createRootElement() {
        log.log(Level.FINER, "Creating sbml root.");
        this.root = this.document.createElement("sbml");
        this.root.setAttribute("xmlns", "http://www.sbml.org/sbml/level3/version1/core");
        this.root.setAttribute("level", "3");
        this.root.setAttribute("version", "1");
        this.document.appendChild(this.root);
    }

    private void appendModel() {
        Element model = this.document.createElement("model");
        model.appendChild(createUnitDefinitions());
        model.appendChild(createParameters());
        model.appendChild(createCompartments());
        model.appendChild(createSpecies());
        this.root.appendChild(model);
    }

    private Element createUnitDefinitions() {
        log.log(Level.FINER, "Creating unit definitions for SBML.");
        Element unitDefinitions = this.document.createElement("listOfUnitDefinitions");
        unitDefinitions.appendChild(
                createOneDimensionalUnitDefinition(EnvironmentalVariables.getInstance().getNodeDistance()));
        unitDefinitions.appendChild(
                createOneDimensionalUnitDefinition(EnvironmentalVariables.getInstance().getTimeStep()));
        unitDefinitions.appendChild(
                createOneDimensionalUnitDefinition(EnvironmentalVariables.getInstance().getSystemTemperature()));
        unitDefinitions.appendChild(
                createMultiDimensionalUnitDefinition(EnvironmentalVariables.getInstance().getSystemViscosity()));
        return unitDefinitions;
    }

    private Element createOneDimensionalUnitDefinition(Quantity<?> quantity) {
        UnitName distanceUnitName = UnitUtilitys.getUnitNameFromUnit(quantity.getUnit());
        UnitPrefix distancePrefix = UnitUtilitys.getUnitPrefixFromUnit(quantity.getUnit());

        Element nodeDistanceUnitDefinition = this.document.createElement("unitDefinition");
        nodeDistanceUnitDefinition.setAttribute("id", quantity.getUnit().toString());

        Element units = this.document.createElement("listOfUnits");

        Element distanceUnit = this.document.createElement("unit");
        distanceUnit.setAttribute("kind", distanceUnitName.toString().toLowerCase());
        if (distancePrefix != UnitPrefix.NO_PREFIX) {
            distanceUnit.setAttribute("scale", String.valueOf(distancePrefix.getScale()));
        }

        units.appendChild(distanceUnit);
        nodeDistanceUnitDefinition.appendChild(units);

        return nodeDistanceUnitDefinition;
    }

    private Element createMultiDimensionalUnitDefinition(Quantity<?> quantity) {

        Element nodeDistanceUnitDefinition = this.document.createElement("unitDefinition");
        nodeDistanceUnitDefinition.setAttribute("id",
                UnitUtilitys.formatMultidimensionalUnit(quantity.getUnit()));

        Element units = this.document.createElement("listOfUnits");

        UnitPrefix globalPrefix = UnitUtilitys.getUnitPrefixFromDivisor(quantity.getUnit());
        boolean usedGlaobalPrefix = false;

        Map<? extends Unit<?>, Integer> unitsMap = quantity.getUnit().getProductUnits();
        for (Unit<?> unit : unitsMap.keySet()) {
            UnitName unitName = UnitUtilitys.getUnitNameFromUnit(unit);
            UnitPrefix prefix = UnitUtilitys.getUnitPrefixFromUnit(unit);
            Element componentUnit = this.document.createElement("unit");
            componentUnit.setAttribute("kind", unitName.toString().toLowerCase());
            if (prefix != UnitPrefix.NO_PREFIX) {
                componentUnit.setAttribute("scale", String.valueOf(prefix.getScale()));
            }
            if (!usedGlaobalPrefix) {
                componentUnit.setAttribute("scale", String.valueOf(globalPrefix.getScale()));
                usedGlaobalPrefix = true;
            }
            if (unitsMap.get(unit) != 1) {
                componentUnit.setAttribute("exponent", unitsMap.get(unit).toString());
            }
            units.appendChild(componentUnit);
        }

        nodeDistanceUnitDefinition.appendChild(units);

        return nodeDistanceUnitDefinition;
    }

    private Element createParameters() {
        log.log(Level.FINER, "Creating global parameters for SBML.");
        Element parameters = this.document.createElement("listOfParameters");

        Element nodeDistanceParameter = this.document.createElement("parameter");
        nodeDistanceParameter.setAttribute("id", "NodeDistance");
        nodeDistanceParameter.setAttribute("value",
                String.valueOf(EnvironmentalVariables.getInstance().getNodeDistance().getValue().doubleValue()));
        nodeDistanceParameter.setAttribute("units",
                EnvironmentalVariables.getInstance().getNodeDistance().getUnit().toString());

        parameters.appendChild(nodeDistanceParameter);

        Element timeStepParameter = this.document.createElement("parameter");
        timeStepParameter.setAttribute("id", "TimeStep");
        timeStepParameter.setAttribute("value",
                String.valueOf(EnvironmentalVariables.getInstance().getTimeStep().getValue().doubleValue()));
        timeStepParameter.setAttribute("units",
                EnvironmentalVariables.getInstance().getTimeStep().getUnit().toString());

        parameters.appendChild(timeStepParameter);

        Element systemTemperatureParameter = this.document.createElement("parameter");
        systemTemperatureParameter.setAttribute("id", "SystemTemperature");
        systemTemperatureParameter.setAttribute("value",
                String.valueOf(EnvironmentalVariables.getInstance().getSystemTemperature().getValue().doubleValue()));
        systemTemperatureParameter.setAttribute("units",
                EnvironmentalVariables.getInstance().getSystemTemperature().getUnit().toString());

        parameters.appendChild(systemTemperatureParameter);

        Element systemViscosityParameter = this.document.createElement("parameter");
        systemViscosityParameter.setAttribute("id", "SystemViscosity");
        systemViscosityParameter.setAttribute("value",
                String.valueOf(EnvironmentalVariables.getInstance().getSystemViscosity().getValue().doubleValue()));
        systemViscosityParameter.setAttribute("units", UnitUtilitys.formatMultidimensionalUnit(
                EnvironmentalVariables.getInstance().getSystemViscosity().getUnit()));

        parameters.appendChild(systemViscosityParameter);

        return parameters;

    }

    private Element createCompartments() {
        log.log(Level.FINER, "Creating compartments for SBML.");
        Element compartments = this.document.createElement("listOfCompartments");
        Element compartment = this.document.createElement("compartment");
        compartment.setAttribute("id", "cell");
        compartment.setAttribute("spatialDimensions", "3");
        compartments.appendChild(compartment);
        return compartments;
    }

    private Element createSpecies() {
        log.log(Level.FINER, "Creating species for SBML.");
        Element listOfSpecies = this.document.createElement("listOfSpecies");
        Map<String, ChemicalEntity> speciesMap = BioGraphUtilities.generateMapOfEntities(automata.getGraph());
        for (String speciesName : speciesMap.keySet()) {
            Element species = this.document.createElement("species");
            species.setAttribute("id", speciesMap.get(speciesName).getIdentifier().toString());
            species.setAttribute("name", speciesName);
            species.setAttribute("compartment", "cell");
            species.setAttribute("hasOnlySubstanceUnits", "false");
            species.setAttribute("boundaryCondition", "true");
            species.setAttribute("constant", "false");
            listOfSpecies.appendChild(species);
        }
        return listOfSpecies;
    }

    public void exportToFile(File file) throws TransformerException {
        log.log(Level.FINE, "Writing SBML with global parameters of the model to " + file.getPath() + ".");
        appendModel();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(this.document);
        StreamResult result = new StreamResult(file);
        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
    }

}
