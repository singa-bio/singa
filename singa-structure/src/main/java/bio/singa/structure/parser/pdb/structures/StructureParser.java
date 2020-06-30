package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.parser.pdb.structures.iterators.StructureIterator;
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
        AdditionalLocalSourceStep localPdb(LocalPdb localPDB);

        /**
         * The location of a local PDB installation in addition to the structure, that is to be parsed.
         *
         * @param localPDB The local pdb.
         * @param pdbIdentifier The PDB identifier.
         * @return Branch selection
         */
        SingleBranchStep localPdb(LocalPdb localPDB, String pdbIdentifier);

        /**
         * The location of a local PDB installation in addition to a list of structures, that are to be parsed.
         *
         * @param localPDB The local pdb.
         * @param pdbIdentifiers The PDB identifiers.
         * @return Branch selection
         */
        MultiBranchStep localPdb(LocalPdb localPDB, List<String> pdbIdentifiers);

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
         * @param path The path of the chainIdentifier list file
         * @param separator The separator between the PDBId and the ChainId
         * @return The MultiParser.
         */
        MultiParser chainList(Path path, String separator);

        MultiParser all();

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

        public Map<String, LeafSkeleton> getSkeletons() {
            return selector.getSkeletons();
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
                    MmtfStructure mmtfStructure = new MmtfStructure(ReaderUtils.getByteArrayFromUrl(selector.sourceSelector.structureIterator.next().get(0)));
                    MmtfReducer.reduceMMTFStructure(mmtfStructure, selector);
                    return mmtfStructure;
                } else if (selector.sourceSelector.sourceLocation == SourceLocation.OFFLINE_MMTF) {
                    return new MmtfStructure(Files.readAllBytes(Paths.get(selector.sourceSelector.structureIterator.next().get(0))), false);
                }
            } catch (IOException e) {
                logger.warn("failed to parse structure", e);
                throw new StructureParserException(e.getMessage());
            }
            return StructureCollector.parse(selector.sourceSelector.structureIterator.next(), selector);
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
            return selector.sourceSelector.structureIterator.getNumberOfQueuedStructures();
        }

        /**
         * Returns the number of structures remaining.
         *
         * @return The number of structures remaining.
         */
        public synchronized int getNumberOfRemainingStructures() {
            return selector.sourceSelector.structureIterator.getNumberOfRemainingStructures();
        }

        /**
         * Returns the pdb identifier of the next structure, if it can be accessed prior to parsing.
         *
         * @return The pdb identifier of the next structure.
         * @throws IllegalStateException if the structures identifier could not be accessed.
         */
        public String getCurrentPdbIdentifier() {
            return selector.sourceSelector.structureIterator.getCurrentPdbIdentifier();
        }

        /**
         * Returns the chain identifier of the next structure, if it can be accessed prior to parsing.
         *
         * @return The chain identifier of the next structure.
         * @throws IllegalStateException if the structures identifier could not be accessed.
         */
        public String getCurrentChainIdentifier() {
            return selector.sourceSelector.structureIterator.getCurrentChainIdentifier();
        }

        public String getCurrentSource() {
            return selector.sourceSelector.structureIterator.getCurrentSource();
        }

        public Path getCurrentPath() {
            return selector.sourceSelector.structureIterator.getCurrentPath();
        }


        public Map<String, LeafSkeleton> getSkeletons() {
            return selector.getSkeletons();
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
            selector.sourceSelector.iterator.forEachRemaining(structures::add);
            return structures;
        }


        @Override
        synchronized public boolean hasNext() {
            return selector.sourceSelector.iterator.hasNext();
        }

        @Override
        synchronized public Structure next() {
            selector.sourceSelector.iterator.prepareNext();
            return selector.sourceSelector.iterator.next();
        }

        synchronized public void skip() {
            selector.sourceSelector.iterator.prepareNext();
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
    public static class Reducer {

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
            pdbIdentifier = sourceSelector.structureIterator.getCurrentPdbIdentifier();
        }

        /**
         * Updates the chain identifier after parsing.
         */
        void updateChainIdentifier() {
            chainIdentifier = sourceSelector.structureIterator.getCurrentChainIdentifier();
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

        public Map<String, LeafSkeleton> getSkeletons() {
            return skeletons;
        }
    }

    /**
     * Remembers the choices during the the stepwise building process.
     */
    static class SourceSelector implements LocalSourceStep, IdentifierStep, AdditionalLocalSourceStep {

        /**
         * The iterator that guides the parsing process to the specified files.
         */
        StructureIterator iterator;

        /**
         * The local pdb installation if there is any.
         */
        private LocalPdb localPdb;

        private SourceLocation sourceLocation;

        public SourceSelector(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
        }

        public SourceSelector() {
        }

        @Override
        public SingleBranchStep pdbIdentifier(String pdbIdentifier) {
            List<String> pdbIdentifiers = Collections.singletonList(pdbIdentifier);
            SingleReducingSelector reducer = new SingleReducingSelector(this);
            iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, reducer, sourceLocation);
            return reducer;
        }

        @Override
        public MultiBranchStep pdbIdentifiers(List<String> pdbIdentifiers) {
            MultiReducingSelector reducer = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, reducer, sourceLocation);
            return reducer;
        }


        @Override
        public SingleBranchStep file(File file) {
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(this);
            iterator = StructureIterator.createFromFiles(Collections.singletonList(file), singleReducingSelector, sourceLocation);
            return singleReducingSelector;
        }

        @Override
        public MultiBranchStep files(List<File> files) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromFiles(files, multiReducingSelector, sourceLocation);
            return multiReducingSelector;
        }


        @Override
        public SingleBranchStep path(Path path) {
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(this);
            iterator = StructureIterator.createFromPaths(Collections.singletonList(path), singleReducingSelector, sourceLocation);
            return singleReducingSelector;
        }

        @Override
        public MultiBranchStep paths(List<Path> paths) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromPaths(paths, multiReducingSelector, sourceLocation);
            return multiReducingSelector;
        }

        @Override
        public SingleBranchStep inputStream(InputStream inputStream) {
            File tempFile;
            try {
                // TODO create converter for InputStream
                tempFile = File.createTempFile("temporaryStructure", ".pdb");
                Objects.requireNonNull(tempFile);
                tempFile.deleteOnExit();
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to parse structure from input stream.", e);
            }
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(this);
            iterator = StructureIterator.createFromFiles(Collections.singletonList(tempFile), singleReducingSelector, sourceLocation);
            return singleReducingSelector;
        }

        @Override
        public AdditionalLocalSourceStep localPdb(LocalPdb localPdb) {
            sourceLocation = localPdb.sourceLocation;
            this.localPdb = localPdb;
            return this;
        }

        @Override
        public MultiBranchStep localPdb(LocalPdb localPdb, List<String> pdbIdentifiers) {
            sourceLocation = localPdb.sourceLocation;
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, multiReducingSelector, localPdb);
            return multiReducingSelector;
        }

        @Override
        public SingleBranchStep localPdb(LocalPdb localPdb, String pdbIdentifier) {
            sourceLocation = localPdb.sourceLocation;
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(this);
            iterator = StructureIterator.createFromIdentifiers(Collections.singletonList(pdbIdentifier), singleReducingSelector, localPdb);
            return singleReducingSelector;
        }

        @Override
        public SingleBranchStep fileLocation(String location) {
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(this);
            iterator = StructureIterator.createFromLocations(Collections.singletonList(location), singleReducingSelector, sourceLocation);
            return singleReducingSelector;
        }

        @Override
        public MultiBranchStep fileLocations(List<String> locations) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromLocations(locations, multiReducingSelector, sourceLocation);
            return multiReducingSelector;
        }

        @Override
        public MultiParser chainList(Path path, String separator) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromChainList(path, separator, multiReducingSelector, localPdb);
            return multiReducingSelector.mapping();
        }

        @Override
        public MultiParser chainList(Path path) {
            return chainList(path, "\t");
        }

        @Override
        public MultiParser all() {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(this);
            iterator = StructureIterator.createFromLocalPdb(localPdb, multiReducingSelector);
            return multiReducingSelector.everything();
        }

    }

    /**
     * This class represents a local PDB installation.
     */
    public static class LocalPdb {

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
         * @param sourceLocation The type of file used (either {@link SourceLocation#OFFLINE_MMTF} or {@link
         * SourceLocation#OFFLINE_PDB}).
         */
        public LocalPdb(String localPdbLocation, SourceLocation sourceLocation) {
            this(localPdbLocation, sourceLocation, BASE_PATH_PDB);
        }

        /**
         * Creates a new reference for a local pdb installation.
         *
         * @param localPdbLocation The location of the local PDB installation.
         * @param sourceLocation The type of file used (either {@link SourceLocation#OFFLINE_MMTF} or {@link
         * SourceLocation#OFFLINE_PDB}).
         * @param basePathPdb The base PDB path if different from data/structures/divided/
         */
        public LocalPdb(String localPdbLocation, SourceLocation sourceLocation, Path basePathPdb) {
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

        public SourceLocation getSourceLocation() {
            return sourceLocation;
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
