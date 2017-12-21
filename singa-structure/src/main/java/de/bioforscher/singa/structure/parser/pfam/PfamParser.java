package de.bioforscher.singa.structure.parser.pfam;

import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.oak.StructuralEntityFilter;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions;
import de.bioforscher.singa.structure.parser.pfam.tokens.IdentifierToken;
import de.bioforscher.singa.structure.parser.pfam.tokens.PdbToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * A parser for the The Pfam protein families database. This can be used to parse protein structures directly from Pfam.
 * One can either parse all chains of a given Pfam-ID or only the domain ranges annotated with that Pfam-ID. According
 * to:
 * <pre>
 * R.Y. Eberhardt, S.R. Eddy, J. Mistry, A.L. Mitchell, S.C. Potter, M. Punta, M. Qureshi, A. Sangrador-Vegas, G.A.
 * Salazar, J. Tate, A. Bateman
 * Nucleic Acids Research (2016)  Database Issue 44:D279-D285
 * </pre>
 *
 * @author fk
 */
public class PfamParser {

    private static final Pattern PFAM_IDENTIFIER_PATTERN = Pattern.compile("PF[0-9]{5}");
    private static final String DEFAULT_CHAIN_LIST_SEPARATOR = "\t";
    private static final Predicate<LeafSubstructure<?>> LEAF_SUBSTRUCTURE_FILTER = leafSubstructure -> !(leafSubstructure instanceof AminoAcid);

    private static final Logger logger = LoggerFactory.getLogger(PfamParser.class);

    private final PfamVersion version;
    private final String pfamIdentifier;
    private final Path chainListPath;
    private final String chainListSeparator;
    private final boolean parseChains;
    private final StructureParserOptions structureParserOptions;

    private List<List<LeafSubstructure<?>>> domains;
    private List<Chain> chains;
    private List<String> relevantLines;

    public PfamParser(Builder builder) {
        version = builder.version;
        pfamIdentifier = builder.pfamIdentifier;
        chainListPath = builder.chainListPath;
        chainListSeparator = builder.chainListSeparator == null ? DEFAULT_CHAIN_LIST_SEPARATOR : builder.chainListSeparator;
        parseChains = builder.parseChains;
        structureParserOptions = builder.structureParserOptions;
        parse();
    }

    public static VersionStep create() {
        return new PfamParser.Builder();
    }

    private void parse() {

        fetchMappingFile();

        // reduce relevant lines if only certain chains are wanted
        if (chainListPath != null) {
            try {
                List<String[]> chainsToCollect = Files.lines(chainListPath)
                        .map(line -> line.split(chainListSeparator))
                        .collect(Collectors.toList());
                relevantLines.removeIf(line -> chainsToCollect.stream()
                        .noneMatch(chain -> PdbToken.PDB_IDENTIFIER.extract(line).equalsIgnoreCase(chain[0])
                                && PdbToken.CHAIN_IDENTIFIER.extract(line).equals(chain[1])));
                logger.info("Pfam covers {} of {} chains in the provided list", relevantLines.size(), chainsToCollect.size());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        // decide whether to parse chains or domains
        if (parseChains) {
            parseChains();
        } else {
            parseDomains();
        }
    }

    private void parseDomains() {
        List<List<LeafSubstructure<?>>> domains = new ArrayList<>();
        for (String relevantLine : relevantLines) {
            String pdbIdentifier = PdbToken.PDB_IDENTIFIER.extract(relevantLine);
            String chainIdentifier = PdbToken.CHAIN_IDENTIFIER.extract(relevantLine);
            int startPdb = Integer.valueOf(PdbToken.PDB_RESIDUE_START.extract(relevantLine));
            int endPdb = Integer.valueOf(PdbToken.PDB_RESIDUE_END.extract(relevantLine));
            List<LeafSubstructure<?>> domain;
            if (structureParserOptions != null) {
                domain = StructureParser.online()
                        .pdbIdentifier(pdbIdentifier)
                        .chainIdentifier(chainIdentifier)
                        .setOptions(structureParserOptions)
                        .parse()
                        .getAllLeafSubstructures();
            } else {
                domain = StructureParser.online()
                        .pdbIdentifier(pdbIdentifier)
                        .chainIdentifier(chainIdentifier)
                        .parse()
                        .getAllLeafSubstructures();
            }
            // apply global filter
            domain.removeIf(LEAF_SUBSTRUCTURE_FILTER);
            // remove all leaf remaining leaf substructures if not in range
            domain.removeIf(StructuralEntityFilter.LeafFilter.isWithinRange(startPdb, endPdb).negate());
            if (!domain.isEmpty()) {
                domains.add(domain);
            } else {
                logger.warn("domain range of {}_{} contains no residues and is skipped", pdbIdentifier, chainIdentifier);
            }
        }
        this.domains = domains;
    }

    private void parseChains() {
        List<Chain> chains = new ArrayList<>();
        for (String relevantLine : relevantLines) {
            String pdbIdentifier = PdbToken.PDB_IDENTIFIER.extract(relevantLine).toLowerCase().toLowerCase();
            String chainIdentifier = PdbToken.CHAIN_IDENTIFIER.extract(relevantLine);
            logger.info("parsing Pfam chain {}_{}", pdbIdentifier, chainIdentifier);
            Chain chain;
            if (structureParserOptions != null) {
                chain = StructureParser.online()
                        .pdbIdentifier(pdbIdentifier)
                        .chainIdentifier(chainIdentifier)
                        .setOptions(structureParserOptions)
                        .parse()
                        .getFirstModel().getFirstChain();
            } else {
                chain = StructureParser.online()
                        .pdbIdentifier(pdbIdentifier)
                        .chainIdentifier(chainIdentifier)
                        .parse()
                        .getFirstModel().getFirstChain();
            }
            chains.add(chain);
        }
        this.chains = chains;
    }

    private void fetchMappingFile() {
        try {
            logger.info("creating temporary file to store mapping file Pfam version {}", version);
            Path temporaryZippedFile = Files.createTempFile("singa_", ".txt.gz");
            URL website = new URL(version.pfamMappingLocation);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(temporaryZippedFile.toFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            logger.info("unzipping mapping file {}", temporaryZippedFile);
            try (GZIPInputStream zip = new GZIPInputStream(new FileInputStream(temporaryZippedFile.toFile()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(zip, "UTF-8"))) {
                relevantLines = reader.lines()
                        .filter(line -> IdentifierToken.PFAM_IDENTIFIER.extract(line).equals(pfamIdentifier))
                        .collect(Collectors.toList());
                logger.info("Pfam {} has {} entries", pfamIdentifier, relevantLines.size());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<List<LeafSubstructure<?>>> getDomains() {
        return domains;
    }

    public List<Chain> getChains() {
        return chains;
    }

    public enum PfamVersion {

        V31("ftp://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam31.0/database_files/pdb_pfamA_reg.txt.gz");

        private String pfamMappingLocation;

        PfamVersion(String pfamMappingLocation) {
            this.pfamMappingLocation = pfamMappingLocation;
        }
    }

    public interface VersionStep {

        /**
         * The version of the Pfam database that should be used.
         *
         * @param version The {@link PfamVersion}.
         * @return The {@link FamilyStep} to choose the Pfam-ID.
         */
        FamilyStep version(PfamVersion version);
    }

    public interface FamilyStep {

        /**
         * The Pfam-ID for which data should be fetched.
         *
         * @param pfamIdentifier The Pfam-ID.
         * @return ChainStep to limit selection of chains.
         */
        ChainStep pfamIdentifier(String pfamIdentifier);
    }

    public interface ChainStep {

        /**
         * Parse all chains annotated in Pfam.
         *
         * @return The {@link DomainStep} to decide if domains or chains should be parsed.
         */
        DomainStep all();

        /**
         * Parse all chains annotated in Pfam and listed in the given chain list. Default separator is "\t".
         *
         * @param chainListPath The {@link Path} to the given chain list.
         * @return The {@link DomainStep} to decide if domains or chains should be parsed.
         */
        DomainStep chainList(Path chainListPath);

        /**
         * Parse all chains annotated in Pfam and listed in the given chain list with the custom separator.
         *
         * @param chainListPath The {@link Path} to the given chain list.
         * @param chainListSeparator The separator used in the chain list.
         * @return The {@link DomainStep} to decide if domains or chains should be parsed.
         */
        DomainStep chainList(Path chainListPath, String chainListSeparator);
    }

    public interface DomainStep {

        /**
         * Specify {@link StructureParserOptions} used to parse the structures.
         *
         * @param structureParserOptions The {@link StructureParserOptions} to be used
         * @return The {@link DomainStep} to decide if domains or chains should be parsed.
         */
        DomainStep structureParserOptions(StructureParserOptions structureParserOptions);

        /**
         * Parse only the domains.
         *
         * @return Pfam domains.
         */
        List<List<LeafSubstructure<?>>> domains();

        /**
         * Parse all chains carrying one Pfam domain with the desired Pfam-ID.
         *
         * @return Chains with Pfam domains.
         */
        List<Chain> chains();
    }

    public static class Builder implements VersionStep, FamilyStep, ChainStep, DomainStep {

        public StructureParserOptions structureParserOptions;
        private PfamVersion version;
        private String pfamIdentifier;
        private Path chainListPath;
        private String chainListSeparator;
        private boolean parseChains;

        @Override
        public FamilyStep version(PfamVersion version) {
            Objects.requireNonNull(version);
            this.version = version;
            return this;
        }

        @Override
        public ChainStep pfamIdentifier(String pfamIdentifier) {
            Objects.requireNonNull(pfamIdentifier);
            if (!PFAM_IDENTIFIER_PATTERN.asPredicate().test(pfamIdentifier)) {
                throw new IllegalArgumentException("Pfam identifier '" + pfamIdentifier + "' seems to be invalid.");
            }
            this.pfamIdentifier = pfamIdentifier;
            return this;
        }

        @Override
        public DomainStep all() {
            return this;
        }

        @Override
        public DomainStep chainList(Path chainListPath) {
            Objects.requireNonNull(chainListPath);
            this.chainListPath = chainListPath;
            return this;
        }

        @Override
        public DomainStep chainList(Path chainListPath, String chainListSeparator) {
            Objects.requireNonNull(chainListPath);
            Objects.requireNonNull(chainListSeparator);
            this.chainListPath = chainListPath;
            this.chainListSeparator = chainListSeparator;
            return this;
        }

        @Override
        public DomainStep structureParserOptions(StructureParserOptions structureParserOptions) {
            Objects.requireNonNull(structureParserOptions);
            this.structureParserOptions = structureParserOptions;
            return this;
        }

        @Override
        public List<List<LeafSubstructure<?>>> domains() {
            return new PfamParser(this).getDomains();
        }

        @Override
        public List<Chain> chains() {
            parseChains = true;
            return new PfamParser(this).getChains();
        }
    }
}
