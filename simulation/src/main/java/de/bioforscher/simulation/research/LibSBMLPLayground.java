package de.bioforscher.simulation.research;

import de.bioforscher.simulation.parser.BioModelsParserService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;



/**
 * @author cl
 */
public class LibSBMLPLayground {

    // http://www.ebi.ac.uk/biomodels-main/BIOMD0000000038
    private static final String MODEL_38_XML = "BIOMD0000000038.xml";


    public static void main(String[] args) throws IOException, XMLStreamException {
        String modelLocation = LibSBMLPLayground.class.getResource(MODEL_38_XML).getPath();
        BioModelsParserService.parseModelFromFile(modelLocation);



    }

}
