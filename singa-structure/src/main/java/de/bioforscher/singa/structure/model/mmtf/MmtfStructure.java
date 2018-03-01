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
    private final byte[] bytes;

    /**
     * The original mmtf data.
     */
    private final StructureDataInterface data;

    /**
     * The models that have been removed from the structure.
     */
    private Set<Integer> removedModels;

    /**
     * The models that have already been requested.
     */
    private final Map<Integer, MmtfModel> cachedModels;

    /**
     * Creates a new {@link MmtfStructure}
     *
     * @param bytes The original undecoded bytes.
     */
    public MmtfStructure(byte[] bytes) {
        this(bytes, true);
    }

    /**
     * Creates a new {@link MmtfStructure}
     *
     * @param bytes The original undecoded bytes.
     * @param deflate Signifies if the byte array should be deflated.
     */
    public MmtfStructure(byte[] bytes, boolean deflate) {
        this.bytes = bytes;
        data = bytesToStructureData(bytes, deflate);
        cachedModels = new HashMap<>();
        removedModels = new HashSet<>();
    }

    /**
     * Creates a copy by re-decoding the original bytes.
     *
     * @param mmtfStructure The structure to be copied.
     */
    private MmtfStructure(MmtfStructure mmtfStructure) {
        this(mmtfStructure.bytes);
    }

    static StructureDataInterface bytesToStructureData(byte[] bytes, boolean deflate) {
        MessagePackSerialization mmtfBeanSeDeMessagePackImpl = new MessagePackSerialization();
        try {
            byte[] gzip;
            if (deflate) {
                gzip = ReaderUtils.deflateGzip(bytes);
            } else {
                gzip = bytes;
            }
            return new GenericDecoder(mmtfBeanSeDeMessagePackImpl.deserialize(new ByteArrayInputStream(gzip)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String getPdbIdentifier() {
        return data.getStructureId().toLowerCase();
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
                if (!removedModels.contains(internalModelIndex)) {
                    MmtfModel mmtfModel = new MmtfModel(data, bytes, internalModelIndex);
                    cachedModels.put(internalModelIndex, mmtfModel);
                    models.add(mmtfModel);
                }
            }
        }
        return models;
    }

    @Override
    public Set<Integer> getAllModelIdentifiers() {
        Set<Integer> modelIdentifiers = new HashSet<>();
        for (int internalModelIndex = 0; internalModelIndex < data.getNumModels(); internalModelIndex++) {
            if (!removedModels.contains(internalModelIndex)) {
                modelIdentifiers.add(internalModelIndex + 1);
            }
        }
        return modelIdentifiers;
    }

    @Override
    public Model getFirstModel() {
        if (cachedModels.containsKey(0)) {
            return cachedModels.get(0);
        } else {
            TreeSet<Integer> sortedModelIdentifiers = new TreeSet<>(getAllModelIdentifiers());
            for (Integer modelIdentifier : sortedModelIdentifiers) {
                int internalModelIndex = modelIdentifier - 1;
                if (!removedModels.contains(internalModelIndex)) {
                    MmtfModel mmtfModel = new MmtfModel(data, bytes, internalModelIndex);
                    cachedModels.put(internalModelIndex, mmtfModel);
                    return mmtfModel;
                }
            }
        }
        throw new IllegalStateException("The structure does not contain any model. Either each model has been removed or no models have been assigned to this strucutre.");
    }

    @Override
    public Optional<Model> getModel(int modelIdentifier) {
        int internalModelIndex = modelIdentifier - 1;
        if (modelIdentifier < 0 ^ modelIdentifier > data.getNumModels() || removedModels.contains(internalModelIndex)) {
            return Optional.empty();
        }
        if (cachedModels.containsKey(internalModelIndex)) {
            return Optional.of(cachedModels.get(internalModelIndex));
        } else {
            MmtfModel mmtfModel = new MmtfModel(data, bytes, internalModelIndex);
            cachedModels.put(internalModelIndex, mmtfModel);
            return Optional.of(mmtfModel);
        }
    }

    @Override
    public void removeModel(int modelIdentifier) {
        int internalModelIndex = modelIdentifier - 1;
        cachedModels.remove(internalModelIndex);
        removedModels.add(internalModelIndex);
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
        return modelOptional.flatMap(model -> model.getChain(chainIdentifier));
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
        return chainOptional.flatMap(chain -> chain.getLeafSubstructure(leafIdentifier));
    }

    @Override
    public LeafSubstructure<?> getFirstLeafSubstructure() {
        return getFirstChain().getFirstLeafSubstructure();
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
            optionalAtom.ifPresent(atom -> leafSubstructure.removeAtom(atomIdentifier));
        }
    }

    @Override
    public Structure getCopy() {
        return new MmtfStructure(this);
    }

    @Override
    public String toString() {
        return flatToString();
    }
}
