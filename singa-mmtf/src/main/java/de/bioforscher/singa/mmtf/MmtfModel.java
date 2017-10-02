package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Chain;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.interfaces.Model;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.*;

/**
 * The implementation of {@link Model} for mmtf structures. Remembers the index of the model and a mapping of chain
 * identifiers relevant for this model, as well as their respective identifiers in the chain data arrays.
 *
 * @author cl
 */
public class MmtfModel implements Model {

    /**
     * The original data.
     */
    private StructureDataInterface data;

    /**
     * The index of this model in the model data array (the model identifier is the model index + 1).
     */
    private int modelIndex;

    /**
     * A mapping of chain identifiers relevant for this model, and their respective identifiers in the chain data
     * arrays.
     */
    private Map<String, List<Integer>> chainMap;

    /**
     * Creates a new {@link MmtfModel}.
     *
     * @param data The original data.
     * @param modelIndex The index of the model in the model data array.
     */
    MmtfModel(StructureDataInterface data, int modelIndex) {
        this.data = data;
        this.modelIndex = modelIndex;
        this.chainMap = new TreeMap<>();

        if (modelIndex > data.getNumModels() - 1) {
            throw new IllegalArgumentException("Unable to access model with identifier: " + modelIndex);
        }

        // number of chains per model (soring according to assignment)
        int[] chainsPerModel = data.getChainsPerModel();
        int currentChainIndex = 0;
        // get group indices relevant for this chain
        for (int chainsPerModelIndex = 0; chainsPerModelIndex < chainsPerModel.length; chainsPerModelIndex++) {
            int endRange = currentChainIndex + chainsPerModel[chainsPerModelIndex];
            if (chainsPerModelIndex == modelIndex) {
                for (int groupIndex = currentChainIndex; groupIndex <= endRange - 1; groupIndex++) {
                    final String chainName = data.getChainNames()[groupIndex];
                    if (!chainMap.containsKey(chainName)) {
                        chainMap.put(chainName, new ArrayList<>());
                        chainMap.get(chainName).add(groupIndex);
                    } else {
                        chainMap.get(chainName).add(groupIndex);
                    }

                }
            }
        }

    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfModel} to a new instance.
     *
     * @param mmtfModel The {@link MmtfModel} to copy.
     */
    private MmtfModel(MmtfModel mmtfModel) {
        this.data = mmtfModel.data;
        this.modelIndex = mmtfModel.modelIndex;
        this.chainMap = new HashMap<>(mmtfModel.chainMap);
    }

    @Override
    public int getIdentifier() {
        return modelIndex + 1;
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> chains = new ArrayList<>();
        for (String chain : chainMap.keySet()) {
            chains.add(new MmtfChain(data, chain, chainMap.get(chain), modelIndex));
        }
        return chains;
    }

    @Override
    public Chain getFirstChain() {
        final Map.Entry<String, List<Integer>> first = chainMap.entrySet().iterator().next();
        return new MmtfChain(data, first.getKey(), first.getValue(), modelIndex);
    }

    @Override
    public Optional<Chain> getChain(String chainIdentifier) {
        if (!chainMap.containsKey(chainIdentifier)) {
            return Optional.empty();
        }
        return Optional.of(new MmtfChain(data, chainIdentifier, chainMap.get(chainIdentifier), modelIndex));
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        List<LeafSubstructure> leafSubstructures = new ArrayList<>();
        List<Chain> allChains = getAllChains();
        for (Chain chain : allChains) {
            leafSubstructures.addAll(chain.getAllLeafSubstructures());
        }
        return leafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        Optional<Chain> chainOptional = getChain(leafIdentifier.getChainIdentifier());
        if (!chainOptional.isPresent()) {
            return Optional.empty();
        }
        return chainOptional.get().getLeafSubstructure(leafIdentifier);
    }

    @Override
    public Model getCopy() {
        return new MmtfModel(this);
    }

}
