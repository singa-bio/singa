package de.bioforscher.simulation.parser;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class UniProtQueryTest {

    public static void main(String[] args) {

        UniProtQueryService queryService = UniProtJAPI.factory.getUniProtQueryService();

        // Build query for Protein Name
        Query query1 = UniProtQueryBuilder.buildProteinNameQuery("Clpc");

        EntryIterator<UniProtEntry> entries = queryService.getEntryIterator(query1);

        System.out.println("Number of entries with protein name Clpl = " + entries.getResultSize());

        for (UniProtEntry entry : entries) {
            System.out.println("entry.getPrimaryUniProtAccession() = " + entry.getPrimaryUniProtAccession());
        }

        // Build query for EC number
        Query query2 = UniProtQueryBuilder.buildECNumberQuery("3.1.6.-");
        Query query = UniProtQueryBuilder.setReviewedEntries(query2);

        // You can retrieve just accession numbers
        AccessionIterator<UniProtEntry> iterator = queryService.getAccessions(query);

        System.out.println("Number of entries with EC 3.1.6.- = " + iterator.getResultSize());

        // Build query to select entries which were updated during the certain period
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/09/2007");
            endDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH).parse("01/23/2007");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Query dateQuery = UniProtQueryBuilder.buildCreatedQuery(startDate, endDate);

        // Query for SwissProt data set
        Query reviewedQuery = UniProtQueryBuilder.setReviewedEntries(dateQuery);

        AccessionIterator<UniProtEntry> accessions = queryService.getAccessions(reviewedQuery);
        System.out.println("Created entries = " + accessions.getResultSize());

    }

}
