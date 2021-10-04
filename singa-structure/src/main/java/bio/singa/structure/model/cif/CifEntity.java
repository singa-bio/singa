package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.*;

import java.util.*;

public class CifEntity implements LeafSubstructureContainer, ChainContainer {

    private int entityIdentifier;

    private String name;

    private final TreeMap<String, CifChain> chains;

    public CifEntity(int identifier) {
        entityIdentifier = identifier;
        chains = new TreeMap<>();
    }

    public CifEntity(CifEntity cifEntity) {
        entityIdentifier = cifEntity.entityIdentifier;
        chains = new TreeMap<>();
        for (CifChain chain : cifEntity.chains.values()) {
            chains.put(chain.getChainIdentifier(), chain.getCopy());
        }
    }

    public int getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(int entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public CifEntity getCopy() {
        return new CifEntity(this);
    }
}
