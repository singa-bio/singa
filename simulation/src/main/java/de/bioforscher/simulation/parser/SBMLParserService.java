package de.bioforscher.simulation.parser;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.chemistry.parser.UniProtParserService;
import de.bioforscher.core.identifier.ChEBIIdentifier;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.simulation.research.LibSBMLPLayground;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * Created by Christoph on 04/11/2016.
 */
public class SBMLParserService {

    private SBMLDocument document;

    private HashMap<String, ChemicalEntity> entities;

    public SBMLParserService() {
        this.entities = new HashMap<>();
    }

    public void parseFile(String filePath) {
        SBMLReader reader = new SBMLReader();
        try {
            this.document = reader.readSBML(filePath);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
        parseSpecies();
    }

    private void parseSpecies() {
        // component stuff
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
                    if(term.getResourceCount() == 1) {
                        // and one resource
                        System.out.println("only " + term.getResourceCount() + " Resource");
                        parseAndAddSingularComponent(species.getId(), term);
                    } else {
                        // and multiple resources
                        System.out.println(+ term.getResourceCount() + " Resources");
                        System.out.println("  don't know what to do yet...");
                    }

                } else {
                    // with
                    System.out.println("  annotated as \"" + term.getQualifier().getElementNameEquivalent() + "\" with " + term.getResourceCount() + " Resources");
                    System.out.println("  don't know what to do yet...");
                }

            } else {
                // multiple annotations
                System.out.println("  annotated with multiple annotations");
                System.out.println("  don't know what to do yet...");
            }
            System.out.println();
        });
    }

    private void tryToParse() {

    }

    private void parseAndAddComplexComponent(String identifier, CVTerm... cvTerm) {

    }

    private void parseAndAddSingularComponent(String identifier, CVTerm cvTerm) {
        ChemicalEntity entity = parseEntity(cvTerm.getResources().get(0))
                .orElse(Species.UNKNOWN_SPECIES);
        this.entities.put(identifier, entity);
        System.out.println("  -> parsed as " + entity);
    }

    private Optional<ChemicalEntity> parseEntity(String resource) {
        // try to parse as ChEBI
        Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
        if (matcherChEBI.find()) {
            return Optional.of(ChEBIParserService.parse(matcherChEBI.group(0)));
        }
        // try to parse as UniProt
        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
        if (matcherUniProt.find()) {
            return Optional.of(UniProtParserService.parse(matcherUniProt.group(0)));
        }
        // no parser available
        return Optional.empty();
    }

    // http://www.ebi.ac.uk/biomodels-main/BIOMD0000000038
    private static final String MODEL_38_XML = "BIOMD0000000038.xml";

    public static void main(String[] args) throws IOException, XMLStreamException {
        String modelLocation = LibSBMLPLayground.class.getResource(MODEL_38_XML).getPath();

        SBMLParserService service = new SBMLParserService();
        service.parseFile(modelLocation);

    }

}
