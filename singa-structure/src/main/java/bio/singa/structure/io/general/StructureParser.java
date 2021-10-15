package bio.singa.structure.io.general;

import bio.singa.structure.io.ccd.LeafSkeletonFactory;
import bio.singa.structure.io.ccd.LocalCcdParsingBehavior;
import bio.singa.structure.io.ccd.NoCcdParsingBehavior;
import bio.singa.structure.io.ccd.RemoteCcdParsingBehavior;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.iterators.StructureIterator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
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

    public static IdentifierStep cif() {
        return new SourceSelector(SourceLocation.ONLINE_MMCIF);
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
        SingleResultStep pdbIdentifier(String pdbIdentifier);

        /**
         * The pdbIdentifiers of the PDB structures.
         *
         * @param pdbIdentifiers The pdbIdentifiers.
         * @return Batch selection
         */
        MultiResultStep pdbIdentifiers(List<String> pdbIdentifiers);

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         * The default separator is tab (\t).
         *
         * @param path The path of the chainIdentifier list file
         * @return The MultiParser.
         */
        MultiResultStep chainList(Path path);

        /**
         * Reads the provided chainIdentifier list from a file. Each line in the file should have the format:
         * <pre>[PDBId][separator][ChainId] </pre>
         *
         * @param path The path of the chainIdentifier list file
         * @param separator The separator between the PDBId and the ChainId
         * @return The MultiParser.
         */
        MultiResultStep chainList(Path path, String separator);

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
        SingleResultStep file(File file);

        /**
         * The files to parse.
         *
         * @param files The files.
         * @return Branch selection
         */
        MultiResultStep files(List<File> files);

        /**
         * The path to parse.
         *
         * @param path The path.
         * @return Branch selection
         */
        SingleResultStep path(Path path);

        /**
         * The paths to parse.
         *
         * @param paths The paths.
         * @return Branch selection
         */
        MultiResultStep paths(List<Path> paths);

        /**
         * The location of a local PDB installation. This requires the input of a chin list in the following step.
         *
         * @param localStructureRepository The local pdb.
         * @return Additional local list file selection.
         */
        LocalSourceStep localStructureRepository(LocalStructureRepository localStructureRepository);

        LocalSourceStep localCcdRepository(LocalCcdRepository localCcdRepository);

        MultiResultStep all();

        MultiResultStep all(int limit);

        /**
         * The location of a file as a sting.
         *
         * @param location The location.
         * @return Branch selection
         */
        SingleResultStep fileLocation(String location);

        /**
         * The location of files as strings.
         *
         * @param targetStructures The locations
         * @return Branch selection
         */
        MultiResultStep fileLocations(List<String> targetStructures);

        /**
         * Parses a structure from an input stream of a pdb file.
         *
         * @param inputStream The input stream of a pdb file.
         * @return Branch selection
         */
        SingleResultStep inputStream(InputStream inputStream);

    }

    /**
     * Initiates the selection of chains, if multiple structures have been chosen.
     */
    public interface MultiResultStep {

        MultiResultStep settings(StructureParserOptions.Setting... settings);

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
    public interface SingleResultStep {

        SingleResultStep settings(StructureParserOptions.Setting... settings);

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


    static class MultiResultSelector extends SourceSelector implements MultiResultStep {

        public MultiResultSelector(SourceLocation sourceLocation) {
            super(sourceLocation);
        }

        @Override
        public StructureIterator everything() {
            return iterator;
        }

        @Override
        public MultiResultStep settings(StructureParserOptions.Setting... settings) {
            iterator.getOptions().applySettings(settings);
            return this;
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
            iterator.setOptions(options);
            return this;
        }

        public Structure parse() {
            iterator.prepareNext();
            return iterator.next();
        }

    }

    static class SingleResultSelector extends SourceSelector implements SingleResultStep {

        public SingleResultSelector(SourceLocation sourceLocation) {
            super(sourceLocation);
        }

        @Override
        public SingleParserFacade everything() {
            return new SingleParserFacade(iterator);
        }

        @Override
        public SingleResultStep settings(StructureParserOptions.Setting... settings) {
            iterator.getOptions().applySettings(settings);
            if (Arrays.asList(settings).contains(StructureParserOptions.Setting.OMIT_LIGAND_INFORMATION)) {
                iterator.setLeafSkeletonFactory(new LeafSkeletonFactory(new NoCcdParsingBehavior()));
            }
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

        protected LocalStructureRepository localStructureRepository;

        protected LeafSkeletonFactory leafSkeletonFactory;

        public SourceSelector(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
        }

        public SourceSelector() {
        }

        @Override
        public SingleResultStep pdbIdentifier(String pdbIdentifier) {
            List<String> pdbIdentifiers = Collections.singletonList(pdbIdentifier);
            SingleResultSelector selector = new SingleResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, sourceLocation, localStructureRepository);
            assignRepository(selector.iterator);
            return selector;
        }

        public void assignRepository(StructureIterator iterator) {
            if (leafSkeletonFactory == null) {
               iterator.setLeafSkeletonFactory(new LeafSkeletonFactory(new RemoteCcdParsingBehavior()));
            }
        }

        @Override
        public MultiResultStep pdbIdentifiers(List<String> pdbIdentifiers) {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromIdentifiers(pdbIdentifiers, sourceLocation, localStructureRepository);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleResultStep file(File file) {
            SingleResultSelector selector = new SingleResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromFiles(Collections.singletonList(file), sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiResultStep files(List<File> files) {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromFiles(files, sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleResultStep path(Path path) {
            SingleResultSelector selector = new SingleResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromPaths(Collections.singletonList(path), sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiResultStep paths(List<Path> paths) {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromPaths(paths, sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleResultStep inputStream(InputStream inputStream) {
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
            SingleResultSelector selector = new SingleResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromFiles(Collections.singletonList(tempFile), sourceLocation);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public LocalSourceStep localStructureRepository(LocalStructureRepository localStructureRepository) {
            sourceLocation = localStructureRepository.getSourceLocation();
            this.localStructureRepository = localStructureRepository;
            return this;
        }

        @Override
        public LocalSourceStep localCcdRepository(LocalCcdRepository localCcdRepository) {
            leafSkeletonFactory = new LeafSkeletonFactory(new LocalCcdParsingBehavior(localCcdRepository));
            return this;
        }

        @Override
        public MultiResultStep chainList(Path path) {
            return chainList(path, "\t");
        }

        @Override
        public MultiResultStep chainList(Path path, String separator) {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            if (localStructureRepository != null) {
                selector.iterator = StructureIterator.createFromChainList(path, separator, localStructureRepository);
            } else {
                selector.iterator = StructureIterator.createFromChainList(path, separator, sourceLocation);
            }
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiResultStep all() {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocalPdb(localStructureRepository);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiResultStep all(int limit) {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocalPdb(localStructureRepository, limit);
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public SingleResultStep fileLocation(String location) {
            SingleResultSelector selector = new SingleResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocations(Collections.singletonList(location));
            assignRepository(selector.iterator);
            return selector;
        }

        @Override
        public MultiResultStep fileLocations(List<String> locations) {
            MultiResultSelector selector = new MultiResultSelector(sourceLocation);
            selector.iterator = StructureIterator.createFromLocations(locations);
            assignRepository(selector.iterator);
            return selector;
        }



    }

}
