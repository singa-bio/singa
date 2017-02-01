package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.model.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created by leberech on 25/01/17.
 */
public class StructureParser {

    private static final Logger logger = LoggerFactory.getLogger(StructureParser.class);

    public static IdentifierStep from(StructureSources source) {
        return new Parser(source);
    }

    public static IdentifierStep from(LocalPDB localPdb) {
        return new Parser(localPdb);
    }


    public interface IdentifierStep {
        BranchSelectionStep identifier(String identifier);
    }

    public interface BranchSelectionStep extends ChainStep {
        ChainStep model(int ModelIdentifier);

        ChainStep allModels();

        ParseStep everything();
    }

    public interface ChainStep {
        ParseStep chain(String chainIdentifier);

        ParseStep allChains();
    }

    public interface ParseStep {
        Structure parse() throws IOException;
    }

    public static class Parser implements IdentifierStep, BranchSelectionStep, ChainStep, ParseStep {

        StructureSources source;
        String identifier;
        LocalPDB localPdb;
        int modelIdentifier;
        String chainIdentifier;
        boolean allModels;
        boolean allChains;
        /**
         * signifies that models should be reduced in any way
         */
        boolean modelsReduced;
        boolean onlyAtoms;
        boolean connectChains;
        boolean connectLigands;

        public Parser(StructureSources source) {
            Objects.requireNonNull(source);
            this.source = source;
        }

        public Parser(LocalPDB localPdb) {
            Objects.requireNonNull(localPdb);
            this.source = StructureSources.PDB_LOCAL;
            this.localPdb = localPdb;
        }

        @Override
        public BranchSelectionStep identifier(String identifier) {
            Objects.requireNonNull(identifier);
            this.identifier = identifier;
            return this;
        }

        @Override
        public ChainStep model(int modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
            this.modelsReduced = true;
            return this;
        }

        @Override
        public ChainStep allModels() {
            this.allModels = true;
            return this;
        }

        @Override
        public ParseStep everything() {
            this.allModels = true;
            this.allChains = true;
            return this;
        }

        @Override
        public ParseStep chain(String chainIdentifier) {
            Objects.requireNonNull(chainIdentifier);
            this.chainIdentifier = chainIdentifier;
            if (!this.modelsReduced) {
                this.allModels = true;
            }
            return this;
        }

        @Override
        public ParseStep allChains() {
            this.allChains = true;
            return this;
        }

        @Override
        public Structure parse() throws IOException {
            return StructureCollector.parse(this);
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
