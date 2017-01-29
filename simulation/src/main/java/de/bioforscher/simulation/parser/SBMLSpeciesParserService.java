package de.bioforscher.simulation.parser;

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
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Christoph on 04/11/2016.
 */
public class SBMLSpeciesParserService {

    private static SBMLSpeciesParserService service;

    private SBMLDocument document;
    private HashMap<String, ChemicalEntity> entities;
    private HashMap<Identifier, ChemicalEntity> allreadyParsedEntities;

    private SBMLSpeciesParserService() {
        this.entities = new HashMap<>();
        this.allreadyParsedEntities = new HashMap<>();
    }

    public static HashMap<String, ChemicalEntity> parseStream(InputStream stream) {
        SBMLSpeciesParserService.service = new SBMLSpeciesParserService();
        SBMLReader reader = new SBMLReader();
        try {
            SBMLSpeciesParserService.service.document = reader.readSBMLFromStream(stream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        SBMLSpeciesParserService.service.parseSpecies();
        return SBMLSpeciesParserService.service.entities;
    }

    private void parseSpecies() {
        this.document.getModel().getListOfSpecies().forEach(species -> {
            System.out.println("Species: " + species.getId());
            // the annotations describe the entity used and is composed of CVTerms
            // each cv term is composed of
            // Qualifiers: the relationship between the entity and the resource
            // Resources: the actual links or data
            if (species.getAnnotation().getCVTermCount() == 1) {
                // only one annotation
                CVTerm term = species.getAnnotation().getCVTerm(0);
                if (term.getQualifier() == CVTerm.Qualifier.BQB_IS || term.getQualifier() == CVTerm.Qualifier.BQB_IS_VERSION_OF) {
                    // with only one "is" qualifier
                    System.out.print("  annotated as \"" + term.getQualifier().getElementNameEquivalent() + "\" with ");
                    if (term.getResourceCount() == 1) {
                        // and one resource
                        System.out.println("only " + term.getResourceCount() + " Resource");
                        parseAndAddSingularComponent(species.getId(), term);
                    } else {
                        // and multiple resources
                        // assuming here, that if the annotations are from different databases, they are alternatives
                        // and if they are from the same database they are a different parts of a complex entity
                        if (resourcesHaveTheSameOrigin(term)) {
                            System.out.println(term.getResourceCount() + " Resources one source");
                            parseAndAddComplexComponent(species.getId(), term);
                        } else {
                            System.out.println(term.getResourceCount() + " Resources from different sources");
                            parseAndAddSingularComponent(species.getId(), term);
                        }
                    }
                } else {
                    // with
                    if (term.getQualifier() == CVTerm.Qualifier.BQB_HAS_PART) {
                        // has part should have at least two components it is the only annotation
                        System.out.print("  annotated as \"" + term.getQualifier().getElementNameEquivalent() + "\" with ");
                        if (resourcesHaveTheSameOrigin(term)) {
                            System.out.println(term.getResourceCount() + " Resources one source");
                            parseAndAddComplexComponent(species.getId(), term);
                        } else {
                            System.out.println(term.getResourceCount() + " Resources from different sources");
                            parseAndAddAllComponents(species.getId(), term);
                        }
                    }

                }
            } else {
                // multiple annotations
                System.out.print("  annotated with multiple annotations");
                parseAndAddAllComponents(species.getId(), species.getAnnotation().getListOfCVTerms());
            }
            System.out.println();
        });
    }

    /**
     * Parses and adds a component using the first parsable resource in the given CVTerm.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerm     The CVTerm containing the resources.
     */
    private void parseAndAddSingularComponent(String identifier, CVTerm cvTerm) {
        for (String resource : cvTerm.getResources()) {
            Optional<ChemicalEntity> entity = parseEntity(resource);
            if (entity.isPresent()) {
                this.entities.put(identifier, entity.get());
                System.out.println("  -> parsed as " + entity.get());
                return;
            }
        }
        this.entities.put(identifier, Species.UNKNOWN_SPECIES);
        System.out.println("  -> could not parse " + identifier + " from any database, referencing " + Species.UNKNOWN_SPECIES);
    }

    /**
     * Parses and adds a complex component using a CVTerm with multiple resources.
     *
     * @param identifier The identifier as referenced in the model.
     * @param cvTerm     The CVTerm containing the resources.
     */
    private void parseAndAddComplexComponent(String identifier, CVTerm cvTerm) {
        ComplexedChemicalEntity complex = new ComplexedChemicalEntity.Builder(identifier).build();
        for (String resource : cvTerm.getResources()) {
            complex.addAssociatedPart(parseEntity(resource).orElse(Species.UNKNOWN_SPECIES));
        }
        System.out.println("  -> parsed as " + complex);
        this.entities.put(identifier, complex);
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
     * @param complex The complex to add to the map of entities.
     */
    private void checkAndAddComplexedChemicalEntity(String identifier, ComplexedChemicalEntity complex) {
        if (complex.getAssociatedChemicalEntities().size() == 1) {
            ChemicalEntity parsedPart = complex.getAssociatedParts().keySet().iterator().next();
            System.out.println("  -> parsed as " + parsedPart);
            this.entities.put(identifier, parsedPart);
        } else if (complex.getAssociatedChemicalEntities().isEmpty()) {
            System.out.println("  -> parsed as " + Species.UNKNOWN_SPECIES);
            this.entities.put(identifier, Species.UNKNOWN_SPECIES);
        } else {
            System.out.println("  -> parsed as " + complex);
            this.entities.put(identifier, complex);
        }
    }

    /**
     * Tries to parse an entity using the given resource. If it can not be parsed an empty Optional is returned.
     * @param resource The resource to parse.
     * @return The parsed chemical entity, if a parser is available.
     */
    private Optional<ChemicalEntity> parseEntity(String resource) {
        // try to parse as ChEBI
        Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
        if (matcherChEBI.find()) {
            ChEBIIdentifier identifier = new ChEBIIdentifier(matcherChEBI.group(0));
            if (this.allreadyParsedEntities.containsKey(identifier)) {
                return Optional.of(this.allreadyParsedEntities.get(identifier));
            } else {
                Species species = ChEBIParserService.parse(identifier.toString());
                this.allreadyParsedEntities.put(identifier, species);
                return Optional.of(species);
            }
        }
        // try to parse as UniProt
        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
        if (matcherUniProt.find()) {
            UniProtIdentifier identifier = new UniProtIdentifier(matcherUniProt.group(0));
            if (this.allreadyParsedEntities.containsKey(identifier)) {
                return Optional.of(this.allreadyParsedEntities.get(identifier));
            } else {
                Enzyme enzyme = UniProtParserService.parse(identifier.toString());
                this.allreadyParsedEntities.put(identifier, enzyme);
                return Optional.of(enzyme);
            }
        }
        // no parser available
        return Optional.empty();
    }

    /**
     * Checks, whether the resources in a CVTerm have the same origin.
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
