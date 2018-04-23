package de.bioforscher.singa.structure.parser.sifts;

import de.bioforscher.singa.core.parser.AbstractHTMLParser;
import de.bioforscher.singa.features.identifiers.PfamIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * @author fk
 */
public class PDBPfamMapper extends AbstractHTMLParser<Map<String, PfamIdentifier>> {

    private static final String MAP_URL = "http://ftp.ebi.ac.uk/pub/databases/msd/sifts/flatfiles/tsv/pdb_chain_pfam.tsv.gz";
    private static List<String> mappingStrings;

    private static PDBPfamMapper pdbPfamMapper;

    private String currentPDBIdentifier;

    private PDBPfamMapper() {
        setResource(MAP_URL);
        fetchResource();
        try {
            mappingStrings = new BufferedReader(new InputStreamReader(new GZIPInputStream(getFetchResult()), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to unpack gzip.", e);
        }
    }

    private static PDBPfamMapper getInstance() {
        if (pdbPfamMapper == null) {
            pdbPfamMapper = new PDBPfamMapper();
        }
        return pdbPfamMapper;
    }

    public static Map<String, PfamIdentifier> map(String pdbIdentifier) {
        getInstance().currentPDBIdentifier = pdbIdentifier;
        return getInstance().parse();
    }

    @Override
    public Map<String, PfamIdentifier> parse() {
        Map<String, PfamIdentifier> result = new HashMap<>();
        Pattern pattern = Pattern.compile("^" + currentPDBIdentifier + "\\t(\\p{Alpha}*)\\t\\w*\\t(\\w*)$");
        for (String mappingString : mappingStrings) {
            Matcher matcher = pattern.matcher(mappingString);
            if (matcher.matches()) {
                String chain = matcher.group(1);
                String pfamIdentifier = matcher.group(2);
                result.put(chain, new PfamIdentifier(pfamIdentifier));
            }
        }
        return result;
    }
}
