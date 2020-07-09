package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.iterators.StructureIterator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Parses structures in pdb format.
 *
 * @author cl
 */
public class StructureParser {

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
        StructureIterator chainList(Path path);

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         *
         * @param path The path of the chainIdentifier list file
         * @param separator The separator between the PDBId and the ChainId
         * @return The MultiParser.
         */
        StructureIterator chainList(Path path, String separator);

        StructureIterator all();

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
        StructureIterator chain(String chainIdentifier);

        /**
         * Parses all chains.
         *
         * @return The MultiParser.
         */
        StructureIterator allChains();

        /**
         * Shortcut to parse all chains.
         *
         * @return The MultiParser.
         */
        StructureIterator everything();

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
        SingleParserFacade chainIdentifier(String chainIdentifier);

        /**
         * Parses all chains.
         *
         * @return The Parser.
         */
        SingleParserFacade allChains();

        /**
         * Shortcut to parse all chains.
         *
         * @return The Parser.
         */
        SingleParserFacade everything();

        /**
         * Returns the parsed structure, skipping user contact with parsing.
         *
         * @return The specified structure.
         */
        Structure parse() throws StructureParserException;
    }


    static class MultiReducingSelector extends SourceSelector implements MultiBranchStep {

        public MultiReducingSelector(SourceLocation sourceLocation) {
            super(sourceLocation);
        }

        @Override
        public MultiChainStep model(int modelIdentifier) {
            iterator.getReducer().setModelIdentifier(modelIdentifier);
            iterator.getReducer().setReduceModels(true);
            return this;
        }

        @Override
        public MultiChainStep allModels() {
            return this;
        }

        @Override
        public StructureIterator everything() {
            return iterator;
        }

        @Override
        public StructureIterator chain(String chainIdentifier) {
            iterator.getReducer().setChainIdentifier(chainIdentifier);
            iterator.getReducer().setReduceChains(true);
            return iterator;
        }

        @Override
        public StructureIterator allChains() {
            return iterator;
        }

        @Override
        public List<Structure> parse() {
            return iterator.parse();
        }
    }

    public static class SingleParserFacade {

        private final StructureIterator iterator;

        public SingleParserFacade(StructureIterator iterator) {
            this.iterator = iterator;
        }

        public StructureIterator getIterator() {
            return iterator;
        }

        public SingleParserFacade setOptions(StructureParserOptions options) {
            iterator.getReducer().setOptions(options);
            return this;
        }

        public Structure parse() {
            iterator.prepareNext();
            return iterator.next();
        }

    }

    static class SingleReducingSelector extends SourceSelector implements SingleBranchStep {

        public SingleReducingSelector(SourceLocation sourceLocation) {
            super(sourceLocation);
        }

        @Override
        public SingleChainStep model(int modelIdentifier) {
            iterator.getReducer().setModelIdentifier(modelIdentifier);
            iterator.getReducer().setReduceModels(true);
            return this;
        }

        @Override
        public SingleChainStep allModels() {
            return this;
        }

        @Override
        public SingleParserFacade everything() {
            return new SingleParserFacade(iterator);
        }

        @Override
        public SingleParserFacade chainIdentifier(String chainIdentifier) {
            iterator.getReducer().setChainIdentifier(chainIdentifier);
            iterator.getReducer().setReduceChains(true);
            return new SingleParserFacade(iterator);
        }

        @Override
        public SingleParserFacade allChains() {
            return new SingleParserFacade(iterator);
        }

        @Override
        public Structure parse() throws StructureParserException {
            return new SingleParserFacade(iterator).parse();
        }

    }

    /**
     * Remembers the choices during the the stepwise building process.
     */
    static class SourceSelector implements LocalSourceStep, IdentifierStep, AdditionalLocalSourceStep {

        StructureIterator iterator;
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
            SingleReducingSelector reducer = new SingleReducingSelector(sourceLocation);
            reducer.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, sourceLocation);
            return reducer;
        }

        @Override
        public MultiBranchStep pdbIdentifiers(List<String> pdbIdentifiers) {
            MultiReducingSelector reducer = new MultiReducingSelector(sourceLocation);
            reducer.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, sourceLocation);
            return reducer;
        }

        @Override
        public SingleBranchStep file(File file) {
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(sourceLocation);
            singleReducingSelector.iterator = StructureIterator.createFromFiles(Collections.singletonList(file), sourceLocation);
            return singleReducingSelector;
        }

        @Override
        public MultiBranchStep files(List<File> files) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(sourceLocation);
            multiReducingSelector.iterator = StructureIterator.createFromFiles(files, sourceLocation);
            return multiReducingSelector;
        }

        @Override
        public SingleBranchStep path(Path path) {
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(sourceLocation);
            singleReducingSelector.iterator = StructureIterator.createFromPaths(Collections.singletonList(path), sourceLocation);
            return singleReducingSelector;
        }

        @Override
        public MultiBranchStep paths(List<Path> paths) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(sourceLocation);
            multiReducingSelector.iterator = StructureIterator.createFromPaths(paths, sourceLocation);
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
            // FIXME this assumes always pdb format streams
            sourceLocation = SourceLocation.OFFLINE_PDB;
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(sourceLocation);
            singleReducingSelector.iterator = StructureIterator.createFromFiles(Collections.singletonList(tempFile), sourceLocation);
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
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(sourceLocation);
            multiReducingSelector.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, localPdb);
            return multiReducingSelector;
        }

        @Override
        public SingleBranchStep localPdb(LocalPdb localPdb, String pdbIdentifier) {
            sourceLocation = localPdb.sourceLocation;
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(sourceLocation);
            singleReducingSelector.iterator = StructureIterator.createFromIdentifiers(Collections.singletonList(pdbIdentifier), localPdb);
            return singleReducingSelector;
        }

        @Override
        public SingleBranchStep fileLocation(String location) {
            SingleReducingSelector singleReducingSelector = new SingleReducingSelector(sourceLocation);
            singleReducingSelector.iterator = StructureIterator.createFromLocations(Collections.singletonList(location));
            return singleReducingSelector;
        }

        @Override
        public MultiBranchStep fileLocations(List<String> locations) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(sourceLocation);
            multiReducingSelector.iterator = StructureIterator.createFromLocations(locations);
            return multiReducingSelector;
        }

        @Override
        public StructureIterator chainList(Path path, String separator) {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(sourceLocation);
            if (localPdb != null) {
                multiReducingSelector.iterator = StructureIterator.createFromChainList(path, separator, localPdb);
            } else {
                multiReducingSelector.iterator = StructureIterator.createFromChainList(path, separator, sourceLocation);
            }
            return multiReducingSelector.everything();
        }

        @Override
        public StructureIterator chainList(Path path) {
            return chainList(path, "\t");
        }

        @Override
        public StructureIterator all() {
            MultiReducingSelector multiReducingSelector = new MultiReducingSelector(sourceLocation);
            multiReducingSelector.iterator = StructureIterator.createFromLocalPdb(localPdb);
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
