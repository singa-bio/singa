package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.graph.model.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.Chain;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author cl
 */
public class OakModel implements Model {

    /**
     * The identifier of this entity.
     */
    private final Integer identifier;

    /**
     * The substructures of this substructure.
     */
    private TreeMap<String , OakChain> chains;

    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public OakModel(int identifier) {
        this.identifier = identifier;
        this.chains = new TreeMap<>();
    }

    public OakModel(OakModel model) {
        this.identifier = model.getIdentifier();
        this.chains = new TreeMap<>();
        for (OakChain chain : model.chains.values()) {
            this.chains.put(chain.getIdentifier(), chain.getCopy());
        }

    }

    @Override
    public Integer getIdentifier() {
        return identifier;
    }

    public Chain getFirstChain() {
        return chains.firstEntry().getValue();
    }

    public List<Chain> getAllChains() {
        return new ArrayList<>(chains.values());
    }

    @Override
    public Optional<Chain> getChain(String chainIdentifier) {
        if (this.chains.containsKey(chainIdentifier)) {
            return Optional.of(this.chains.get(chainIdentifier));
        }
        return Optional.empty();
    }

    public void addChain(OakChain chain) {
        this.chains.put(chain.getIdentifier(), chain);
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        List<LeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (Chain chain : this.chains.values()) {
            final List<LeafSubstructure> leafSubstructures = chain.getAllLeafSubstructures();
            allLeafSubstructures.addAll(leafSubstructures);
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (Chain chain : this.chains.values()) {
            final Optional<LeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
            if (optionalLeafSubstructure.isPresent()) {
                return optionalLeafSubstructure;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (Chain chain : this.chains.values()) {
            final Optional<LeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
            if (optionalLeafSubstructure.isPresent()) {
                chain.removeLeafSubstructure(optionalLeafSubstructure.get().getIdentifier());
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        for (Chain chain : this.chains.values()) {
            final List<LeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (LeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                if (optionalAtom.isPresent()) {
                    return optionalAtom;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (Chain chain : this.chains.values()) {
            final List<LeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (LeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                if (optionalAtom.isPresent()) {
                    leafSubstructure.removeAtom(atomIdentifier);
                }
            }
        }
    }

    @Override
    public OakModel getCopy() {
        return new OakModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OakModel that = (OakModel) o;

        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return this.identifier != null ? this.identifier.hashCode() : 0;
    }

}
