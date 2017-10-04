package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Chain;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.interfaces.Model;
import de.bioforscher.singa.chemistry.physical.interfaces.Structure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.*;

/**
 * The implementation of {@link Structure}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfStructure implements Structure {

    /**
     * The original mmtf data.
     */
    private StructureDataInterface data;

    /**
     * The models that have already been requested.
     */
    private Map<Integer, MmtfModel> cachedModels;

    /**
     * Creates a new {@link MmtfStructure}
     * @param data The original mmtf data.
     */
    public MmtfStructure(StructureDataInterface data) {
        this.data = data;
        this.cachedModels = new HashMap<>();
    }

    @Override
    public String getPdbIdentifier() {
        return data.getStructureId();
    }

    @Override
    public String getTitle() {
        return data.getTitle();
    }

    @Override
    public List<Model> getAllModels() {
        List<Model> models = new ArrayList<>();
        for (int internalModelIndex = 0; internalModelIndex < data.getNumModels(); internalModelIndex++) {
            if (cachedModels.containsKey(internalModelIndex)) {
                models.add(cachedModels.get(internalModelIndex));
            } else {
                MmtfModel mmtfModel = new MmtfModel(data, internalModelIndex);
                cachedModels.put(internalModelIndex, mmtfModel);
                models.add(mmtfModel);
            }
        }
        return models;
    }

    @Override
    public Model getFirstModel() {
        if (cachedModels.containsKey(0)) {
            return cachedModels.get(0);
        } else {
            MmtfModel mmtfModel = new MmtfModel(data, 0);
            cachedModels.put(0, mmtfModel);
            return mmtfModel;
        }
    }

    @Override
    public Optional<Model> getModel(int modelIdentifier) {
        if (modelIdentifier > 0 && modelIdentifier <= data.getNumModels()) {
            return Optional.empty();
        }
        int modelIndex = modelIdentifier - 1;
        if (cachedModels.containsKey(modelIndex)) {
            return Optional.of(cachedModels.get(modelIndex));
        } else {
            MmtfModel mmtfModel = new MmtfModel(data, modelIndex);
            cachedModels.put(modelIndex, mmtfModel);
            return Optional.of(mmtfModel);
        }
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> chains = new ArrayList<>();
        List<Model> allModels = getAllModels();
        for (Model model : allModels) {
            chains.addAll(model.getAllChains());
        }
        return chains;
    }

    @Override
    public Chain getFirstChain() {
        return getFirstModel().getFirstChain();
    }

    @Override
    public Optional<Chain> getChain(int modelIdentifier, String chainIdentifier) {
        Optional<Model> modelOptional = getModel(modelIdentifier);
        if (!modelOptional.isPresent()) {
            return Optional.empty();
        }
        return modelOptional.get().getChain(chainIdentifier);
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
        Optional<Chain> chainOptional = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        if (!chainOptional.isPresent()) {
            return Optional.empty();
        }
        return chainOptional.get().getLeafSubstructure(leafIdentifier);
    }

    @Override
    public Structure getCopy() {
        return new MmtfStructure(data);
    }

}
