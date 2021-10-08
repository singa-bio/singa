package bio.singa.structure.io.sifts;

import bio.singa.core.parser.AbstractHTMLParser;
import bio.singa.features.identifiers.UniProtIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * @author cl
 */
public class PDBUniProtMapper extends AbstractHTMLParser<Map<String, UniProtIdentifier>> {

    private static final String MAP_URL = "http://ftp.ebi.ac.uk/pub/databases/msd/sifts/flatfiles/tsv/pdb_chain_uniprot.tsv.gz";
    private static List<String> mappingStrings;

    private static PDBUniProtMapper pdbUniProtMapper;

    private String currentPDBIdentifier;

    private PDBUniProtMapper() {
        setResource(MAP_URL);
        fetchResource();
    }

    @Override
    public void fetchResource() {
        GZIPInputStream gzipInputStream;
        try {
            gzipInputStream = new GZIPInputStream(new URL(getResource()).openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to fetch result url " + getResource() + " for uniprot sifts mapping.");
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                mappingStrings = bufferedReader.lines()
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new UncheckedIOException("unable to parse pfam list ", e);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("unable to parse pfam list ", e);
        }
    }

    private static PDBUniProtMapper getInstance() {
        if (pdbUniProtMapper == null) {
            pdbUniProtMapper = new PDBUniProtMapper();
        }
        return pdbUniProtMapper;
    }

    public static Map<String, UniProtIdentifier> map(String pdbIdentifier) {
        getInstance().currentPDBIdentifier = pdbIdentifier;
        return getInstance().parse();
    }

    @Override
    public Map<String, UniProtIdentifier> parse() {
        Map<String, UniProtIdentifier> result = new HashMap<>();
        Pattern pattern = Pattern.compile("^" + currentPDBIdentifier + "\\t(\\p{Alpha}*)\\t(\\w*)\\t.*$");
        for (String mappingString : mappingStrings) {
            Matcher matcher = pattern.matcher(mappingString);
            if (matcher.matches()) {
                String chain = matcher.group(1);
                String uniProtIdentifier = matcher.group(2);
                result.put(chain, new UniProtIdentifier(uniProtIdentifier));
            }
        }
        return result;
    }
}
