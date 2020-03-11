package bio.singa.structure.parser.pdb.structures;

import bio.singa.core.utility.Pair;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;
import org.rcsb.mmtf.decoder.ReaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parses structures in pdb format.
 *
 * @author cl
 */
public class StructureParser {

    private static final Logger logger = LoggerFactory.getLogger(StructureParser.class);

    /**
     * Structures will be retrieved from a local source.
     *
     * @return Source selection
     */
    public static LocalSourceStep local() {
        return new SourceSelector();
    }

    /**
     * Structures will be pulled online.
     *
     * @return Source selection
     */
    public static IdentifierStep pdb() {
        return new SourceSelector(SourceLocation.ONLINE_PDB);
    }

    /**
     * Structures will be pulled online.
     *
     * @return Source selection
     */
    public static IdentifierStep mmtf() {
        return new SourceSelector(SourceLocation.ONLINE_MMTF);
    }

    /**
     * After selecting the source, the identifer(s) to parse can be chosen.
     */
    public interface IdentifierStep extends AdditionalLocalSourceStep {

        /**
         * The pdbIdentifier of the PDB structure.
         *
         * @param pdbIdentifier The pdbIdentifier.
         * @return Model selection
         */
        SingleBranchStep pdbIdentifier(String pdbIdentifier);

        /**
         * The pdbIdentifiers of the PDB structures.
         *
         * @param pdbIdentifiers The pdbIdentifiers.
         * @return Batch selection
         */
        MultiBranchStep pdbIdentifiers(List<String> pdbIdentifiers);

    }

    /**
     * Select a local source to parse from.
     */
    public interface LocalSourceStep {

        /**
         * The file to parse.
         *
         * @param file The file.
         * @return Branch selection
         */
        SingleBranchStep file(File file);

        /**
         * The files to parse.
         *
         * @param files The files.
         * @return Branch selection
         */
        MultiBranchStep files(List<File> files);

        /**
         * The path to parse.
         *
         * @param path The path.
         * @return Branch selection
         */
        SingleBranchStep path(Path path);

        /**
         * The paths to parse.
         *
         * @param paths The paths.
         * @return Branch selection
         */
        MultiBranchStep paths(List<Path> paths);

        /**
         * The location of a local PDB installation. This requires the input of a chin list in the following step.
         *
         * @param localPDB The local pdb.
         * @return Additional local list file selection.
         */
        AdditionalLocalSourceStep localPDB(LocalPDB localPDB);

        /**
         * The location of a local PDB installation in addition to the structure, that is to be parsed.
         *
         * @param localPDB      The local pdb.
         * @param pdbIdentifier The PDB identifier.
         * @return Branch selection
         */
        SingleBranchStep localPDB(LocalPDB localPDB, String pdbIdentifier);

        /**
         * The location of a local PDB installation in addition to a list of structures, that are to be parsed.
         *
         * @param localPDB       The local pdb.
         * @param pdbIdentifiers The PDB identifiers.
         * @return Branch selection
         */
        MultiBranchStep localPDB(LocalPDB localPDB, List<String> pdbIdentifiers);

        /**
         * The location of a file as a sting.
         *
         * @param location The location.
         * @return Branch selection
         */
        SingleBranchStep fileLocation(String location);

        /**
         * The location of files as strings.
         *
         * @param targetStructures The locations
         * @return Branch selection
         */
        MultiBranchStep fileLocations(List<String> targetStructures);

        /**
         * Parses a structure from an input stream of a pdb file.
         *
         * @param inputStream The input stream of a pdb file.
         * @return Branch selection
         */
        SingleBranchStep inputStream(InputStream inputStream);

    }

    /**
     * Using a local pdb installation, additional source steps might be used.
     */
    public interface AdditionalLocalSourceStep {

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         * The default separator is tab (\t).
         *
         * @param path The path of the chainIdentifier list file
         * @return The MultiParser.
         */
        MultiParser chainList(Path path);

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         *
         * @param path      The path of the chainIdentifier list file
         * @param separator The separator between the PDBId and the ChainId
         * @return The MultiParser.
         */
        MultiParser chainList(Path path, String separator);

    }

    /**
     * Initiates the structure reduction steps for multiple structures.
     */
    public interface MultiBranchStep extends MultiChainStep {

        /**
         * If only a single model should be parsed, give its pdbIdentifier here.
         *
         * @param modelIdentifier The pdbIdentifier of the model to parse.
         * @return Chain selection
         */
        MultiChainStep model(int modelIdentifier);

        /**
         * Short cut to parse all models.
         *
         * @return Chain selection
         */
        MultiChainStep allModels();

    }

    /**
     * Initiates the structure reduction steps for a single structure.
     */
    public interface SingleBranchStep extends SingleChainStep {

        /**
         * If only a single model should be parsed, give its pdbIdentifier here.
         *
         * @param modelIdentifier The pdbIdentifier of the model to parse.
         * @return Chain selection
         */
        SingleChainStep model(int modelIdentifier);

        /**
         * Short cut to parse all models.
         *
         * @return Chain selection
         */
        SingleChainStep allModels();

    }

    /**
     * Initiates the selection of chains, if multiple structures have been chosen.
     */
    public interface MultiChainStep {

        /**
         * If only a single chain should be parsed, choose it here.
         *
         * @param chainIdentifier The chain to parse.
         * @return The MultiParser.
         */
        MultiParser chain(String chainIdentifier);

        /**
         * Parses all chains.
         *
         * @return The MultiParser.
         */
        MultiParser allChains();

        /**
         * Shortcut to parse all chains.
         *
         * @return The MultiParser.
         */
        MultiParser everything();

        /**
         * Returns a list of parsed structures, skipping user contact with parsing.
         *
         * @return The specified structures.
         */
        List<Structure> parse();
    }

    /**
     * Initiates the selection of chains, if a single structure has been chosen.
     */
    public interface SingleChainStep {

        /**
         * If only a single chain should be parsed, choose it here.
         *
         * @param chainIdentifier The chain to parse.
         * @return The Parser.
         */
        SingleParser chainIdentifier(String chainIdentifier);

        /**
         * Parses all chains.
         *
         * @return The Parser.
         */
        SingleParser allChains();

        /**
         * Shortcut to parse all chains.
         *
         * @return The Parser.
         */
        SingleParser everything();

        /**
         * Returns the parsed structure, skipping user contact with parsing.
         *
         * @return The specified structure.
         */
        Structure parse() throws StructureParserException;
    }


    /**
     * This parser should be used if you require only a single structure. If more structures should be parsed at once,
     * the MultiParser grants more functionality and speed improvements.
     */
    public static class SingleParser {

        /**
         * The reducer specifies what content is parsed.
         */
        final SingleReducingSelector selector;

        /**
         * Creates a new parser. The selector specifies what content should be parsed.
         *
         * @param selector The selector.
         */
        SingleParser(SingleReducingSelector selector) {
            this.selector = selector;
        }

        /**
         * Sets the {@link StructureParserOptions}.
         *
         * @param options The options.
         * @return The parser with the given options.
         */
        public SingleParser setOptions(StructureParserOptions options) {
            selector.options = options;
            return this;
        }

        /**
         * Parses the structure as specifies during teh selection process and returns is,
         *
         * @return The structures.
         * @throws StructureParserException if the structure could not be parsed as specified during the selection.
         */
        public Structure parse() throws StructureParserException {
            try {
                if (selector.sourceSelector.sourceLocation == SourceLocation.ONLINE_MMTF) {
                    MmtfStructure mmtfStructure = new MmtfStructure(ReaderUtils.getByteArrayFromUrl(selector.sourceSelector.contentIterator.next().get(0)));
                    MmtfReducer.reduceMMTFStructure(mmtfStructure, selector);
                    return mmtfStructure;
                } else if (selector.sourceSelector.sourceLocation == SourceLocation.OFFLINE_MMTF) {
                    return new MmtfStructure(Files.readAllBytes(Paths.get(selector.sourceSelector.contentIterator.next().get(0))), false);
                }
            } catch (IOException e) {
                logger.warn("failed to parse structure", e);
                throw new StructureParserException(e.getMessage());
            }
            return StructureCollector.parse(selector.sourceSelector.contentIterator.next(), selector);
        }
    }

    /**
     * The MultiParser performs parsing of multiple structures from a single source. Using the {@link Iterator} pattern
     * the structures can be parsed and processed individually. Each specified structure will be parsed lazily so
     * parsing until a certain condition is met can be done without parsing unused structures. Additionally some
     * speedups are provided parsing multiple structures. Every ligand is only parsed once, the first time it is
     * encountered, afterwards it is stored as a {@link LeafSkeleton} that is completed with the the concrete atom
     * positions for each new occurrence.
     */
    public static class MultiParser implements Iterator<Structure> {

        /**
         * The reducer specifies what content is parsed.
         */
        final MultiReducingSelector selector;

        /**
         * Creates a new parser. The selector specifies what content should be parsed.
         *
         * @param selector The selector.
         */
        MultiParser(MultiReducingSelector selector) {
            this.selector = selector;
        }

        /**
         * Returns the number of structures totally queued.
         *
         * @return The number of structures totally queued.
         */
        public int getNumberOfQueuedStructures() {
            return selector.sourceSelector.contentIterator.getNumberOfQueuedStructures();
        }

        /**
         * Returns the number of structures remaining.
         *
         * @return The number of structures remaining.
         */
        public synchronized int getNumberOfRemainingStructures() {
            return selector.sourceSelector.contentIterator.getNumberOfRemainingStructures();
        }

        /**
         * Returns the pdb identifier of the next structure, if it can be accessed prior to parsing.
         *
         * @return The pdb identifier of the next structure.
         * @throws IllegalStateException if the structures identifier could not be accessed.
         */
        public String getCurrentPdbIdentifier() {
            return selector.sourceSelector.contentIterator.getCurrentPdbIdentifier();
        }

        /**
         * Returns the chain identifier of the next structure, if it can be accessed prior to parsing.
         *
         * @return The chain identifier of the next structure.
         * @throws IllegalStateException if the structures identifier could not be accessed.
         */
        public String getCurrentChainIdentifier() {
            return selector.sourceSelector.contentIterator.getCurrentChainIdentifier();
        }

        /**
         * Sets the {@link StructureParserOptions} for this parsing process.
         *
         * @param options The options.
         * @return The parser with the set options.
         */
        public MultiParser setOptions(StructureParserOptions options) {
            selector.options = options;
            return this;
        }

        /**
         * Parses all structures that are queued for this {@link MultiParser}.
         *
         * @return A list of structures.
         */
        public List<Structure> parse() {
            logger.info("parsing {} structures ", getNumberOfQueuedStructures());
            List<Structure> structures = new ArrayList<>();
            selector.sourceSelector.contentIterator.forEachRemaining(lines -> {
                try {
                    // FIXME uiuiui
                    switch (selector.sourceSelector.sourceLocation) {
                        case ONLINE_MMTF:
                            MmtfStructure structureOnline = new MmtfStructure(ReaderUtils.getByteArrayFromUrl(lines.get(0)));
                            MmtfReducer.reduceMMTFStructure(structureOnline, selector);
                            structures.add(structureOnline);
                            break;
                        case OFFLINE_MMTF:
                            MmtfStructure structureOffline = new MmtfStructure(Files.readAllBytes(Paths.get(lines.get(0))), false);
                            MmtfReducer.reduceMMTFStructure(structureOffline, selector);
                            structures.add(structureOffline);
                            break;
                        default:
                            structures.add(StructureCollector.parse(lines, selector));
                            break;
                    }
                } catch (StructureParserException | IOException e) {
                    logger.warn("failed to parse structure", e);
                }
            });
            return structures;
        }


        @Override
        synchronized public boolean hasNext() {
            return selector.sourceSelector.contentIterator.hasNext();
        }

        @Override
        synchronized public Structure next() {
            try {
                // FIXME uiuiui
                if (selector.sourceSelector.sourceLocation == SourceLocation.ONLINE_MMTF) {
                    MmtfStructure mmtfStructure = new MmtfStructure(ReaderUtils.getByteArrayFromUrl(selector.sourceSelector.contentIterator.next().get(0)));
                    MmtfReducer.reduceMMTFStructure(mmtfStructure, selector);
                    return mmtfStructure;
                } else if (selector.sourceSelector.sourceLocation == SourceLocation.OFFLINE_MMTF) {
                    MmtfStructure mmtfStructure = new MmtfStructure(Files.readAllBytes(Paths.get(selector.sourceSelector.contentIterator.next().get(0))), true);
                    MmtfReducer.reduceMMTFStructure(mmtfStructure, selector);
                    return mmtfStructure;
                }
            } catch (IOException e) {
                logger.warn("failed to parse structure {}", selector.sourceSelector.contentIterator.next().get(0));
            }
            return StructureCollector.parse(selector.sourceSelector.contentIterator.next(), selector);
        }
    }

    /**
     * This class remembers what restrictions are to be applied during the parsing of multiple structures.
     */
    static class MultiReducingSelector extends Reducer implements MultiBranchStep {

        /**
         * Creates a new selector using the supplied source selector.
         *
         * @param sourceSelector The {@link SourceSelector} to be used.
         */
        MultiReducingSelector(SourceSelector sourceSelector) {
            super(sourceSelector);
        }

        @Override
        public MultiChainStep model(int modelIdentifier) {
            setModelIdentifier(modelIdentifier);
            return this;
        }

        @Override
        public MultiChainStep allModels() {
            allModels = true;
            return this;
        }

        @Override
        public MultiParser everything() {
            setEverything();
            return new MultiParser(this);
        }

        /**
         * Returns a {@link MultiParser} that is configured to use a chain mapping during parsing.
         *
         * @return The {@link MultiParser}.
         */
        private MultiParser mapping() {
            parseMapping = true;
            return new MultiParser(this);
        }

        @Override
        public MultiParser chain(String chainIdentifier) {
            setChainIdentifier(chainIdentifier);
            return new MultiParser(this);
        }

        @Override
        public MultiParser allChains() {
            setAllChains();
            return new MultiParser(this);
        }

        @Override
        public List<Structure> parse() {
            setEverything();
            return new MultiParser(this).parse();
        }
    }

    /**
     * This class remembers what restrictions are to be applied during the parsing of a single structures.
     */
    static class SingleReducingSelector extends Reducer implements SingleBranchStep {

        /**
         * Creates a new selector using the supplied source selector.
         *
         * @param sourceSelector The {@link SourceSelector} to be used.
         */
        SingleReducingSelector(SourceSelector sourceSelector) {
            super(sourceSelector);
        }

        @Override
        public SingleChainStep model(int modelIdentifier) {
            setModelIdentifier(modelIdentifier);
            return this;
        }

        @Override
        public SingleChainStep allModels() {
            allModels = true;
            return this;
        }

        @Override
        public SingleParser everything() {
            setEverything();
            return new SingleParser(this);
        }

        @Override
        public SingleParser chainIdentifier(String chainIdentifier) {
            setChainIdentifier(chainIdentifier);
            return new SingleParser(this);
        }

        @Override
        public SingleParser allChains() {
            setAllChains();
            return new SingleParser(this);
        }

        @Override
        public Structure parse() throws StructureParserException {
            setEverything();
            return new SingleParser(this).parse();
        }

    }

    /**
     * This class remembers what general restrictions are to be applied during the parsing.
     */
    protected static class Reducer {

        /**
         * The content to be parsed.
         */
        final SourceSelector sourceSelector;

        /**
         * A cache of {@link LeafSkeleton}s that are reused during parsing of ligands.
         */
        final Map<String, LeafSkeleton> skeletons;

        /**
         * The current pdb identifier. This is updated by the content iterator whenever possible.
         */
        String pdbIdentifier;

        /**
         * The current model identifier. This is not updated by the content iterator.
         */
        int modelIdentifier;

        /**
         * The current chain identifier. This is updated by the content iterator whenever possible
         */
        String chainIdentifier;

        /**
         * References that all models should be parsed.
         */
        boolean allModels = true;

        /**
         * References that all chains should be parsed.
         */
        boolean allChains = true;

        /**
         * References that a pdb identifier - chain identifier pattern should be parsed.
         */
        boolean parseMapping = false;

        /**
         * The options provided of the parser
         */
        StructureParserOptions options = new StructureParserOptions();

        /**
         * signifies that models should be reduced in any way
         */
        boolean modelsReduced;

        /**
         * Creates a new reducer with the supplied source selector.
         *
         * @param sourceSelector The source selector.
         */
        Reducer(SourceSelector sourceSelector) {
            this.sourceSelector = sourceSelector;
            skeletons = new HashMap<>();
        }

        /**
         * Sets the model to be parsed.
         *
         * @param modelIdentifier The model identifier.
         */
        void setModelIdentifier(int modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
            modelsReduced = true;
            allModels = false;
        }

        /**
         * Sets the chain to be parsed.
         *
         * @param chainIdentifier The chain identifier.
         */
        void setChainIdentifier(String chainIdentifier) {
            Objects.requireNonNull(chainIdentifier);
            this.chainIdentifier = chainIdentifier;
            allChains = false;
            if (!modelsReduced) {
                allModels = true;
            }
        }

        /**
         * Sets all chains to be parsed.
         */
        void setAllChains() {
            allChains = true;
        }

        /**
         * Sets everything to be parsed.
         */
        void setEverything() {
            if (!modelsReduced) {
                allModels = true;
            }
            allChains = true;
        }

        /**
         * Updates the pdb identifier after parsing.
         */
        void updatePdbIdentifer() {
            pdbIdentifier = sourceSelector.contentIterator.getCurrentPdbIdentifier();
        }

        /**
         * Updates the chain identifier after parsing.
         */
        void updateChainIdentifier() {
            chainIdentifier = sourceSelector.contentIterator.getCurrentChainIdentifier();
        }

        @Override
        public String toString() {
            return "Reducer{pdbIdentifier='" + pdbIdentifier + '\'' +
                    ", modelIdentifier=" + modelIdentifier +
                    ", chainIdentifier='" + chainIdentifier + '\'' +
                    ", allModels=" + allModels +
                    ", allChains=" + allChains +
                    '}';
        }

    }

    /**
     * Remembers the choices during the the stepwise building process.
     */
    static class SourceSelector implements LocalSourceStep, IdentifierStep, AdditionalLocalSourceStep {

        /**
         * The iterator that guides the parsing process to the specified files.
         */
        StructureContentIterator contentIterator;

        /**
         * The local pdb installation if there is any.
         */
        private LocalPDB localPDB;

        private SourceLocation sourceLocation;

        public SourceSelector(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
        }

        public SourceSelector() {
        }

        @Override
        public SingleBranchStep pdbIdentifier(String pdbIdentifier) {
            contentIterator = new StructureContentIterator(pdbIdentifier, sourceLocation);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep pdbIdentifiers(List<String> pdbIdentifiers) {
            contentIterator = new StructureContentIterator(String.class, pdbIdentifiers, sourceLocation);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep file(File file) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            contentIterator = new StructureContentIterator(file);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep files(List<File> files) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            contentIterator = new StructureContentIterator(File.class, files, SourceLocation.OFFLINE_PDB);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep path(Path path) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            contentIterator = new StructureContentIterator(path);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep paths(List<Path> paths) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            contentIterator = new StructureContentIterator(Path.class, paths, SourceLocation.OFFLINE_PDB);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep inputStream(InputStream inputStream) {
            File tempFile;
            try {
                tempFile = File.createTempFile("temporaryStructure", ".pdb");
                Objects.requireNonNull(tempFile);
                tempFile.deleteOnExit();
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to parse structure from input stream.", e);
            }
            contentIterator = new StructureContentIterator(tempFile);
            return new SingleReducingSelector(this);
        }

        @Override
        public AdditionalLocalSourceStep localPDB(LocalPDB localPDB) {
            sourceLocation = localPDB.sourceLocation;
            this.localPDB = localPDB;
            return this;
        }

        @Override
        public MultiBranchStep localPDB(LocalPDB localPDB, List<String> pdbIdentifiers) {
            sourceLocation = localPDB.sourceLocation;
            contentIterator = new StructureContentIterator(localPDB, pdbIdentifiers);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep localPDB(LocalPDB localPDB, String pdbIdentifier) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            contentIterator = new StructureContentIterator(localPDB, pdbIdentifier);
            return new SingleReducingSelector(this);
        }

        @Override
        public SingleBranchStep fileLocation(String location) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            contentIterator = new StructureContentIterator(Paths.get(location));
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep fileLocations(List<String> locations) {
            sourceLocation = SourceLocation.OFFLINE_PDB;
            List<Path> paths = locations.stream()
                    .map(Paths::get)
                    .collect(Collectors.toList());
            contentIterator = new StructureContentIterator(Path.class, paths, SourceLocation.OFFLINE_PDB);
            return new MultiReducingSelector(this);
        }

        @Override
        public MultiParser chainList(Path path, String separator) {
            try {
                if (localPDB != null) {
                    contentIterator = new StructureContentIterator(readMappingFile(path, separator), localPDB);
                } else {
                    contentIterator = new StructureContentIterator(readMappingFile(path, separator), sourceLocation);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for chain list file.", e);
            }
            return new MultiReducingSelector(this).mapping();
        }

        @Override
        public MultiParser chainList(Path path) {
            return chainList(path, "\t");
        }

        /**
         * Reads a pdb identifier chain identifier mapping file.
         *
         * @param mappingPath The path to the mapping file.
         * @param separator   The String separating both.
         * @return A list of pdb identifier chain paris.
         * @throws IOException if the file could not be read.
         */
        private List<Pair<String>> readMappingFile(Path mappingPath, String separator) throws IOException {
            InputStream inputStream = Files.newInputStream(mappingPath);
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    return composePairsForChainList(bufferedReader.lines().collect(Collectors.toList()), separator);
                }
            }
        }

        /**
         * Reads a file of pdb identifier chain identifier paris and converts them to be read by the {@link
         * StructureContentIterator}.
         *
         * @param lines     The lines that should be parsed.
         * @param separator The String separating the pairs.
         * @return A list of pdb identifier chain paris.
         */
        private List<Pair<String>> composePairsForChainList(List<String> lines, String separator) {
            ArrayList<Pair<String>> pairs = new ArrayList<>();
            for (String line : lines) {
                String[] split = line.split(separator);
                // first contains PDB-ID and second contains chainIdentifier-ID
                pairs.add(new Pair<>(split[0], split[1]));
            }
            return pairs;
        }


    }

    /**
     * This class represents a local PDB installation.
     */
    public static class LocalPDB {

        /**
         * The default folder structure of local pdb installations.
         */
        static final Path BASE_PATH_PDB = Paths.get("data/structures/divided/");

        private final SourceLocation sourceLocation;

        /**
         * The path to the local pdb.
         */
        private Path localPdbPath;

        /**
         * Creates a new reference for a local pdb installation.
         *
         * @param localPdbLocation The location of the local PDB installation.
         * @param sourceLocation   The type of file used (either {@link SourceLocation#OFFLINE_MMTF} or {@link
         *                         SourceLocation#OFFLINE_PDB}).
         */
        public LocalPDB(String localPdbLocation, SourceLocation sourceLocation) {
            this(localPdbLocation, sourceLocation, BASE_PATH_PDB);
        }

        /**
         * Creates a new reference for a local pdb installation.
         *
         * @param localPdbLocation The location of the local PDB installation.
         * @param sourceLocation   The type of file used (either {@link SourceLocation#OFFLINE_MMTF} or {@link
         *                         SourceLocation#OFFLINE_PDB}).
         * @param basePathPdb      The base PDB path if different from data/structures/divided/
         */
        public LocalPDB(String localPdbLocation, SourceLocation sourceLocation, Path basePathPdb) {
            this.sourceLocation = sourceLocation;
            switch (sourceLocation) {
                case OFFLINE_MMTF:
                    localPdbPath = Paths.get(localPdbLocation).resolve(basePathPdb).resolve("mmtf");
                    break;
                case OFFLINE_PDB:
                    localPdbPath = Paths.get(localPdbLocation).resolve(basePathPdb).resolve("pdb");
                    break;
                default:
                    throw new IllegalArgumentException("Source location mus be offline.");
            }

        }

        /**
         * Returns the path to the local pdb.
         *
         * @return The path to the local pdb.
         */
        public Path getLocalPdbPath() {
            return localPdbPath;
        }

        /**
         * Returns the full path of a given PDB-ID in respect to the local PDB copy.
         *
         * @param pdbIdentifier The PDB-ID for which the full path should be retrieved.
         * @return The full path of the given PDB-ID.
         */
        public Path getPathForPdbIdentifier(String pdbIdentifier) {
            pdbIdentifier = pdbIdentifier.toLowerCase();
            final Path middleIdentifierPath = localPdbPath.resolve(pdbIdentifier.substring(1, 3));
            if (sourceLocation == SourceLocation.OFFLINE_PDB) {
                return middleIdentifierPath.resolve("pdb" + pdbIdentifier + ".ent.gz");
            }
            return middleIdentifierPath.resolve(pdbIdentifier + ".mmtf.gz");
        }

        @Override
        public String toString() {
            return "LocalPDB{" +
                    "sourceLocation=" + sourceLocation +
                    ", localPdbPath=" + localPdbPath +
                    '}';
        }
    }
}
