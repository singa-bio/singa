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

    public OakChain getFirstChain() {
        return chains.firstEntry().getValue();
    }

    public Collection<OakChain> getAllChains() {
        return chains.values();
    }

    @Override
    public Set<String> getAllChainIdentifiers() {
        return new HashSet<>(chains.keySet());
    }

    @Override
    public Optional<OakChain> getChain(String chainIdentifier) {
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
    public List<OakLeafSubstructure> getAllLeafSubstructures() {
        List<OakLeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (OakChain chain : chains.values()) {
            final Collection<OakLeafSubstructure> leafSubstructures = chain.getAllLeafSubstructures();
            allLeafSubstructures.addAll(leafSubstructures);
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<OakLeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (OakChain chain : chains.values()) {
            final Optional<OakLeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
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
        for (OakChain chain : chains.values()) {
            final Optional<OakLeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
            if (optionalLeafSubstructure.isPresent()) {
                chain.removeLeafSubstructure(optionalLeafSubstructure.get().getIdentifier());
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<OakAtom> getAtom(Integer atomIdentifier) {
        for (OakChain chain : chains.values()) {
            final Collection<OakLeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (OakLeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<OakAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                if (optionalAtom.isPresent()) {
                    return optionalAtom;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (OakChain chain : chains.values()) {
            final Collection<OakLeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (OakLeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<OakAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
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
