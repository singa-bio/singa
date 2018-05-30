package de.bioforscher.singa.simulation.parser.sbml;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.identifiers.model.Identifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AssignmentRule;
import de.bioforscher.singa.simulation.modules.reactions.implementations.DynamicReaction;
import de.bioforscher.singa.simulation.parser.sbml.converter.SBMLAssignmentRuleConverter;
import de.bioforscher.singa.simulation.parser.sbml.converter.SBMLParameterConverter;
import de.bioforscher.singa.simulation.parser.sbml.converter.SBMLReactionConverter;
import de.bioforscher.singa.simulation.parser.sbml.converter.SBMLUnitConverter;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.AbstractUnit;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author cl
 */
public class SBMLParser {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParser.class);
    private static final FeatureOrigin defaultOrigin = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION,
            "Defaulted during SBML Parsing, due to lack of information.", "none");
    // the controlpanles mapped to their sizes
    private final Map<CellSubsection, Double> compartments;
    // the chemical entities
    private final Map<String, ChemicalEntity> entities;
    // their starting concentrations
    private final Map<ChemicalEntity, Double> startingConcentrations;
    // a utility map to provide species by their database identifier
    private final Map<Identifier, ChemicalEntity> entitiesByDatabaseId;
    // the functions
    private final Map<String, FunctionReference> functions;
    // assignment rules
    private final List<AssignmentRule> assignmentRules;
    private SBMLDocument document;
    // the units
    private Map<String, Unit<?>> units;
    // the reactions
    private List<DynamicReaction> reactions;
    // the global parameters
    private Map<String, SimulationParameter<?>> globalParameters;

    public SBMLParser(InputStream inputStream) {
        entities = new HashMap<>();
        entitiesByDatabaseId = new HashMap<>();
        startingConcentrations = new HashMap<>();
        reactions = new ArrayList<>();
        globalParameters = new HashMap<>();
        functions = new HashMap<>();
        assignmentRules = new ArrayList<>();
        compartments = new HashMap<>();
        initializeDocument(inputStream);
    }

    private void initializeDocument(InputStream inputStream) {
        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        try {
            document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            logger.error("Could not read SBML File.");
            e.printStackTrace();
        }
    }

    public Map<String, ChemicalEntity> getChemicalEntities() {
        return entities;
    }

    public Map<CellSubsection, Double> getCompartments() {
        return compartments;
    }

    public List<DynamicReaction> getReactions() {
        return reactions;
    }

    public Map<ChemicalEntity, Double> getStartingConcentrations() {
        return startingConcentrations;
    }

    public Map<String, SimulationParameter<?>> getGlobalParameters() {
        return globalParameters;
    }

    public List<AssignmentRule> getAssignmentRules() {
        return assignmentRules;
    }

    public void parse() {
        parseUnits();
        parseGlobalParameters();
        parseCompartments();
        parseFunctions();
        parseSpecies();
        parseReactions();
        parseStartingConcentrations();
        parseAssignmentRules();
    }

    private void parseCompartments() {
        logger.info("Parsing Compartments ...");
        document.getModel().getListOfCompartments().forEach(compartment -> {
            CellSubsection singaCompartment = new CellSubsection(compartment.getId());
            compartments.put(singaCompartment, compartment.getSize());
            globalParameters.put(singaCompartment.getIdentifier(),
                    new SimulationParameter<>(singaCompartment.getIdentifier(),
                            Quantities.getQuantity(compartment.getSize(), AbstractUnit.ONE)));
        });
    }

    private void parseSpecies() {
        logger.info("Parsing Chemical Entity Data ...");
        document.getModel().getListOfSpecies().forEach(species -> {
            logger.debug("Parsing Chemical Entity {} ...", species.getId());
            // the annotations describe the entity used and is composed of CVTerms
            // each cv term is composed of
            // Qualifiers: the relationship between the entity and the resource
            // Resources: the actual links or data
            if (species.getAnnotation().getCVTermCount() == 1) {
                // only one annotation
                CVTerm term = species.getAnnotation().getCVTerm(0);
                if (term.getQualifier() == CVTerm.Qualifier.BQB_IS || term.getQualifier() == CVTerm.Qualifier.BQB_IS_VERSION_OF) {
                    // with only one "is" qualifier
                    if (term.getResourceCount() == 1) {
                        // and one resource
                        logger.debug("Chemical Entity {} is annotated with one {} resource", species.getId(), term.getQualifier().getElementNameEquivalent());
                        parseAndAddSingularComponent(species.getId(), term, species);
                    } else {
                        // and multiple resources
                        if (resourcesHaveTheSameOrigin(term)) {
                            // assuming here, that if the annotations are from different databases, they are alternatives
                            logger.debug("Chemical Entity {} is annotated with multiple {} resources from the same origin.", species.getId(), term.getQualifier().getElementNameEquivalent());
                            parseAndAddComplexComponent(species.getId(), species, term);
                        } else {
                            // and if they are from the same database they are a different parts of a complex entity
                            logger.debug("Chemical Entity {} is annotated with multiple {} resources from different sources.", species.getId(), term.getQualifier().getElementNameEquivalent());
                            parseAndAddSingularComponent(species.getId(), term, species);
                        }
                    }
                } else {
                    // with
                    if (term.getQualifier() == CVTerm.Qualifier.BQB_HAS_PART) {
                        // has part should have at least two components it is the only annotation
                        if (resourcesHaveTheSameOrigin(term)) {
                            logger.debug("Chemical Entity {} is annotated with multiple {} resources from the same origin.", species.getId(), term.getQualifier().getElementNameEquivalent());
                            parseAndAddComplexComponent(species.getId(), species, term);
                        } else {
                            logger.debug("Chemical Entity {} is annotated with multiple {} resources from different sources.", species.getId(), term.getQualifier().getElementNameEquivalent());
                            parseAndAddAllComponents(species.getId(), term);
                        }
                    }

                }
            } else {
                // multiple annotations
                logger.debug("Chemical Entity {} is annotated with multiple annotations.", species.getId());
                parseAndAddAllComponents(species.getId(), species.getAnnotation().getListOfCVTerms());
            }
        });
    }

    private void parseAssignmentRules() {
        logger.info("Parsing Assignment Rules ...");
        SBMLAssignmentRuleConverter converter = new SBMLAssignmentRuleConverter(units, entities, functions, globalParameters);
        document.getModel().getListOfRules().forEach(rule -> {
            if (rule.isAssignment()) {
                assignmentRules.add(converter.convertAssignmentRule((org.sbml.jsbml.AssignmentRule) rule));
            }
        });
    }

    private void parseFunctions() {
        logger.info("Parsing Functions ...");
        document.getModel().getListOfFunctionDefinitions().forEach(function ->
                functions.put(function.getId(), new FunctionReference(function.getId(), function.getMath().toString()))
        );
    }

    private void parseReactions() {
        logger.info("Parsing Reactions ...");
        SBMLReactionConverter converter = new SBMLReactionConverter(units, entities, functions, globalParameters);
        reactions = converter.convertReactions(document.getModel().getListOfReactions());
    }

    private void parseStartingConcentrations() {
        logger.info("Parsing initial concentrations ...");
        document.getModel().getListOfSpecies().forEach(species -> {
            ChemicalEntity entity = entities.get(species.getId());
            startingConcentrations.put(entity, species.getInitialConcentration());
        });
    }

    private void parseUnits() {
        logger.info("Parsing units ...");
        SBMLUnitConverter converter = new SBMLUnitConverter();
        units = converter.convertUnits(document.getModel().getListOfUnitDefinitions());
    }

    private void parseGlobalParameters() {
        logger.info("Parsing global parameters ...");
        SBMLParameterConverter converter = new SBMLParameterConverter(units);
        globalParameters = converter.convertSimulationParameters(document.getModel().getListOfParameters());
    }

    /**
     * Parses and adds a component using the first parsable resource in the given CVTerm.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerm The CVTerm containing the resources.
     */
    private void parseAndAddSingularComponent(String identifier, CVTerm cvTerm, org.sbml.jsbml.Species species) {
        for (String resource : cvTerm.getResources()) {
            Optional<ChemicalEntity> entity = parseEntity(identifier, resource);
            if (entity.isPresent()) {
                entities.put(identifier, entity.get());
                return;
            }
        }
        entities.put(identifier, createReferenceEntity(species));
    }

    /**
     * Parses and adds a complex component using a CVTerm with multiple resources.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerm The CVTerm containing the resources.
     */
    private void parseAndAddComplexComponent(String identifier, org.sbml.jsbml.Species species, CVTerm cvTerm) {
        ComplexedChemicalEntity complex = new ComplexedChemicalEntity.Builder(identifier).build();
        for (String resource : cvTerm.getResources()) {
            Optional<ChemicalEntity> chemicalEntity = parseEntity(resource);
            chemicalEntity.ifPresent(complex::addAssociatedPart);
        }
        if (complex.getAssociatedParts().size() > 1) {
            logger.debug("Parsed Chemical Entity as {}", complex);
            entities.put(identifier, complex);
        } else {
            ChemicalEntity referenceEntity = createReferenceEntity(species);
            entities.put(referenceEntity.getIdentifier().toString(), referenceEntity);
        }
    }

    private ChemicalEntity createReferenceEntity(org.sbml.jsbml.Species species) {
        return new SmallMolecule.Builder(species.getId())
                .name(species.getName())
                .assignFeature(new MolarMass(10, defaultOrigin))
                .build();
    }

    private void parseAndAddAllComponents(String identifier, CVTerm cvTerm) {
        ComplexedChemicalEntity complex = new ComplexedChemicalEntity.Builder(identifier).build();
        for (String resource : cvTerm.getResources()) {
            addPartToComplex(complex, resource);
        }
        checkAndAddComplexedChemicalEntity(identifier, complex);
    }

    /**
     * Parses and adds a complex component using a List of CVTerms.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerms The CVTerms containing the resources.
     */
    private void parseAndAddAllComponents(String identifier, List<CVTerm> cvTerms) {
        ComplexedChemicalEntity complex = new ComplexedChemicalEntity.Builder(identifier).build();
        cvTerms.forEach(term -> {
            if (term.getQualifier() == CVTerm.Qualifier.BQB_HAS_PART) {
                for (String resource : term.getResources()) {
                    addPartToComplex(complex, resource);
                }
            }
        });
        checkAndAddComplexedChemicalEntity(identifier, complex);
    }

    /**
     * Parses and adds the resource to the given complex.
     *
     * @param complex The complex to add to.
     * @param resource The resource to parse.
     */
    private void addPartToComplex(ComplexedChemicalEntity complex, String resource) {
        Optional<ChemicalEntity> chemicalEntityOptional = parseEntity(resource);
        chemicalEntityOptional.ifPresent(chemicalEntity -> {
            if (!complex.getAssociatedChemicalEntities().contains(chemicalEntity)) {
                complex.addAssociatedPart(chemicalEntity);
            }
        });
    }

    /**
     * Checks certain things that can occur. If a complex has only one part only this part is added to the map of
     * entities. If a complex has no associated parts it is replaced by {@link SmallMolecule#UNKNOWN_SPECIES}. Else it is
     * just added to the map of entities.
     *
     * @param identifier The identifier as referenced in the model.
     * @param complex The complex to add to the map of entities.
     */
    private void checkAndAddComplexedChemicalEntity(String identifier, ComplexedChemicalEntity complex) {
        if (complex.getAssociatedChemicalEntities().size() == 1) {
            ChemicalEntity parsedPart = complex.getAssociatedParts().keySet().iterator().next();
            logger.debug("Parsed Chemical Entity as {}", parsedPart);
            entities.put(identifier, parsedPart);
        } else if (complex.getAssociatedChemicalEntities().isEmpty()) {
            SmallMolecule species = new SmallMolecule.Builder(identifier)
                    .assignFeature(new MolarMass(10, defaultOrigin))
                    .build();
            logger.debug("Parsed Chemical Entity as {}", species);
            entities.put(identifier, species);
        } else {
            logger.debug("Parsed Chemical Entity as {}", complex);
            entities.put(identifier, complex);
        }
    }

    /**
     * Tries to parse an entity using the given resource. If it can not be parsed an empty Optional is returned.
     *
     * @param resource The resource to parse.
     * @return The parsed chemical entity, if a parser is available.
     */
    private Optional<ChemicalEntity> parseEntity(String primaryIdentifier, String resource) {
        // try to parse as ChEBI
        Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
        if (matcherChEBI.find()) {
            ChEBIIdentifier identifier = new ChEBIIdentifier(matcherChEBI.group(0));
            if (entities.containsKey(primaryIdentifier)) {
                logger.debug("Already parsed Chemical Entity for {}", primaryIdentifier);
                return Optional.of(entities.get(primaryIdentifier));
            } else {
                SmallMolecule species = ChEBIParserService.parse(identifier.toString(), primaryIdentifier);
                entities.put(primaryIdentifier, species);
                entitiesByDatabaseId.put(identifier, species);
                return Optional.of(species);
            }
        }
        // try to parse as UniProt
        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
        if (matcherUniProt.find()) {
            UniProtIdentifier identifier = new UniProtIdentifier(matcherUniProt.group(0));
            if (entities.containsKey(primaryIdentifier)) {
                logger.debug("Already parsed Chemical Entity for {}", primaryIdentifier);
                return Optional.of(entities.get(primaryIdentifier));
            } else {
                Protein protein = UniProtParserService.parse(identifier.toString(), primaryIdentifier);
                entities.put(primaryIdentifier, protein);
                entitiesByDatabaseId.put(identifier, protein);
                return Optional.of(protein);
            }
        }
        // no parser available
        return Optional.empty();
    }

    private Optional<ChemicalEntity> parseEntity(String resource) {
        // try to parse as ChEBI
        Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
        if (matcherChEBI.find()) {
            ChEBIIdentifier identifier = new ChEBIIdentifier(matcherChEBI.group(0));
            if (entitiesByDatabaseId.containsKey(identifier)) {
                logger.debug("Already parsed Chemical Entity for {}", identifier);
                return Optional.of(entitiesByDatabaseId.get(identifier));
            } else {
                SmallMolecule species = ChEBIParserService.parse(identifier.toString());
                entitiesByDatabaseId.put(identifier, species);
                return Optional.of(species);
            }
        }
        // try to parse as UniProt
        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
        if (matcherUniProt.find()) {
            UniProtIdentifier identifier = new UniProtIdentifier(matcherUniProt.group(0));
            if (entitiesByDatabaseId.containsKey(identifier)) {
                logger.debug("Already parsed Chemical Entity for {}", identifier);
                return Optional.of(entitiesByDatabaseId.get(identifier));
            } else {
                Protein protein = UniProtParserService.parse(identifier.toString());
                entitiesByDatabaseId.put(identifier, protein);
                return Optional.of(protein);
            }
        }
        // no parser available
        return Optional.empty();
    }

    /**
     * Checks, whether the resources in a CVTerm have the same origin.
     *
     * @param term The CVTerm to be checked
     * @return True, if the resources in a CVTerm have the same origin.
     */
    private boolean resourcesHaveTheSameOrigin(CVTerm term) {
        Set<String> origins = new HashSet<>();
        term.getResources().forEach(resource -> {
            // TODO: 06/11/2016 this can implemented better with some default method in the identifier package
            if (ChEBIIdentifier.PATTERN.matcher(resource).find()) {
                origins.add(ChEBIIdentifier.class.getName());
            } else if (UniProtIdentifier.PATTERN.matcher(resource).find()) {
                origins.add(UniProtIdentifier.class.getName());
            } else {
                origins.add(SimpleStringIdentifier.class.getName());
            }
        });
        // if the set contains only one element, all resources have the same origin
        return origins.size() == 1;
    }

}
