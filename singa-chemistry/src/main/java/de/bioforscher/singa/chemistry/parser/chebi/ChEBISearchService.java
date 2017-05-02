package de.bioforscher.singa.chemistry.parser.chebi;

import de.bioforscher.singa.chemistry.descriptive.Species;
import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChEBISearchService {

    private static final Logger log = Logger.getLogger(ChEBIParserService.class.getName());

    private int maximalNumberOfResults;
    private ChebiWebServiceClient client;
    private String searchTerm;

    public ChEBISearchService() {
        this.client = new ChebiWebServiceClient();
        this.maximalNumberOfResults = 20;
    }

    public ChEBISearchService(String searchTerm) {
        this();
        this.searchTerm = searchTerm;
    }

    public List<Species> search() {
        return getSearchResults();
    }

    private List<Species> getSearchResults() {
        List<LiteEntity> searchResults = new ArrayList<>(this.maximalNumberOfResults);

        try {
            LiteEntityList liteList = this.client.getLiteEntity(this.searchTerm, SearchCategory.ALL, this.maximalNumberOfResults, StarsCategory.ALL);
            searchResults = liteList.getListElement();
        } catch (ChebiWebServiceFault_Exception e) {
            log.log(Level.SEVERE,
                    "Can not reach Chemical Entities of Biological Interest (ChEBI) Database. Species can not be fetched.");
            e.printStackTrace();
        }

        List<Species> convertedList = new ArrayList<>(searchResults.size());
        convertedList.addAll(searchResults.stream().map(this::convertLiteEntityToSpecies).collect(Collectors.toList()));

        return convertedList;
    }

    private Species convertLiteEntityToSpecies(LiteEntity lightEntity) {
        ChEBIParserService chebiService = new ChEBIParserService(lightEntity.getChebiId());
        return chebiService.fetchSpecies();
    }

    public int getMaximalNumberOfResults() {
        return this.maximalNumberOfResults;
    }

    public void setMaximalNumberOfResults(int maximalNumberOfResults) {
        this.maximalNumberOfResults = maximalNumberOfResults;
    }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
