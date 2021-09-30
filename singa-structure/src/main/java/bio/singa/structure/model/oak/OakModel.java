package bio.singa.structure.model.oak;

import bio.singa.structure.model.interfaces.*;

import java.util.*;

/**
 * @author cl
 */
public class OakModel implements Model {

    /**
     * The identifier of this entity.
     */
    private Integer identifier;

    /**
     * The substructures of this substructure.
     */
    private final TreeMap<String, OakChain> chains;

    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public OakModel(int identifier) {
        this.identifier = identifier;
        chains = new TreeMap<>();
    }

    public OakModel(OakModel model) {
        identifier = model.getModelIdentifier();
        chains = new TreeMap<>();
        for (OakChain chain : model.chains.values()) {
            chains.put(chain.getChainIdentifier(), chain.getCopy());
        }
    }

    @Override
    public Integer getModelIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Chain getFirstChain() {
        return chains.firstEntry().getValue();
    }

    public List<Chain> getAllChains() {
        return new ArrayList<>(chains.values());
    }

    @Override
    public Set<String> getAllChainIdentifiers() {
        return new HashSet<>(chains.keySet());
    }

    @Override
    public Optional<Chain> getChain(String chainIdentifier) {
        if (chains.containsKey(chainIdentifier)) {
            return Optional.of(chains.get(chainIdentifier));
        }
        return Optional.empty();
    }

    public void addChain(OakChain chain) {
        chains.put(chain.getChainIdentifier(), chain);
    }

    @Override
    public void removeChain(String chainIdentifier) {
        chains.remove(chainIdentifier);
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        List<LeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (Chain chain : chains.values()) {
            final List<LeafSubstructure> leafSubstructures = chain.getAllLeafSubstructures();
            allLeafSubstructures.addAll(leafSubstructures);
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (Chain chain : chains.values()) {
            final Optional<LeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
            if (optionalLeafSubstructure.isPresent()) {
                return optionalLeafSubstructure;
            }
        }
        return Optional.empty();
    }

    @Override
    public LeafSubstructure getFirstLeafSubstructure() {
        return getFirstChain().getFirstLeafSubstructure();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (Chain chain : chains.values()) {
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
        for (Chain chain : chains.values()) {
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
        for (Chain chain : chains.values()) {
            final List<LeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (LeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                optionalAtom.ifPresent(atom -> leafSubstructure.removeAtom(atomIdentifier));
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

        OakModel model = (OakModel) o;

        return identifier != null ? identifier.equals(model.identifier) : model.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
