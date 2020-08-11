package bio.singa.structure.parser.pdb.structures.iterators;

import bio.singa.structure.parser.pdb.structures.LocalCifRepository;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;

/**
 * @author cl
 */
public class StructureReducer {

    private StructureParserOptions options;

    private boolean reduceModels;
    private int modelIdentifier;

    private boolean reduceChains;
    private String chainIdentifier;

    private LocalCifRepository localCifRepository;

    public StructureReducer() {
        options = new StructureParserOptions();
    }

    public StructureParserOptions getOptions() {
        return options;
    }

    public void setOptions(StructureParserOptions options) {
        this.options = options;
    }

    public boolean isReducingModels() {
        return reduceModels;
    }

    public void setReduceModels(boolean reduceModels) {
        this.reduceModels = reduceModels;
    }

    public int getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(int modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public boolean isReducingChains() {
        return reduceChains;
    }

    public void setReduceChains(boolean reduceChains) {
        this.reduceChains = reduceChains;
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public void setChainIdentifier(String chainIdentifier) {
        this.chainIdentifier = chainIdentifier;
    }

    public LocalCifRepository getLocalCifRepository() {
        return localCifRepository;
    }

    public void setLocalCifRepository(LocalCifRepository localCifRepository) {
        this.localCifRepository = localCifRepository;
    }

}
