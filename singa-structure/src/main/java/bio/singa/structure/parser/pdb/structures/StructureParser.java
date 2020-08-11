package bio.singa.structure.parser.pdb.structures;

import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.iterators.StructureIterator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public interface IdentifierStep {

        /**
         * The pdbIdentifier of the PDB structure.
         *
         * @param pdbIdentifier The pdbIdentifier.
         * @return Model selection
         */
        SingleModelStep pdbIdentifier(String pdbIdentifier);

        /**
         * The pdbIdentifiers of the PDB structures.
         *
         * @param pdbIdentifiers The pdbIdentifiers.
         * @return Batch selection
         */
        MultiModelStep pdbIdentifiers(List<String> pdbIdentifiers);

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         * The default separator is tab (\t).
         *
         * @param path The path of the chainIdentifier list file
         * @return The MultiParser.
         */
        MultiModelStep chainList(Path path);

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         *
         * @param path The path of the chainIdentifier list file
         * @param separator The separator between the PDBId and the ChainId
         * @return The MultiParser.
         */
        MultiModelStep chainList(Path path, String separator);

    }

    /**
     * Select a local source to parse from.
     */
    public interface LocalSourceStep extends IdentifierStep {

        /**
         * The file to parse.
         *
         * @param file The file.
         * @return Branch selection
         */
        SingleModelStep file(File file);

        /**
         * The files to parse.
         *
         * @param files The files.
         * @return Branch selection
         */
        MultiModelStep files(List<File> files);

        /**
         * The path to parse.
         *
         * @param path The path.
         * @return Branch selection
         */
        SingleModelStep path(Path path);

        /**
         * The paths to parse.
         *
         * @param paths The paths.
         * @return Branch selection
         */
        MultiModelStep paths(List<Path> paths);

        /**
         * The location of a local PDB installation. This requires the input of a chin list in the following step.
         *
         * @param localPDB The local pdb.
         * @return Additional local list file selection.
         */
        LocalSourceStep localPdb(LocalPdbRepository localPDB);

        LocalSourceStep localCifRepository(LocalCifRepository localCifRepository);

        MultiModelStep all();

        /**
         * The location of a file as a sting.
         *
         * @param location The location.
         * @return Branch selection
         */
        SingleModelStep fileLocation(String location);

        /**
         * The location of files as strings.
         *
         * @param targetStructures The locations
         * @return Branch selection
         */
        MultiModelStep fileLocations(List<String> targetStructures);

        /**
         * Parses a structure from an input stream of a pdb file.
         *
         * @param inputStream The input stream of a pdb file.
         * @return Branch selection
         */
        SingleModelStep inputStream(InputStream inputStream);

    }

    /**
     * Initiates the structure reduction steps for multiple structures.
     */
    public interface MultiModelStep extends MultiChainStep {

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
    public interface SingleModelStep extends SingleChainStep {

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

        MultiChainStep settings(StructureParserOptions.Setting... settings);

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

        SingleChainStep settings(StructureParserOptions.Setting... settings);

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


    static class MultiReducingSelector extends SourceSelector implements MultiModelStep {

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
        public MultiChainStep settings(StructureParserOptions.Setting... settings) {
            iterator.getReducer().getOptions().applySettings(settings);
            return this;
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

    static class SingleReducingSelector extends SourceSelector implements SingleModelStep {

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
        public SingleChainStep settings(StructureParserOptions.Setting... settings) {
            iterator.getReducer().getOptions().applySettings(settings);
            return this;
        }

        @Override
        public Structure parse() throws StructureParserException {
            return new SingleParserFacade(iterator).parse();
        }

    }

    /**
     * Remembers the choices during the the stepwise building process.
     */
    static class SourceSelector implements LocalSourceStep, IdentifierStep {

        protected StructureIterator iterator;
        protected SourceLocation sourceLocation;

        protected LocalPdbRepository localPdb;
        protected LocalCifRepository localCifRepository;

        public SourceSelector(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
        }

        public SourceSelector() {
        }

        @Override
        public SingleModelStep pdbIdentifier(String pdbIdentifier) {
            List<String> pdbIdentifiers = Collections.singletonList(pdbIdentifier);
            SingleReducingSelector selector = new SingleReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, sourceLocation, localPdb);
            assignRepository(selector.iterator);
            return selector;
        }

        public void assignRepository(StructureIterator iterator) {
            if (localCifRepository != null) {
               iterator.getReducer().setLocalCifRepository(localCifRepository);
            }
        }

        @Override
        public MultiModelStep pdbIdentifiers(List<String> pdbIdentifiers) {
            MultiReducingSelector selector = new MultiReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, sourceLocation, localPdb);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleModelStep file(File file) {
            SingleReducingSelector selector = new SingleReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromFiles(Collections.singletonList(file), sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiModelStep files(List<File> files) {
            MultiReducingSelector selector = new MultiReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromFiles(files, sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleModelStep path(Path path) {
            SingleReducingSelector selector = new SingleReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromPaths(Collections.singletonList(path), sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiModelStep paths(List<Path> paths) {
            MultiReducingSelector selector = new MultiReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromPaths(paths, sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleModelStep inputStream(InputStream inputStream) {
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
            SingleReducingSelector selector = new SingleReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromFiles(Collections.singletonList(tempFile), sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public LocalSourceStep localPdb(LocalPdbRepository localPdb) {
            sourceLocation = localPdb.getSourceLocation();
            this.localPdb = localPdb;
            return this;
        }

        @Override
        public LocalSourceStep localCifRepository(LocalCifRepository localCifRepository) {
            this.localCifRepository = localCifRepository;
            return this;
        }

        @Override
        public MultiModelStep chainList(Path path) {
            return chainList(path, "\t");
        }

        @Override
        public MultiModelStep chainList(Path path, String separator) {
            MultiReducingSelector selector = new MultiReducingSelector(sourceLocation);
            if (localPdb != null) {
                selector.iterator = StructureIterator.createFromChainList(path, separator, localPdb);
            } else {
                selector.iterator = StructureIterator.createFromChainList(path, separator, sourceLocation);
            }
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiModelStep all() {
            MultiReducingSelector selector = new MultiReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocalPdb(localPdb);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleModelStep fileLocation(String location) {
            SingleReducingSelector selector = new SingleReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocations(Collections.singletonList(location));
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiModelStep fileLocations(List<String> locations) {
            MultiReducingSelector selector = new MultiReducingSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocations(locations);
            assignRepository(selector.iterator);
            return selector;
        }



    }

}
