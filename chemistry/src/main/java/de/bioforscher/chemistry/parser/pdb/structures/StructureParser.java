package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.parser.pdb.structures.tokens.LeafSkeleton;
import de.bioforscher.chemistry.physical.model.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by leberech on 25/01/17.
 */
public class StructureParser {

    private static final Logger logger = LoggerFactory.getLogger(StructureParser.class);

    public static LocalSourceStep local() {
        return new SourceSelector();
    }

    public static IdentifierStep online() {
        return new SourceSelector();
    }

    public interface IdentifierStep {

        /**
         * The identifier of the PDB structure.
         *
         * @param identifier The identifier.
         * @return Model selection
         */
        SingleBranchStep identifier(String identifier);

        /**
         * The identifiers of the PDB structures.
         *
         * @param identifiers The identifiers.
         * @return Batch selection
         */
        MultiBranchStep identifiers(List<String> identifiers);

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

        SingleBranchStep localPDB(LocalPDB localPDB, String identifier);

        MultiBranchStep localPDB(LocalPDB localPDB, List<String> identifiers);

        SingleBranchStep fileLocation(String location);

        MultiBranchStep fileLocations(List<String> targetStructures);
    }


    public interface MultiBranchStep extends MultiChainStep {

        /**
         * If only a single model should be parsed, give its identifier here.
         *
         * @param modelIdentifier The identifier of the model to parse.
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
         * If only a single model should be parsed, give its identifier here.
         *
         * @param modelIdentifier The identifier of the model to parse.
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
        SingleParser chain(String chainIdentifier);

        SingleParser allChains();

        SingleParser everything();

        Structure parse();
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

        public Structure parse() {
            return StructureCollector.parse(this.selector.sourceSelector.contentIterator.next(), this.selector);
        }
    }

    public static class MultiParser implements Iterator<Structure> {

        MultiReducingSelector selector;

        MultiParser(MultiReducingSelector selector) {
            this.selector = selector;
        }

        public List<Structure> parse() {
            logger.info("parsing {} structures ", this.selector.sourceSelector.contentIterator.getNumberOfQueuedStructures());
            List<Structure> structures = new ArrayList<>();
            this.selector.sourceSelector.contentIterator.forEachRemaining(lines -> {
                try {
                    structures.add(StructureCollector.parse(lines, this.selector));
                } catch (UncheckedIOException e) {
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

        @Override
        public MultiParser chain(String chainIdentifier) {
            setChain(chainIdentifier);
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
        public SingleParser chain(String chainIdentifier) {
            setChain(chainIdentifier);
            return new SingleParser(this);
        }

        @Override
        public SingleParser allChains() {
            setAllChains();
            return new SingleParser(this);
        }

        @Override
        public Structure parse() {
            setEverything();
            return new SingleParser(this).parse();
        }

    }

    protected static class Reducer {

        SourceSelector sourceSelector;

        Map<String, LeafSkeleton> skeletons;

        int modelIdentifier;
        String chainIdentifier;
        boolean allModels;
        boolean allChains;

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
        }

        public void setEverything() {
            if (!this.modelsReduced) {
                this.allModels = true;
            }
            this.allChains = true;
        }

        public void setChain(String chainIdentifier) {
            Objects.requireNonNull(chainIdentifier);
            this.chainIdentifier = chainIdentifier;
            if (!this.modelsReduced) {
                this.allModels = true;
            }
        }

        public void setAllChains() {
            this.allChains = true;
        }

    }

    public static class SourceSelector implements LocalSourceStep, IdentifierStep {

        StructureContentIterator contentIterator;

        @Override
        public SingleBranchStep identifier(String identifier) {
            this.contentIterator = new StructureContentIterator(identifier);
            return new SingleReducingSelector(this);
        }

        @Override
        public MultiBranchStep identifiers(List<String> identifiers) {
            this.contentIterator = new StructureContentIterator(String.class, identifiers);
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
        public MultiBranchStep localPDB(LocalPDB localPDB, List<String> identifiers) {
            this.contentIterator = new StructureContentIterator(localPDB, identifiers);
            return new MultiReducingSelector(this);
        }

        @Override
        public SingleBranchStep localPDB(LocalPDB localPDB, String identifiers) {
            this.contentIterator = new StructureContentIterator(localPDB, identifiers);
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
    }

    /**
     * Represents a local PDB copy.
     */
    public static class LocalPDB {

        private Path localPdbPath;

        public LocalPDB(String localPdbLocation) {
            this.localPdbPath = Paths.get(localPdbLocation);
        }

        public Path getLocalPdbPath() {
            return this.localPdbPath;
        }
    }

}
