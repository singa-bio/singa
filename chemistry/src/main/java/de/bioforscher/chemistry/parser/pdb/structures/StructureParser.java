package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.model.Structure;

import java.io.IOException;

/**
 * Created by leberech on 25/01/17.
 */
public class StructureParser {

    public static IdentifierStep from(StructureSources source) {
        return new Parser(source);
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
            this.source = source;
        }

        @Override
        public BranchSelectionStep identifier(String identifier) {
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

}
