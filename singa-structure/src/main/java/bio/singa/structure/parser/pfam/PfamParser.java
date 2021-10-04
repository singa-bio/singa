package bio.singa.structure.parser.pfam;

import bio.singa.features.identifiers.PfamIdentifier;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.general.StructuralEntityFilter;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
import bio.singa.structure.parser.pfam.tokens.PfamToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
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

    private static final String DEFAULT_CHAIN_LIST_SEPARATOR = "\t";
    private static final Predicate<LeafSubstructure> LEAF_SUBSTRUCTURE_FILTER = leafSubstructure -> !(leafSubstructure instanceof AminoAcid);

    private static final Logger logger = LoggerFactory.getLogger(PfamParser.class);

    private final PfamVersion version;
    private final PfamIdentifier pfamIdentifier;
    private final Path chainListPath;
    private final String chainListSeparator;
    private final boolean parseChains;
    private final StructureParserOptions structureParserOptions;

    private List<Collection<? extends LeafSubstructure>> domains;
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
                        .noneMatch(chain -> PfamToken.PDBToken.PDB_IDENTIFIER.extract(line).equalsIgnoreCase(chain[0])
                                && PfamToken.PDBToken.CHAIN_IDENTIFIER.extract(line).equals(chain[1])));
                logger.info("Pfam covers {} of {} chains in the provided list", relevantLines.size(), chainsToCollect.size());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        // decide whether to parse chains or domains
        if (parseChains) {
            logger.info("Parsing Pfam {} chains", pfamIdentifier);
            parseChains();
        } else {
            logger.info("Parsing Pfam {} domains.", pfamIdentifier);
            parseDomains();
        }
    }

    private void parseDomains() {
        List<Collection<? extends LeafSubstructure>> domains = new ArrayList<>();
        for (String relevantLine : relevantLines) {
            String pdbIdentifier = PfamToken.PDBToken.PDB_IDENTIFIER.extract(relevantLine).toLowerCase();
            String chainIdentifier = PfamToken.PDBToken.CHAIN_IDENTIFIER.extract(relevantLine);
            int startPdb = Integer.parseInt(PfamToken.PDBToken.PDB_RESIDUE_START.extract(relevantLine));
            int endPdb = Integer.parseInt(PfamToken.PDBToken.PDB_RESIDUE_END.extract(relevantLine));
            Collection<? extends LeafSubstructure> domain;
            if (structureParserOptions != null) {
                domain = StructureParser.pdb()
                        .pdbIdentifier(pdbIdentifier)
                        .chainIdentifier(chainIdentifier)
                        .setOptions(structureParserOptions)
                        .parse()
                        .getAllLeafSubstructures();
            } else {
                domain = StructureParser.pdb()
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
            String pdbIdentifier = PfamToken.PDBToken.PDB_IDENTIFIER.extract(relevantLine).toLowerCase();
            String chainIdentifier = PfamToken.PDBToken.CHAIN_IDENTIFIER.extract(relevantLine);
            logger.debug("parsing Pfam chain {}_{}", pdbIdentifier, chainIdentifier);
            Chain chain;
            if (structureParserOptions != null) {
                chain = StructureParser.pdb()
                        .pdbIdentifier(pdbIdentifier)
                        .chainIdentifier(chainIdentifier)
                        .setOptions(structureParserOptions)
                        .parse()
                        .getFirstModel().getFirstChain();
            } else {
                chain = StructureParser.pdb()
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
        logger.info("creating temporary file to store mapping file Pfam version {}", version);
        URL website;
        try {
            website = new URL(version.pfamMappingLocation);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unable to convert url from " + version.pfamMappingLocation + " for pfam mapping.");
        }
        GZIPInputStream gzipInputStream;
        try {
            gzipInputStream = new GZIPInputStream(website.openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to open url " + version.pfamMappingLocation + " for pfam mapping.");
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                relevantLines = bufferedReader.lines()
                        .filter(line -> PfamToken.IdentifierToken.PFAM_IDENTIFIER.extract(line).equals(pfamIdentifier.getContent()))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new UncheckedIOException("unable to parse pfam list ", e);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("unable to parse pfam list ", e);
        }
        logger.debug("Pfam {} has {} entries", pfamIdentifier, relevantLines.size());

    }

    public List<Collection<? extends LeafSubstructure>> getDomains() {
        return domains;
    }

    public List<Chain> getChains() {
        return chains;
    }

    public enum PfamVersion {

        V31("http://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam31.0/database_files/pdb_pfamA_reg.txt.gz"),
        V33("http://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam33.1/database_files/pdb_pfamA_reg.txt.gz");

        private final String pfamMappingLocation;

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
        List<Collection<? extends LeafSubstructure>> domains();

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
        private PfamIdentifier pfamIdentifier;
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
            this.pfamIdentifier = new PfamIdentifier(pfamIdentifier);
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
        public List<Collection<? extends LeafSubstructure>> domains() {
            return new PfamParser(this).getDomains();
        }

        @Override
        public List<Chain> chains() {
            parseChains = true;
            return new PfamParser(this).getChains();
        }
    }
}
