package de.bioforscher.singa.chemistry.parser.chebi;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.parser.AbstractParser;
import de.bioforscher.singa.core.parser.FetchResultContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;

import java.util.ArrayList;
import java.util.List;


public class ChEBIParserService extends AbstractParser<Entity> {

    private static final Logger logger = LoggerFactory.getLogger(ChEBIParserService.class);

    private String primaryIdentifier;

    public ChEBIParserService(String chebiIdentifier) {
        setResource(chebiIdentifier);
    }

    public ChEBIParserService() {

    }

    public static Species parse(String chebiIdentifier) {
        ChEBIParserService parser = new ChEBIParserService(chebiIdentifier);
        return parser.fetchSpecies();
    }

    public static Species parse(String chebiIdentifier, String primaryIdentifier) {
        ChEBIParserService parser = new ChEBIParserService(chebiIdentifier);
        parser.primaryIdentifier = primaryIdentifier;
        return parser.fetchSpecies();
    }

    @Override
    public void fetchResource() {
        ChebiWebServiceClient client = new ChebiWebServiceClient();
        logger.debug("Fetching information {} from ChEBI using the ChebiWebServiceClient.", this.getResource());
        try {
            Entity entity = client.getCompleteEntity(getResource());
            setFetchResult(new FetchResultContainer<>(entity));
        } catch (Exception e) {
            logger.warn("Can not reach Chemical Entities of Biological Interest (ChEBI) Database. Species {} can not be fetched.", this.getResource());
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> parseObjects() {
        List<Object> list = new ArrayList<>();
        Entity entity = getFetchResult().getContent();
        logger.debug("Creating {} from retrieved information ... ", entity.getChebiAsciiName());
        Species species;
        if (this.primaryIdentifier == null) {
            species = new Species.Builder(entity.getChebiId())
                    .name(entity.getChebiAsciiName())
                    .molarMass(handleWeight(entity.getMass()))
                    .smilesRepresentation(entity.getSmiles())
                    .build();
        } else {
            species = new Species.Builder(this.primaryIdentifier)
                    .additionalIdentifier(new ChEBIIdentifier(entity.getChebiId()))
                    .name(entity.getChebiAsciiName())
                    .molarMass(handleWeight(entity.getMass()))
                    .smilesRepresentation(entity.getSmiles())
                    .build();

        }
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
