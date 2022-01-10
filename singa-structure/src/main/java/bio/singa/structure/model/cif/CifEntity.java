package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

public class CifEntity {

    private int entityIdentifier;

    private String name;

    private CifEntityType cifEntityType;

    private final Set<String> chains;

    public CifEntity(int identifier) {
        entityIdentifier = identifier;
        chains = new HashSet<>();
    }

    public CifEntity(CifEntity cifEntity) {
        entityIdentifier = cifEntity.entityIdentifier;
        name = cifEntity.getName();
        cifEntityType = cifEntity.cifEntityType;
        chains = new HashSet<>(cifEntity.chains);
    }

    public int getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(int entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public CifEntityType getCifEntityType() {
        return cifEntityType;
    }

    void setCifEntityType(CifEntityType cifEntityType) {
        this.cifEntityType = cifEntityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getAllChainIdentifiers() {
        return chains;
    }

    public Collection<CifChain> getAllRelevantChainsFrom(CifModel model) {
        return chains.stream()
                .map(model::getChain)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void addChain(CifChain chain) {
        chains.add(chain.getChainIdentifier());
    }

    public CifEntity getCopy() {
        return new CifEntity(this);
    }

}
