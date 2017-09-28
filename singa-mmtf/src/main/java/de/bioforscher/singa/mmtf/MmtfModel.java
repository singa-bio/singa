package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Chain;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.interfaces.Model;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.*;

/**
 * @author cl
 */
public class MmtfModel implements Model {

    private StructureDataInterface data;
    private int modelIdentifier;
    private Map<String, List<Integer>> chainMap;

    MmtfModel(StructureDataInterface data, int modelIdentifier) {
        this.data = data;
        this.modelIdentifier = modelIdentifier;
        this.chainMap = new TreeMap<>();

        if (modelIdentifier > data.getNumModels() - 1) {
            throw new IllegalArgumentException("Unable to access model with identifier: " + modelIdentifier);
        }

        // number of chains per model (soring according to assignment)
        int[] chainsPerModel = data.getChainsPerModel();
        int currentChainIndex = 0;
        // get group indices relevant for this chain
        for (int chainsPerModelIndex = 0; chainsPerModelIndex < chainsPerModel.length; chainsPerModelIndex++) {
            int endRange = currentChainIndex + chainsPerModel[chainsPerModelIndex];
            if (chainsPerModelIndex == modelIdentifier) {
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

    private MmtfModel(MmtfModel mmtfModel) {
        this.data = mmtfModel.data;
        this.modelIdentifier = mmtfModel.modelIdentifier;
        this.chainMap = new HashMap<>(mmtfModel.chainMap);
    }

    @Override
    public int getIdentifier() {
        return modelIdentifier+1;
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> chains = new ArrayList<>();
        for (String chain : chainMap.keySet()) {
            chains.add(new MmtfChain(data, chain, chainMap.get(chain), modelIdentifier));
        }
        return chains;
    }

    @Override
    public Chain getFirstChain() {
        final Map.Entry<String, List<Integer>> first = chainMap.entrySet().iterator().next();
        return new MmtfChain(data, first.getKey(), first.getValue(), modelIdentifier);
    }

    @Override
    public Optional<Chain> getChain(String chainIdentifier) {
        if (!chainMap.containsKey(chainIdentifier)) {
            return Optional.empty();
        }
        return Optional.of(new MmtfChain(data, chainIdentifier, chainMap.get(chainIdentifier), modelIdentifier));
    }

    @Override
    public List<LeafSubstructure<?>> getAllLeafSubstructures() {
        List<LeafSubstructure<?>> leafSubstructures = new ArrayList<>();
        List<Chain> allChains = getAllChains();
        for (Chain chain : allChains) {
            leafSubstructures.addAll(chain.getAllLeafSubstructures());
        }
        return leafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier) {
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
