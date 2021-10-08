package bio.singa.structure.io.sifts;

import bio.singa.core.parser.AbstractHTMLParser;
import bio.singa.features.identifiers.ECNumber;

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
public class PDBEnzymeMapper extends AbstractHTMLParser<Map<String, ECNumber>> {

    private static final String MAP_URL = "http://ftp.ebi.ac.uk/pub/databases/msd/sifts/flatfiles/tsv/pdb_chain_enzyme.tsv.gz";
    private static List<String> mappingStrings;

    private static PDBEnzymeMapper pdbUniProtMapper;

    private String currentPDBIdentifier;

    private PDBEnzymeMapper() {
        setResource(MAP_URL);
        fetchResource();
        try {
            mappingStrings = new BufferedReader(new InputStreamReader(new GZIPInputStream(getFetchResult()), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to unpack gzip.", e);
        }
    }

    private static PDBEnzymeMapper getInstance() {
        if (pdbUniProtMapper == null) {
            pdbUniProtMapper = new PDBEnzymeMapper();
        }
        return pdbUniProtMapper;
    }

    public static Map<String, ECNumber> map(String pdbIdentifier) {
        getInstance().currentPDBIdentifier = pdbIdentifier;
        return getInstance().parse();
    }

    @Override
    public Map<String, ECNumber> parse() {
        Map<String, ECNumber> result = new HashMap<>();
        Pattern pattern = Pattern.compile("^" + currentPDBIdentifier + "\\t(\\p{Alpha}*)\\t\\w*\\t.*$");
        for (String mappingString : mappingStrings) {
            Matcher matcher = pattern.matcher(mappingString);
            if (matcher.matches()) {
                String chain = matcher.group(1);
                String ecNumber = mappingString.split("\t")[3];
                result.put(chain, new ECNumber(ecNumber));
            }
        }
        return result;
    }
}
