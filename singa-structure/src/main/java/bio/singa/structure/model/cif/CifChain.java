package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafIdentifier;

import java.util.Collection;
import java.util.Optional;
import java.util.TreeMap;

public class CifChain implements Chain {

    private String identifier;
    private String legacyIdentifier;

    private final TreeMap<CifLeafIdentifier, CifLeafSubstructure> leafSubstructures;

    public CifChain(String chainIdentifier) {
        identifier = chainIdentifier;
        leafSubstructures = new TreeMap<>();
    }

    public CifChain(CifChain cifChain) {
        identifier = cifChain.identifier;
        leafSubstructures = new TreeMap<>();
        for (CifLeafSubstructure leafSubstructure : cifChain.leafSubstructures.values()) {
            leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure.getCopy());
        }
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLegacyIdentifier() {
        return legacyIdentifier;
    }

    public void setLegacyIdentifier(String legacyIdentifier) {
        this.legacyIdentifier = legacyIdentifier;
    }

    @Override
    public Optional<CifAtom> getAtom(Integer atomIdentifier) {
        for (CifLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<CifAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (CifLeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<CifAtom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                leafSubstructure.removeAtom(optionalAtom.get().getAtomIdentifier());
                return;
            }
        }
    }

    @Override
    public String getChainIdentifier() {
        return identifier;
    }

    @Override
    public CifChain getCopy() {
        return new CifChain(this);
    }

    public void addLeafSubstructure(CifLeafSubstructure leafSubstructure) {
        leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure);
    }

    @Override
    public Collection<CifLeafSubstructure> getAllLeafSubstructures() {
        return leafSubstructures.values();
    }

    @Override
    public Optional<CifLeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (leafSubstructures.containsKey(leafIdentifier)) {
            return Optional.of(leafSubstructures.get(leafIdentifier));
        }
        return Optional.empty();
    }

    @Override
    public CifLeafSubstructure getFirstLeafSubstructure() {
        return leafSubstructures.values().iterator().next();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        if (leafSubstructures.containsKey(leafIdentifier)) {
            leafSubstructures.remove(leafIdentifier);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifChain cifChain = (CifChain) o;

        return identifier != null ? identifier.equals(cifChain.identifier) : cifChain.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

}
