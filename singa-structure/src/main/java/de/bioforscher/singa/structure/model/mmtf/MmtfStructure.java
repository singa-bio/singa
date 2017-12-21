package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.*;
import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.decoder.GenericDecoder;
import org.rcsb.mmtf.decoder.ReaderUtils;
import org.rcsb.mmtf.serialization.MessagePackSerialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

/**
 * The implementation of {@link Structure}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfStructure implements Structure {

    /**
     * The original bytes kept to copy.
     */
    private byte[] bytes;

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
     *
     * @param bytes The original undecoded bytes.
     */
    public MmtfStructure(byte[] bytes) {
        this.bytes = bytes;
        data = bytesToStructureData(bytes);
        cachedModels = new HashMap<>();
    }

    /**
     * Creates a copy by re-decoding the original bytes.
     *
     * @param mmtfStructure The structure to be copied.
     */
    private MmtfStructure(MmtfStructure mmtfStructure) {
        this(mmtfStructure.bytes);
    }

    static StructureDataInterface bytesToStructureData(byte[] bytes) {
        MessagePackSerialization mmtfBeanSeDeMessagePackImpl = new MessagePackSerialization();
        try {
            return new GenericDecoder(mmtfBeanSeDeMessagePackImpl.deserialize(new ByteArrayInputStream(ReaderUtils.deflateGzip(bytes))));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
                MmtfModel mmtfModel = new MmtfModel(data, bytes, internalModelIndex);
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
            MmtfModel mmtfModel = new MmtfModel(data, bytes, 0);
            cachedModels.put(0, mmtfModel);
            return mmtfModel;
        }
    }

    @Override
    public Optional<Model> getModel(int modelIdentifier) {
        int modelIndex = modelIdentifier - 1;
        if (modelIdentifier < 0 ^ modelIdentifier > data.getNumModels()) {
            return Optional.empty();
        }
        if (cachedModels.containsKey(modelIndex)) {
            return Optional.of(cachedModels.get(modelIndex));
        } else {
            MmtfModel mmtfModel = new MmtfModel(data, bytes, modelIndex);
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
        Optional<Chain> chainOptional = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        if (!chainOptional.isPresent()) {
            return Optional.empty();
        }
        return chainOptional.get().getLeafSubstructure(leafIdentifier);
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (Chain chain : getAllChains()) {
            if (chain.removeLeafSubstructure(leafIdentifier)) {
                return true;
            }
        }
        return false;
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
    public Structure getCopy() {
        return new MmtfStructure(this);
    }

    @Override
    public String toString() {
        return "MmtfStructure{" +
                "pdbIdentifier='" + data.getStructureId() + '\'' +
                ", title='" + data.getTitle() + '\'' +
                '}';
    }
}
