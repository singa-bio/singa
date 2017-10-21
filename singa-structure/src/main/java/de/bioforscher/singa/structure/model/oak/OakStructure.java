package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.families.LigandFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.identifiers.UniqueAtomIdentifer;
import de.bioforscher.singa.structure.model.interfaces.*;

import java.util.*;

/**
 * @author cl
 */
public class OakStructure implements Structure {

    /**
     * The PDB identifier of the structure.
     */
    private String pdbIdentifier;

    /**
     * The title of the structure.
     */
    private String title;

    /**
     * The branches this structure contains.
     */
    private TreeMap<Integer, OakModel> models;

    private int lastAddedAtomIdentifier;

    public OakStructure() {
        this.models = new TreeMap<>();
    }

    public OakStructure(OakStructure structure) {
        this.pdbIdentifier = structure.getPdbIdentifier();
        this.title = structure.title;
        this.models = new TreeMap<>();
        for (OakModel model : structure.models.values()) {
            this.models.put(model.getIdentifier(), model.getCopy());
        }
    }

    @Override
    public String getPdbIdentifier() {
        return this.pdbIdentifier;
    }

    public void setPdbIdentifier(String pdbIdentifier) {
        this.pdbIdentifier = pdbIdentifier;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<Model> getAllModels() {
        return new ArrayList<>(models.values());
    }

    @Override
    public Model getFirstModel() {
        return models.firstEntry().getValue();
    }

    @Override
    public Optional<Model> getModel(int modelIdentifier) {
        if (models.containsKey(modelIdentifier)) {
            return Optional.of(models.get(modelIdentifier));
        }
        return Optional.empty();
    }

    public void addModel(OakModel model) {
        this.models.put(model.getIdentifier(), model);
    }

    @Override
    public Optional<Chain> getChain(int modelIdentifier, String chainIdentifier) {
        final Optional<Model> optionalModel = getModel(modelIdentifier);
        if (optionalModel.isPresent()) {
            return optionalModel.get().getChain(chainIdentifier);
        }
        return Optional.empty();
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> allChains = new ArrayList<>();
        for (OakModel model : models.values()) {
            allChains.addAll(model.getAllChains());
        }
        return allChains;
    }

    @Override
    public Chain getFirstChain() {
        return getFirstModel().getFirstChain();
    }

    @Override
    public List<LeafSubstructure<?>> getAllLeafSubstructures() {
        List<LeafSubstructure<?>> allLeafSubstructures = new ArrayList<>();
        for (OakModel model : models.values()) {
            allLeafSubstructures.addAll(model.getAllLeafSubstructures());
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure<?>> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final Optional<Chain> chain = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        if (chain.isPresent()) {
            return chain.get().getLeafSubstructure(leafIdentifier);
        }
        return Optional.empty();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final Optional<Chain> chain = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        if (chain.isPresent()) {
            if (chain.get().getLeafSubstructure(leafIdentifier).isPresent()) {
                chain.get().removeLeafSubstructure(leafIdentifier);
                return true;
            }
        }
        getAllAtoms();
        return false;
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> atom = leafSubstructure.getAtom(atomIdentifier);
            if (atom.isPresent()) {
                return atom;
            }
        }
        return Optional.empty();
    }

    public Optional<Map.Entry<UniqueAtomIdentifer, Atom>> getUniqueAtomEntry(int atomSerial) {
        for (Model model : getAllModels()) {
            for (Chain chain : model.getAllChains()) {
                for (LeafSubstructure leafSubstructure : chain.getAllLeafSubstructures()) {
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        if (atom.getIdentifier().equals(atomSerial)) {
                            UniqueAtomIdentifer identifier = new UniqueAtomIdentifer(this.pdbIdentifier, model.getIdentifier(),
                                    chain.getIdentifier(), leafSubstructure.getIdentifier().getSerial(), leafSubstructure.getIdentifier().getInsertionCode(),
                                    atomSerial);
                            return Optional.of(new AbstractMap.SimpleEntry<>(identifier, atom));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public void addAtom(String chainIdentifier, String threeLetterCode, Vector3D position) {
        Optional<Chain> optionalChain = getFirstModel().getChain(chainIdentifier);
        if (optionalChain.isPresent()) {
            OakChain chain = (OakChain) optionalChain.get();
            OakLigand leafSubstructure = new OakLigand(chain.getNextLeafIdentifier(), new LigandFamily(threeLetterCode));
            this.lastAddedAtomIdentifier++;
            leafSubstructure.addAtom(new OakAtom(this.lastAddedAtomIdentifier, ElementProvider.UNKOWN, "CA", position));
            chain.addLeafSubstructure(leafSubstructure);
        } else {
            throw new IllegalStateException("Unable to add atom to chain "+chainIdentifier+", chain could not be found.");
        }
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<Atom> atom = leafSubstructure.getAtom(atomIdentifier);
            if (atom.isPresent()) {
                leafSubstructure.removeAtom(atomIdentifier);
                return;
            }
        }
    }

    public int getLastAddedAtomIdentifier() {
        return lastAddedAtomIdentifier;
    }

    public void setLastAddedAtomIdentifier(int lastAddedAtomIdentifier) {
        this.lastAddedAtomIdentifier = lastAddedAtomIdentifier;
    }

    @Override
    public Structure getCopy() {
        return new OakStructure(this);
    }

}
