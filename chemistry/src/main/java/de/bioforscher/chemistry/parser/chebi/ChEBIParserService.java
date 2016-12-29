package de.bioforscher.chemistry.parser.chebi;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.core.parser.AbstractParser;
import de.bioforscher.core.parser.FetchResultContainer;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChEBIParserService extends AbstractParser<Entity> {

    private static final Logger log = Logger.getLogger(ChEBIParserService.class.getName());
    private static final ChEBIParserService INSTANCE = new ChEBIParserService();

    public ChEBIParserService(String chebiId) {
        setResource(chebiId);
    }

    public ChEBIParserService() {

    }

    public static Species parse(String chebiId) {
        INSTANCE.setResource(chebiId);
        return INSTANCE.fetchSpecies();
    }

    @Override
    public void fetchResource() {
        ChebiWebServiceClient client = new ChebiWebServiceClient();
        try {
            Entity entity = client.getCompleteEntity(getResource());
            setFetchResult(new FetchResultContainer<>(entity));
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Can not reach Chemical Entities of Biological Interest (ChEBI) Database. Species can not be fetched.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> parseObjects() {
        List<Object> list = new ArrayList<>();
        Entity entity = getFetchResult().getContent();
        Species species = new Species.Builder(entity.getChebiId())
                .name(entity.getChebiAsciiName())
                .molarMass(handleWeight(entity.getMass()))
                .smilesRepresentation(entity.getSmiles())
                .build();
        list.add(species);
        return list;
    }

    private double handleWeight(String massAsString) {
        if (massAsString != null) {
            return Double.valueOf(massAsString);
        }
        return Double.NaN;
    }

    public Species fetchSpecies() {
        fetchResource();
        return (Species) parseObjects().get(0);
    }

}
