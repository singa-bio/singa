package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.core.parser.xml.AbstractXMLParser;
import org.xml.sax.SAXException;

import java.io.IOException;

public class PubChemParserService extends AbstractXMLParser<Species> {

    public PubChemParserService(String filePath) {
        getXmlReader().setContentHandler(new PubChemContentHandler());
        setResource(filePath);
    }

    @Override
    public Species parse() {
        try {
            this.getXmlReader().parse(getResource());
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return ((PubChemContentHandler) this.getXmlReader().getContentHandler()).getSpecies();
    }
}
