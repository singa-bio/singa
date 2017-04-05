package de.bioforscher.singa.chemistry.parser.pubchem;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.core.parser.xml.AbstractXMLParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PubChemParserService extends AbstractXMLParser {


    public PubChemParserService(String filePath) {
        getXmlReader().setContentHandler(new PubChemContentHandler());
        setResource(filePath);
    }

    @Override
    public void fetchResource() {
        try {
            this.getXmlReader().parse(getResource());
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> parseObjects() {

        List<Object> list = new ArrayList<>();
        Species species = ((PubChemContentHandler) this.getXmlReader().getContentHandler()).getSpecies();
        list.add(species);

        return list;
    }

    public Species fetchSpecies() {
        fetchResource();
        List<Object> list = parseObjects();
        return (Species) list.get(0);
    }

}
