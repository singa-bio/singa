package bio.singa.structure.parser.ncbi;

import bio.singa.core.parser.AbstractUniProtIdentifierMappingParser;
import bio.singa.features.identifiers.RefSeqIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses the UniProt ID mapping service to convert RefSeq nucleotide IDs to UniProt IDs.
 *
 * @see <a href="https://www.uniprot.org/help/api_idmapping"></a>
 */
public class RefSeqUniProtMapper extends AbstractUniProtIdentifierMappingParser<Map<UniProtIdentifier, String>> {

    private static final Logger logger = LoggerFactory.getLogger(RefSeqUniProtMapper.class);

    private static final String MAP_URL = "https://www.uniprot.org/uploadlists/?";
    private RefSeqIdentifier refSeqIdentifier;

    public RefSeqUniProtMapper(RefSeqIdentifier refSeqIdentifier) {
        this.refSeqIdentifier = refSeqIdentifier;
        setResource(MAP_URL);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("from", "REFSEQ_NT_ID");
        parameterMap.put("to", "ACC");
        parameterMap.put("format", "tab");
        parameterMap.put("columns", "id,reviewed");
        // https://www.uniprot.org/help/api_queries
        // https://www.uniprot.org/help/uniprotkb_column_names
        parameterMap.put("query", refSeqIdentifier.toString());
        fetchWithQuery(parameterMap);
    }

    @Override
    public Map<UniProtIdentifier, String> parse() {
        Map<UniProtIdentifier, String> uniProtIdentifiers = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getFetchResult()));
            String headerline = bufferedReader.readLine();
            String[] header = headerline.split("\t");
            int entryIndex = 0;
            int statusIndex = 0;
            for (int i = 0; i < header.length; i++) {
                if (header[i].equals("Entry")) {
                    entryIndex = i;
                }
                if (header[i].equals("Status")) {
                    statusIndex = i;
                }
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\t");
                try {
                    uniProtIdentifiers.put(new UniProtIdentifier(split[entryIndex]), split[statusIndex]);
                } catch (IllegalArgumentException e) {
                    logger.debug("skipping malformed UniProt identifier {} in line {}", split[entryIndex], line);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to retrieve mapping for " + refSeqIdentifier, e);
        }
        return uniProtIdentifiers;
    }
}
