package de.bioforscher.simulation.parser;

import de.bioforscher.core.parser.rest.AbstractRESTParser;
import de.bioforscher.core.parser.xml.XMLErrorHandler;
import de.bioforscher.simulation.deprecated.EnzymeReaction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @deprecated
 */
public class SabioRKParserService extends AbstractRESTParser {

    private XMLReader xmlReader;

    public SabioRKParserService(String entryID) {

        try {
            this.xmlReader = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        this.xmlReader.setErrorHandler(new XMLErrorHandler());
        // this.xmlReader.setContentHandler(new SBMLContentHandler());

        setResource("http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/sbml");
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("q", entryID);
        setQueryMap(queryMap);
    }


    @Override
    public List<Object> parseObjects() {

        List<Object> list = new ArrayList<>();

        InputSource is = new InputSource(new StringReader(getFetchResult().getContent()));

        try {
            this.xmlReader.parse(is);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        EnzymeReaction reaction = ((SBMLContentHandler) this.xmlReader.getContentHandler()).getReaction();

        list.add(reaction);

        return list;
    }

    public EnzymeReaction fetchReaction() {

        fetchResource();
        List<Object> list = parseObjects();

        return (EnzymeReaction) list.get(0);

    }

}
