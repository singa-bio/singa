package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;

public class ChEBIParserService {

    private static final Logger logger = LoggerFactory.getLogger(ChEBIParserService.class);

    private String primaryIdentifier;
    private String chebiIdentifier;

    private Entity entity;

    public ChEBIParserService() {

    }

    public ChEBIParserService(String chebiIdentifier) {
        this.chebiIdentifier = chebiIdentifier;
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

    public void fetch() {
        ChebiWebServiceClient client = new ChebiWebServiceClient();
        logger.debug("Fetching information {} from ChEBI using the ChEBIWebServiceClient.", this.chebiIdentifier);
        try {
            this.entity = client.getCompleteEntity(this.chebiIdentifier);
        } catch (Exception e) {
            logger.warn("Can not reach Chemical Entities of Biological Interest (ChEBI) Database. Species {} can not be fetched.", this.chebiIdentifier);
            e.printStackTrace();
        }
    }

    public Species parse() {
        logger.debug("Creating {} from retrieved information ... ", this.entity.getChebiAsciiName());
        Species species;
        if (this.primaryIdentifier == null) {
            species = new Species.Builder(this.entity.getChebiId())
                    .name(this.entity.getChebiAsciiName())
                    .assignFeature(new MolarMass(handleWeight(this.entity.getMass()), ChEBIDatabase.origin))
                    .smilesRepresentation(this.entity.getSmiles())
                    .build();
        } else {
            species = new Species.Builder(this.primaryIdentifier)
                    .additionalIdentifier(new ChEBIIdentifier(this.entity.getChebiId()))
                    .name(this.entity.getChebiAsciiName())
                    .assignFeature(new MolarMass(handleWeight(this.entity.getMass()), ChEBIDatabase.origin))
                    .smilesRepresentation(this.entity.getSmiles())
                    .build();
        }
        return species;
    }

    private double handleWeight(String massAsString) {
        if (massAsString != null) {
            return Double.valueOf(massAsString);
        }
        return Double.NaN;
    }

    public Species fetchSpecies() {
        fetch();
        return parse();
    }

}
