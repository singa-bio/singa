package de.bioforscher.simulation.research;

import de.bioforscher.simulation.modules.reactions.implementations.DynamicReaction;
import de.bioforscher.simulation.parser.BioModelsParserService;
import de.bioforscher.simulation.parser.SabioRKParserService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;


/**
 * @author cl
 */
public class LibSBMLPLayground {

    // http://www.ebi.ac.uk/biomodels-main/BIOMD0000000038
    private static final String MODEL_38_XML = "BIOMD0000000038.xml";


    public static void main(String[] args) throws IOException, XMLStreamException {

        SabioRKParserService parser =  new SabioRKParserService("10790");
        List<DynamicReaction> dynamicReactions = parser.fetchReaction();


    }

}
