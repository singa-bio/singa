package de.bioforscher.singa.simulation.parser.sbml;

import de.bioforscher.singa.core.parser.AbstractHTMLParser;
import de.bioforscher.singa.simulation.modules.newmodules.imlementations.DynamicReaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SabioRKParserService extends AbstractHTMLParser<List<DynamicReaction>> {

    private static final String SABIORK_FETCH_URL = "http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/sbml";
    private final Map<String, String> queryMap;

    public SabioRKParserService(String entryID) {
        setResource(SABIORK_FETCH_URL);
        queryMap = new HashMap<>();
        queryMap.put("q", entryID);
    }

    @Override
    public List<DynamicReaction> parse() {
        fetchWithQuery(queryMap);
        SBMLParser parser = new SBMLParser(getFetchResult());
        parser.parse();
        return parser.getReactions();
    }


}
