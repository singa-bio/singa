package de.bioforscher.simulation.parser.sbml;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.ComplexedChemicalEntity;
import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.chebi.ChEBIParserService;
import de.bioforscher.chemistry.parser.uniprot.UniProtParserService;
import de.bioforscher.core.identifier.ChEBIIdentifier;
import de.bioforscher.core.identifier.SimpleStringIdentifier;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.core.identifier.model.Identifier;
import de.bioforscher.simulation.modules.reactions.implementations.DynamicReaction;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
import de.bioforscher.simulation.modules.reactions.model.CatalyticReactant;
import de.bioforscher.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import org.sbml.jsbml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class SBMLParser {

    private static final Logger logger = LoggerFactory.getLogger(SBMLParser.class);

    private SBMLDocument document;

    // results
    private Map<String, ChemicalEntity> entities;
    private List<DynamicReaction> reactions;
    private Map<ChemicalEntity, Double> startingConcentrations;
    private Map<String, Double> globalParameters;

    private Map<Identifier, ChemicalEntity> entitiesByPrimaryId;
    private Map<Identifier, ChemicalEntity> entitiesByDatabaseId;

    private DynamicReaction currentReaction;
    private DynamicKineticLaw currentKineticLaw;
    private Map<String, FunctionReference> functions;

    public SBMLParser(InputStream inputStream) {
        this.entities = new HashMap<>();
        this.entitiesByPrimaryId = new HashMap<>();
        this.entitiesByDatabaseId = new HashMap<>();
        this.startingConcentrations = new HashMap<>();
        this.reactions = new ArrayList<>();
        this.globalParameters = new HashMap<>();
        this.functions = new HashMap<>();
        initializeDocument(inputStream);
    }

    private void initializeDocument(InputStream inputStream) {
        SBMLReader reader = new SBMLReader();
        logger.info("Parsing SBML...");
        try {
            this.document = reader.readSBMLFromStream(inputStream);
        } catch (XMLStreamException e) {
            logger.error("Could not read SBML File.");
            e.printStackTrace();
        }
    }

    public Map<String, ChemicalEntity> getChemicalEntities() {
        return this.entities;
    }

    public List<DynamicReaction> getReactions() {
        return this.reactions;
    }

    public Map<ChemicalEntity, Double> getStartingConcentrations() {
        return this.startingConcentrations;
    }

    public Map<String, Double> getGlobalParameters() {
        return this.globalParameters;
    }

    public void parse() {
        parseGlobalParameters();
        parseFunctions();
        parseSpecies();
        parseReactions();
        parseStartingConcentrations();
    }

    private void parseSpecies() {
        logger.info("Parsing Chemical Entity Data ...");
        this.document.getModel().getListOfSpecies().forEach(species -> {
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

    private void parseFunctions() {
        this.document.getModel().getListOfFunctionDefinitions().forEach(function ->
                this.functions.put(function.getId(), new FunctionReference(function.getId(), function.getMath().toString()))
        );
    }

    private void parseReactions() {
        logger.info("Parsing Reaction Data ...");
        this.document.getModel().getListOfReactions().forEach(reaction -> {
            logger.debug("Parsing Reaction {} ...", reaction.getId());
            // kinetics
            KineticLaw kineticLawSBML = reaction.getKineticLaw();
            // supply math with replaced functions
            logger.debug("raw kinetic law {} ...", kineticLawSBML.getMath().toString());
            String kineticLawExpression = prepareKineticLaw(kineticLawSBML.getMath().toString());
            logger.debug("Creating kinetic law with expression {} ...", kineticLawExpression);
            if (!kineticLawExpression.equals("NaN")) {
                this.currentKineticLaw = new DynamicKineticLaw(kineticLawExpression);
                // create reaction
                this.currentReaction = new DynamicReaction(this.currentKineticLaw);
                // assign local parameters
                assignLocalParameters(kineticLawSBML.getListOfLocalParameters());
                // our substrates are their reactants
                assignSubstrates(reaction.getListOfReactants());
                // and products
                assignProducts(reaction.getListOfProducts());
                // assign modifiers
                assignModifiers(reaction.getListOfModifiers());
                // add reaction
                this.reactions.add(this.currentReaction);
            } else {
                logger.warn("Could not parse a valid expression for this reaction.");
            }
            logger.debug("Parsed Reaction:{}", this.currentReaction.getDisplayString());
        });
    }
    
    private void parseStartingConcentrations() {
        logger.info("Parsing initial concentrations ...");
        this.document.getModel().getListOfSpecies().forEach(species -> {
            ChemicalEntity entity = this.entities.get(species.getId());
            if (species.getId().equals("ATP")) {
                System.out.println();
            }
            this.startingConcentrations.put(entity, species.getInitialConcentration());
        });
    }

    private void parseGlobalParameters() {
        logger.info("Parsing global parameters ...");
        this.document.getModel().getListOfParameters().forEach(parameter -> {
            logger.info("Set parameter {} to {}.", parameter.getId(), parameter.getValue());
            this.globalParameters.put(parameter.getId(), parameter.getValue());
        });
    }

    private void assignLocalParameters(ListOf<LocalParameter> localParameters) {
        for (LocalParameter parameter : localParameters) {
            logger.debug("Assigning local parameter {} to {}.", parameter.getId(), parameter.getValue());
            this.currentKineticLaw.setLocalParameter(parameter.getId(), parameter.getValue());
        }
    }

    private void assignSubstrates(ListOf<SpeciesReference> substrates) {
        for (SpeciesReference reference : substrates) {
            logger.debug("Assigning Chemical Entity {} as substrate.", reference.getSpecies());
            String identifier = reference.getSpecies();
            this.currentKineticLaw.referenceChemicalEntityToParameter(identifier, this.entities.get(identifier));
            this.currentReaction.getStoichiometricReactants().add(new StoichiometricReactant(this.entities.get(identifier), ReactantRole.DECREASING, 1));
        }
    }

    private void assignProducts(ListOf<SpeciesReference> products) {
        for (SpeciesReference reference : products) {
            logger.debug("Assigning Chemical Entity {} as product.", reference.getSpecies());
            String identifier = reference.getSpecies();
            this.currentKineticLaw.referenceChemicalEntityToParameter(identifier, this.entities.get(identifier));
            this.currentReaction.getStoichiometricReactants().add(new StoichiometricReactant(this.entities.get(identifier), ReactantRole.INCREASING, 1));
        }
    }

    private void assignModifiers(ListOf<ModifierSpeciesReference> modifiers) {
        for (ModifierSpeciesReference reference : modifiers) {
            logger.debug("Assigning Chemical Entity {} as catalytic modifier.", reference.getSpecies());
            String identifier = reference.getSpecies();
            this.currentKineticLaw.referenceChemicalEntityToParameter(identifier, this.entities.get(identifier));
            this.currentReaction.getCatalyticReactants().add(new CatalyticReactant(this.entities.get(identifier), ReactantRole.INCREASING));
        }
    }

    private String prepareKineticLaw(String kineticLawString) {
        String replacedFunctions = replaceFunction(kineticLawString);
        return replaceGlobalParameter(replacedFunctions);
    }

    private String replaceFunction(String kineticLawString) {
        String replacedString = kineticLawString;
        for (String functionIdentifier : this.functions.keySet()) {
            if (kineticLawString.contains(functionIdentifier)) {
                replacedString = this.functions.get(functionIdentifier).replaceInEquation(replacedString);
            }
        }
        return replacedString;
    }

    private String replaceGlobalParameter(String kineticLawString) {
        // TODO parameters can probably be handled better
        String replacedString = kineticLawString;
        for (String globalParameterName : this.globalParameters.keySet()) {
            Pattern pattern = Pattern.compile("(\\W|^)("+globalParameterName+")(\\W|$)");
            Matcher matcher = pattern.matcher(replacedString);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                // leave prefix and suffix alone only replace parameter identifier with actual value
                matcher.appendReplacement(sb, matcher.group(1) + String.valueOf(this.globalParameters.get(globalParameterName))+matcher.group(3));
            }
            matcher.appendTail(sb);
            replacedString = sb.toString();
        }
        return replacedString;
    }

    /**
     * Parses and adds a component using the first parsable resource in the given CVTerm.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerm     The CVTerm containing the resources.
     */
    private void parseAndAddSingularComponent(String identifier, CVTerm cvTerm, org.sbml.jsbml.Species species) {
        for (String resource : cvTerm.getResources()) {
            Optional<ChemicalEntity> entity = parseEntity(identifier, resource);
            if (entity.isPresent()) {
                this.entities.put(identifier, entity.get());
                return;
            }
        }
        this.entities.put(identifier, createReferenceEntity(species));
    }

    /**
     * Parses and adds a complex component using a CVTerm with multiple resources.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerm     The CVTerm containing the resources.
     */
    private void parseAndAddComplexComponent(String identifier, org.sbml.jsbml.Species species, CVTerm cvTerm) {
        ComplexedChemicalEntity complex = new ComplexedChemicalEntity.Builder(identifier).build();
        for (String resource : cvTerm.getResources()) {
            Optional<ChemicalEntity> chemicalEntity = parseEntity(resource);
            chemicalEntity.ifPresent(complex::addAssociatedPart);
        }
        if (complex.getAssociatedParts().size() > 1) {
            logger.debug("Parsed Chemical Entity as {}", complex);
            this.entities.put(identifier, complex);
        } else {
            ChemicalEntity referenceEntity = createReferenceEntity(species);
            this.entities.put(referenceEntity.getIdentifier().toString(), referenceEntity);
        }
    }

    private ChemicalEntity createReferenceEntity(org.sbml.jsbml.Species species) {
        species.getName();
        species.getId();
        return new Species.Builder(species.getId()).name(species.getName()).molarMass(1.0).build();
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
     * @param cvTerms    The CVTerms containing the resources.
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
     * @param complex  The complex to add to.
     * @param resource The resource to parse.
     */
    private void addPartToComplex(ComplexedChemicalEntity complex, String resource) {
        Optional<ChemicalEntity> chemicalEntity = parseEntity(resource);
        if (chemicalEntity.isPresent()) {
            if (!complex.getAssociatedChemicalEntities().contains(chemicalEntity.get())) {
                complex.addAssociatedPart(chemicalEntity.get());
            }
        }
    }

    /**
     * Checks certain things that can occur. If a complex has only one part only this part is added to the map of
     * entities. If a complex has no associated parts it is replaced by {@link Species#UNKNOWN_SPECIES}. Else it is
     * just added to the map of entities.
     *
     * @param identifier The identifier as referenced in the model.
     * @param complex    The complex to add to the map of entities.
     */
    private void checkAndAddComplexedChemicalEntity(String identifier, ComplexedChemicalEntity complex) {
        if (complex.getAssociatedChemicalEntities().size() == 1) {
            ChemicalEntity parsedPart = complex.getAssociatedParts().keySet().iterator().next();
            logger.debug("Parsed Chemical Entity as {}", parsedPart);
            this.entities.put(identifier, parsedPart);
        } else if (complex.getAssociatedChemicalEntities().isEmpty()) {
            Species species = new Species.Builder(identifier).molarMass(10.0).build();
            logger.debug("Parsed Chemical Entity as {}", species);
            this.entities.put(identifier, species);
        } else {
            logger.debug("Parsed Chemical Entity as {}", complex);
            this.entities.put(identifier, complex);
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
        SimpleStringIdentifier stringIdentifier = new SimpleStringIdentifier(primaryIdentifier);
        Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
        if (matcherChEBI.find()) {
            ChEBIIdentifier identifier = new ChEBIIdentifier(matcherChEBI.group(0));
            if (this.entitiesByPrimaryId.containsKey(stringIdentifier)) {
                logger.debug("Already parsed Chemical Entity for {}", primaryIdentifier);
                return Optional.of(this.entitiesByPrimaryId.get(stringIdentifier));
            } else {
                Species species = ChEBIParserService.parse(identifier.toString(), primaryIdentifier);
                this.entitiesByPrimaryId.put(stringIdentifier, species);
                this.entitiesByDatabaseId.put(identifier, species);
                return Optional.of(species);
            }
        }
        // try to parse as UniProt
        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
        if (matcherUniProt.find()) {
            UniProtIdentifier identifier = new UniProtIdentifier(matcherUniProt.group(0));
            if (this.entitiesByPrimaryId.containsKey(stringIdentifier)) {
                logger.debug("Already parsed Chemical Entity for {}", primaryIdentifier);
                return Optional.of(this.entitiesByPrimaryId.get(stringIdentifier));
            } else {
                Enzyme enzyme = UniProtParserService.parse(identifier.toString(), primaryIdentifier);
                this.entitiesByPrimaryId.put(stringIdentifier, enzyme);
                this.entitiesByDatabaseId.put(identifier, enzyme);
                return Optional.of(enzyme);
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
            if (this.entitiesByDatabaseId.containsKey(identifier)) {
                logger.debug("Already parsed Chemical Entity for {}", identifier);
                return Optional.of(this.entitiesByDatabaseId.get(identifier));
            } else {
                Species species = ChEBIParserService.parse(identifier.toString());
                this.entitiesByDatabaseId.put(identifier, species);
                return Optional.of(species);
            }
        }
        // try to parse as UniProt
        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
        if (matcherUniProt.find()) {
            UniProtIdentifier identifier = new UniProtIdentifier(matcherUniProt.group(0));
            if (this.entitiesByDatabaseId.containsKey(identifier)) {
                logger.debug("Already parsed Chemical Entity for {}", identifier);
                return Optional.of(this.entitiesByDatabaseId.get(identifier));
            } else {
                Enzyme enzyme = UniProtParserService.parse(identifier.toString());
                this.entitiesByDatabaseId.put(identifier, enzyme);
                return Optional.of(enzyme);
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
