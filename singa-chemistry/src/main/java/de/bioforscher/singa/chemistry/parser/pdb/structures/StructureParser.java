package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.LeafSkeleton;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Pair;
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
    public static IdentifierStep online() {
        return new SourceSelector();
    }

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
         * @param localPDB The local pdb.
         * @param pdbIdentifier The PDB identifier.
         * @return Branch selection
         */
        SingleBranchStep localPDB(LocalPDB localPDB, String pdbIdentifier);

        /**
         * The location of a local PDB installation in addition to a list of structures, that are to be parsed.
         *
         * @param localPDB The local pdb.
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

    }


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

    public interface SingleChainStep {
        SingleParser chainIdentifier(String chainIdentifier);

        SingleParser allChains();

        SingleParser everything();

        Structure parse() throws StructureParserException;
    }

    public interface MultiChainStep {
        MultiParser chain(String chainIdentifier);

        MultiParser allChains();

        MultiParser everything();

        List<Structure> parse();
    }

    public static class SingleParser {

        SingleReducingSelector selector;

        SingleParser(SingleReducingSelector selector) {
            this.selector = selector;
        }

        public SingleParser setOptions(StructureParserOptions options) {
            this.selector.options = options;
            return this;
        }

        public Structure parse() throws StructureParserException {
            return StructureCollector.parse(this.selector.sourceSelector.contentIterator.next(), this.selector);
        }
    }

    public static class MultiParser implements Iterator<Structure> {

        MultiReducingSelector selector;

        MultiParser(MultiReducingSelector selector) {
            this.selector = selector;
        }

        public int getNumberOfQueuedStructures() {
            return this.selector.sourceSelector.contentIterator.getNumberOfQueuedStructures();
        }

        public int getNumberOfRemainingStructures() {
            return this.selector.sourceSelector.contentIterator.getNumberOfRemainingStructures();
        }

        public String getCurrentPdbIdentifier() {
            return this.selector.sourceSelector.contentIterator.getCurrentPdbIdentifier();
        }

        public String getCurrentChainIdentifier() {
            return this.selector.sourceSelector.contentIterator.getCurrentChainIdentifier();
        }

        public MultiParser setOptions(StructureParserOptions options) {
            this.selector.options = options;
            return this;
        }

        public List<Structure> parse() {
            logger.info("parsing {} structures ", getNumberOfQueuedStructures());
            List<Structure> structures = new ArrayList<>();
            this.selector.sourceSelector.contentIterator.forEachRemaining(lines -> {
                try {
                    structures.add(StructureCollector.parse(lines, this.selector));
                } catch (StructureParserException | UncheckedIOException e) {
                    logger.warn("failed to parse structure", e);
                }
            });
            return structures;
        }

        @Override
        synchronized public boolean hasNext() {
            return this.selector.sourceSelector.contentIterator.hasNext();
        }

        @Override
        synchronized public Structure next() {
            return StructureCollector.parse(this.selector.sourceSelector.contentIterator.next(), this.selector);
        }
    }


    public static class MultiReducingSelector extends Reducer implements MultiBranchStep {

        MultiReducingSelector(SourceSelector sourceSelector) {
            super(sourceSelector);
        }

        @Override
        public MultiChainStep model(int modelIdentifier) {
            setModel(modelIdentifier);
            return this;
        }

        @Override
        public MultiChainStep allModels() {
            this.allModels = true;
            return this;
        }

        @Override
        public MultiParser everything() {
            setEverything();
            return new MultiParser(this);
        }

        private MultiParser mapping() {
            this.parseMapping = true;
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

    public static class SingleReducingSelector extends Reducer implements SingleBranchStep {

        SingleReducingSelector(SourceSelector sourceSelector) {
            super(sourceSelector);
        }

        @Override
        public SingleChainStep model(int modelIdentifier) {
            setModel(modelIdentifier);
            return this;
        }

        @Override
        public SingleChainStep allModels() {
            this.allModels = true;
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

    protected static class Reducer {

        SourceSelector sourceSelector;

        Map<String, LeafSkeleton> skeletons;

        String pdbIdentifier;
        int modelIdentifier;
        String chainIdentifier;

        boolean allModels = true;
        boolean allChains = true;
        boolean parseMapping = false;

        StructureParserOptions options = new StructureParserOptions();

        /**
         * signifies that models should be reduced in any way
         */
        boolean modelsReduced;

        Reducer(SourceSelector sourceSelector) {
            this.sourceSelector = sourceSelector;
            this.skeletons = new HashMap<>();
        }

        public void setModel(int modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
            this.modelsReduced = true;
            this.allModels = false;
        }

        public void setEverything() {
            if (!this.modelsReduced) {
                this.allModels = true;
            }
            this.allChains = true;
        }

        public void updatePdbIdentifer() {
            this.pdbIdentifier = this.sourceSelector.contentIterator.getCurrentPdbIdentifier();
        }

        public void updateChainIdentifier() {
            this.chainIdentifier = this.sourceSelector.contentIterator.getCurrentChainIdentifier();
        }

        public void setChainIdentifier(String chainIdentifier) {
            Objects.requireNonNull(chainIdentifier);
            this.chainIdentifier = chainIdentifier;
            this.allChains = false;
            if (!this.modelsReduced) {
                this.allModels = true;
            }
        }

        public void setAllChains() {
            this.allChains = true;
        }

        @Override
        public String toString() {
            return "Reducer{pdbIdentifier='" + this.pdbIdentifier + '\'' +
                    ", modelIdentifier=" + this.modelIdentifier +
                    ", chainIdentifier='" + this.chainIdentifier + '\'' +
                    ", allModels=" + this.allModels +
                    ", allChains=" + this.allChains +
                    '}';
        }
    }

    public static class SourceSelector implements LocalSourceStep, IdentifierStep, AdditionalLocalSourceStep {

        StructureContentIterator contentIterator;

        private LocalPDB localPDB;

        @Override
        public SingleBranchStep pdbIdentifier(String pdbIdentifier) {
            this.contentIterator = new StructureContentIterator(pdbIdentifier);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep pdbIdentifiers(List<String> pdbIdentifiers) {
            this.contentIterator = new StructureContentIterator(String.class, pdbIdentifiers);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep file(File file) {
            this.contentIterator = new StructureContentIterator(file);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep files(List<File> files) {
            this.contentIterator = new StructureContentIterator(File.class, files);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep path(Path path) {
            this.contentIterator = new StructureContentIterator(path);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep paths(List<Path> paths) {
            this.contentIterator = new StructureContentIterator(Path.class, paths);
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
            this.contentIterator = new StructureContentIterator(tempFile);
            return new SingleReducingSelector(this);
        }

        @Override
        public AdditionalLocalSourceStep localPDB(LocalPDB localPDB) {
            this.localPDB = localPDB;
            return this;
        }

        @Override
        public MultiBranchStep localPDB(LocalPDB localPDB, List<String> pdbIdentifiers) {
            this.contentIterator = new StructureContentIterator(localPDB, pdbIdentifiers);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep localPDB(LocalPDB localPDB, String pdbIdentifier) {
            this.contentIterator = new StructureContentIterator(localPDB, pdbIdentifier);
            return new SingleReducingSelector(this);
        }

        @Override
        public SingleBranchStep fileLocation(String location) {
            this.contentIterator = new StructureContentIterator(Paths.get(location));
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep fileLocations(List<String> locations) {
            List<Path> paths = locations.stream()
                    .map(Paths::get)
                    .collect(Collectors.toList());
            this.contentIterator = new StructureContentIterator(Path.class, paths);
            return new MultiReducingSelector(this);
        }

        @Override
        public MultiParser chainList(Path path, String separator) {
            try {
                if (this.localPDB != null) {
                    this.contentIterator = new StructureContentIterator(readMappingFile(path, separator), this.localPDB);
                } else {
                    this.contentIterator = new StructureContentIterator(readMappingFile(path, separator));
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Could not open input stream for mapping file.", e);
            }
            return new MultiReducingSelector(this).mapping();
        }

        @Override
        public MultiParser chainList(Path path) {
            return chainList(path, "\t");
        }

        private List<Pair<String>> readMappingFile(Path mappingPath, String separator) throws IOException {
            InputStream inputStream = Files.newInputStream(mappingPath);
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    return composePairsForChainList(bufferedReader.lines().collect(Collectors.toList()), separator);
                }
            }
        }

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
     * Represents a local PDB copy.
     */
    public static class LocalPDB {

        private static final Path BASE_PATH = Paths.get("data/structures/divided/pdb");

        private Path localPdbPath;

        public LocalPDB(String localPdbLocation) {
            this.localPdbPath = Paths.get(localPdbLocation);
        }

        public Path getLocalPdbPath() {
            return this.localPdbPath;
        }

        /**
         * Returns the full path of a given PDB-ID in respect to the local PDB copy.
         *
         * @param pdbIdentifier The PDB-ID for which the full path should be retrieved.
         * @return The full path of the given PDB-ID.
         */
        public Path getPathForPdbIdentifier(String pdbIdentifier) {
            pdbIdentifier = pdbIdentifier.toLowerCase();
            return this.localPdbPath.resolve(BASE_PATH).resolve(pdbIdentifier.substring(1, 3))
                    .resolve(pdbIdentifier).resolve("pdb" + pdbIdentifier + ".ent.gz");
        }
    }
}
