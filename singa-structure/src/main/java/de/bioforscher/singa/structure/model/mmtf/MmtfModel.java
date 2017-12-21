package de.bioforscher.singa.structure.model.mmtf;


import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.*;
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
     * The original bytes kept to copy.
     */
    private byte[] bytes;


    /**
     * The original data.
     */
    private StructureDataInterface data;
    /**
     * The chains that have already been requested.
     */
    private HashMap<String, MmtfChain> cachedChains;
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
    MmtfModel(StructureDataInterface data, byte[] bytes, int modelIndex) {
        this.data = data;
        this.bytes = bytes;
        this.modelIndex = modelIndex;
        chainMap = new TreeMap<>();
        cachedChains = new HashMap<>();

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
        bytes = mmtfModel.bytes;
        data = mmtfModel.data;
        modelIndex = mmtfModel.modelIndex;
        chainMap = new HashMap<>(mmtfModel.chainMap);
        cachedChains = new HashMap<>();
    }

    @Override
    public Integer getModelIdentifier() {
        return modelIndex + 1;
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> chains = new ArrayList<>();
        for (String chainIdentifier : chainMap.keySet()) {
            // cache
            if (cachedChains.containsKey(chainIdentifier)) {
                chains.add(cachedChains.get(chainIdentifier));
            } else {
                MmtfChain mmtfChain = new MmtfChain(data, bytes, chainIdentifier, chainMap.get(chainIdentifier), modelIndex);
                cachedChains.put(chainIdentifier, mmtfChain);
                chains.add(mmtfChain);
            }
        }
        return chains;
    }

    @Override
    public Chain getFirstChain() {
        final Map.Entry<String, List<Integer>> first = chainMap.entrySet().iterator().next();
        String chainIdentifier = first.getKey();
        if (cachedChains.containsKey(chainIdentifier)) {
            return cachedChains.get(chainIdentifier);
        } else {
            MmtfChain mmtfChain = new MmtfChain(data, bytes, chainIdentifier, first.getValue(), modelIndex);
            cachedChains.put(chainIdentifier, mmtfChain);
            return mmtfChain;
        }
    }

    @Override
    public Optional<Chain> getChain(String chainIdentifier) {
        if (!chainMap.containsKey(chainIdentifier)) {
            return Optional.empty();
        }
        if (cachedChains.containsKey(chainIdentifier)) {
            return Optional.of(cachedChains.get(chainIdentifier));
        } else {
            MmtfChain mmtfChain = new MmtfChain(data, bytes, chainIdentifier, chainMap.get(chainIdentifier), modelIndex);
            cachedChains.put(chainIdentifier, mmtfChain);
            return Optional.of(mmtfChain);
        }
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
    public LeafSubstructure<?> getFirstLeafSubstructure() {
        return getFirstChain().getFirstLeafSubstructure();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final Optional<Chain> chain = getChain(leafIdentifier.getChainIdentifier());
        if (chain.isPresent()) {
            if (chain.get().removeLeafSubstructure(leafIdentifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeLeafSubstructuresNotRelevantFor(LeafSubstructureContainer leafSubstructuresToKeep) {
        for (Chain chain : getAllChains()) {
            chain.removeLeafSubstructuresNotRelevantFor(leafSubstructuresToKeep);
        }
    }

    @Override
    public int getNumberOfLeafSubstructures() {
        int sum = 0;
        for (Chain chain : getAllChains()) {
            sum += chain.getNumberOfLeafSubstructures();
        }
        return sum;
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                leafSubstructure.removeAtom(atomIdentifier);
            }
        }
    }

    @Override
    public Model getCopy() {
        return new MmtfModel(this);
    }

    @Override
    public String toString() {
        return flatToString();
    }


}
