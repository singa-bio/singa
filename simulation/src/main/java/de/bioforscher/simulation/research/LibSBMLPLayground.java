package de.bioforscher.simulation.research;

import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.chemistry.parser.UniProtParserService;
import de.bioforscher.core.identifier.ChEBIIdentifier;
import de.bioforscher.core.identifier.UniProtIdentifier;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.regex.Matcher;


/**
 * Created by Christoph on 09.09.2016.
 */
public class LibSBMLPLayground {

    // http://www.ebi.ac.uk/biomodels-main/BIOMD0000000038
    private static final String MODEL_38_XML = "BIOMD0000000038.xml";


    public static void main(String[] args) throws IOException, XMLStreamException {
        String modelLocation = LibSBMLPLayground.class.getResource(MODEL_38_XML).getPath();

        SBMLReader reader = new SBMLReader();
        SBMLDocument document = reader.readSBML(modelLocation);

        System.out.println("File: " + modelLocation);
        System.out.println("Errors: " + document.getNumErrors());

        // component stuff
        document.getModel().getListOfSpecies().forEach(species -> {
            System.out.println("Species: " + species.getId());
            // the annotations describe the entity used and is composed of CVTerms
            species.getAnnotation().getListOfCVTerms().forEach(term -> {
                // each cv term is composed of
                // Qualifiers: the relationship between the entity and the resource
                // System.out.println(" " + term.getQualifier());
                // Resources: the actual links or data
                term.getResources().forEach(resource -> {
                    // if this species *is* a chemical component, we can potentially parse it
                    if (term.getQualifier() == CVTerm.Qualifier.BQB_IS) {
                        // if the resources contain a ChEBI identifier we can definitely parse it
                        Matcher matcherChEBI = ChEBIIdentifier.PATTERN.matcher(resource);
                        if (matcherChEBI.find()) {
                            System.out.println(" is " + matcherChEBI.group(0));
                            System.out.println(" " + ChEBIParserService.parse(matcherChEBI.group(0)));
                        }
                        Matcher matcherUniProt = UniProtIdentifier.PATTERN.matcher(resource);
                        if (matcherUniProt.find()) {
                            System.out.println(" is " + matcherUniProt.group(0));
                            System.out.println(" " + UniProtParserService.parse(matcherUniProt.group(0)));
                        }

                    }
                    // if this species
                });

            });
        });

        System.out.println();

        document.getModel().getListOfReactions().forEach(reaction -> {
            System.out.println("Reaction:" + reaction);
            // substrates
            reaction.getListOfReactants().forEach( reactant -> {
                System.out.println(" R: " + reactant.getSpecies());
            });
            // products
            reaction.getListOfProducts().forEach( product -> {
                System.out.println(" P: " + product.getSpecies());
            });
            // kinetics
            // System.out.println(reaction.getKineticLaw().getMathMLString());
        });



    }

}
