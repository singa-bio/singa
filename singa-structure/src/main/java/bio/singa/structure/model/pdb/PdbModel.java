package bio.singa.structure.model.pdb;

import bio.singa.structure.model.interfaces.*;

import java.util.*;

/**
 * @author cl
 */
public class PdbModel implements Model {

    /**
     * The identifier of this entity.
     */
    private Integer identifier;

    /**
     * The substructures of this substructure.
     */
    private final TreeMap<String, PdbChain> chains;

    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public PdbModel(int identifier) {
        this.identifier = identifier;
        chains = new TreeMap<>();
    }

    public PdbModel(PdbModel model) {
        identifier = model.getModelIdentifier();
        chains = new TreeMap<>();
        for (PdbChain chain : model.chains.values()) {
            chains.put(chain.getChainIdentifier(), chain.getCopy());
        }
    }

    @Override
    public int getModelIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public PdbChain getFirstChain() {
        return chains.firstEntry().getValue();
    }

    public Collection<PdbChain> getAllChains() {
        return chains.values();
    }

    @Override
    public Set<String> getAllChainIdentifiers() {
        return chains.keySet();
    }

    @Override
    public Optional<PdbChain> getChain(String chainIdentifier) {
        if (chains.containsKey(chainIdentifier)) {
            return Optional.of(chains.get(chainIdentifier));
        }
        return Optional.empty();
    }

    public void addChain(PdbChain chain) {
        chains.put(chain.getChainIdentifier(), chain);
    }

    @Override
    public void removeChain(String chainIdentifier) {
        chains.remove(chainIdentifier);
    }

    @Override
    public List<PdbLeafSubstructure> getAllLeafSubstructures() {
        List<PdbLeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (PdbChain chain : chains.values()) {
            final Collection<PdbLeafSubstructure> leafSubstructures = chain.getAllLeafSubstructures();
            allLeafSubstructures.addAll(leafSubstructures);
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<PdbLeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (PdbChain chain : chains.values()) {
            final Optional<PdbLeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
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
        for (PdbChain chain : chains.values()) {
            final Optional<PdbLeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
            if (optionalLeafSubstructure.isPresent()) {
                chain.removeLeafSubstructure(optionalLeafSubstructure.get().getIdentifier());
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<PdbAtom> getAtom(Integer atomIdentifier) {
        for (PdbChain chain : chains.values()) {
            final Collection<PdbLeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (PdbLeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<PdbAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                if (optionalAtom.isPresent()) {
                    return optionalAtom;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (PdbChain chain : chains.values()) {
            final Collection<PdbLeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (PdbLeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<PdbAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                optionalAtom.ifPresent(atom -> leafSubstructure.removeAtom(atomIdentifier));
            }
        }
    }

    @Override
    public PdbModel getCopy() {
        return new PdbModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdbModel model = (PdbModel) o;

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
