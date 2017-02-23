package de.bioforscher.simulation.parser.sbml;

import de.bioforscher.core.parser.rest.AbstractRESTParser;
import de.bioforscher.simulation.modules.reactions.implementations.DynamicReaction;
import de.bioforscher.simulation.parser.sbml.SBMLParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class SabioRKParserService extends AbstractRESTParser {

    public SabioRKParserService(String entryID) {
        setResource("http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/sbml");
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("q", entryID);
        setQueryMap(queryMap);
    }


    @Override
    public List<Object> parseObjects() {
        return null;
    }

    public List<DynamicReaction> fetchReaction() {
        fetchResource();
        InputStream inputStream = new ByteArrayInputStream(getFetchResult().getContent().getBytes(StandardCharsets.UTF_8));
        SBMLParser parser = new SBMLParser(inputStream);
        parser.parse();
        return parser.getReactions();
    }

}
