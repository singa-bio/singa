package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.PdbAtom;
import bio.singa.structure.model.pdb.PdbChain;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import bio.singa.structure.model.pdb.PdbModel;

import java.util.*;

public class CifModel implements Model {

    /**
     * The identifier of this entity.
     */
    private Integer identifier;

    /**
     * The substructures of this substructure.
     */
    private final TreeMap<String, CifChain> chains;

    public CifModel(Integer identifier) {
        this.identifier = identifier;
        chains = new TreeMap<>();
    }

    public CifModel(CifModel model) {
        identifier = model.getModelIdentifier();
        chains = new TreeMap<>();
        for (CifChain chain : model.chains.values()) {
            chains.put(chain.getChainIdentifier(), chain.getCopy());
        }
    }

    @Override
    public Optional<CifAtom> getAtom(Integer atomIdentifier) {
        for (CifChain chain : chains.values()) {
            final Collection<CifLeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (CifLeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<CifAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                if (optionalAtom.isPresent()) {
                    return optionalAtom;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (CifChain chain : chains.values()) {
            final Collection<CifLeafSubstructure> allLeafSubstructures = chain.getAllLeafSubstructures();
            for (CifLeafSubstructure leafSubstructure : allLeafSubstructures) {
                final Optional<CifAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
                optionalAtom.ifPresent(atom -> leafSubstructure.removeAtom(atomIdentifier));
            }
        }
    }

    @Override
    public Collection<CifChain> getAllChains() {
        return chains.values();
    }

    public void addChain(CifChain chain) {
        chains.put(chain.getChainIdentifier(), chain);
    }

    @Override
    public Chain getFirstChain() {
        return chains.firstEntry().getValue();
    }

    @Override
    public Collection<CifLeafSubstructure> getAllLeafSubstructures() {
        List<CifLeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (CifChain chain : chains.values()) {
            final Collection<CifLeafSubstructure> leafSubstructures = chain.getAllLeafSubstructures();
            allLeafSubstructures.addAll(leafSubstructures);
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<CifLeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        for (CifChain chain : chains.values()) {
            final Optional<CifLeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
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
        for (CifChain chain : chains.values()) {
            final Optional<CifLeafSubstructure> optionalLeafSubstructure = chain.getLeafSubstructure(leafIdentifier);
            if (optionalLeafSubstructure.isPresent()) {
                chain.removeLeafSubstructure(optionalLeafSubstructure.get().getIdentifier());
                return true;
            }
        }
        return false;
    }

    @Override
    public int getModelIdentifier() {
        return identifier;
    }

    @Override
    public Set<String> getAllChainIdentifiers() {
        return chains.keySet();
    }

    @Override
    public Optional<CifChain> getChain(String chainIdentifier) {
        if (chains.containsKey(chainIdentifier)) {
            return Optional.of(chains.get(chainIdentifier));
        }
        return Optional.empty();
    }

    @Override
    public void removeChain(String chainIdentifier) {
        chains.remove(chainIdentifier);
    }

    @Override
    public CifModel getCopy() {
        return new CifModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifModel cifModel = (CifModel) o;

        return identifier != null ? identifier.equals(cifModel.identifier) : cifModel.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }
}
