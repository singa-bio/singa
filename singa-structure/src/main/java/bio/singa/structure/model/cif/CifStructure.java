package bio.singa.structure.model.cif;

import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.*;

import java.util.*;
import java.util.stream.Collectors;

public class CifStructure implements Structure {

    /**
     * The branches this structure contains.
     */
    private final TreeMap<Integer, CifModel> models;

    private final TreeMap<Integer, CifEntity> entities;

    /**
     * The PDB identifier of the structure.
     */
    private String structureIdentifier;

    /**
     * The title of the structure.
     */
    private String title;

    private double resolution;

    public CifStructure(String structureIdentifier) {
        this.structureIdentifier = structureIdentifier;
        models = new TreeMap<>();
        entities = new TreeMap<>();
    }

    public CifStructure(CifStructure structure) {
        structureIdentifier = structure.getStructureIdentifier();
        title = structure.title;
        resolution = structure.resolution;
        models = new TreeMap<>();
        for (CifModel model : structure.models.values()) {
            models.put(model.getModelIdentifier(), model.getCopy());
        }
        // todo fill in
        entities = new TreeMap<>();
    }

    @Override
    public Optional<CifAtom> getAtom(Integer atomIdentifier) {
        for (CifLeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<CifAtom> atom = leafSubstructure.getAtom(atomIdentifier);
            if (atom.isPresent()) {
                return atom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (CifLeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<CifAtom> atom = leafSubstructure.getAtom(atomIdentifier);
            if (atom.isPresent()) {
                leafSubstructure.removeAtom(atomIdentifier);
                return;
            }
        }
    }

    @Override
    public Collection<CifChain> getAllChains() {
        List<CifChain> allChains = new ArrayList<>();
        for (CifModel model : models.values()) {
            allChains.addAll(model.getAllChains());
        }
        return allChains;
    }

    @Override
    public Chain getFirstChain() {
        return getFirstModel().getFirstChain();
    }

    public Collection<CifEntity> getAllNonPolymerEntities() {
        return entities.values().stream()
                .filter(cifEntity -> cifEntity.getCifEntityType().equals(CifEntityType.NON_POLYMER))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<CifLeafSubstructure> getAllLeafSubstructures() {
        List<CifLeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (CifModel model : models.values()) {
            allLeafSubstructures.addAll(model.getAllLeafSubstructures());
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<CifLeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final Optional<CifChain> chainOptional = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        return chainOptional.flatMap(chain -> chain.getLeafSubstructure(leafIdentifier));
    }

    @Override
    public LeafSubstructure getFirstLeafSubstructure() {
        return getFirstModel().getFirstChain().getFirstLeafSubstructure();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final Optional<CifChain> chain = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        if (chain.isPresent()) {
            if (chain.get().getLeafSubstructure(leafIdentifier).isPresent()) {
                chain.get().removeLeafSubstructure(leafIdentifier);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStructureIdentifier() {
        return structureIdentifier;
    }

    public void setPdbIdentifier(String pdbIdentifier) {
        if (pdbIdentifier.isEmpty()) {
            pdbIdentifier = PdbLeafIdentifier.DEFAULT_PDB_IDENTIFIER;
        }
        structureIdentifier = pdbIdentifier;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addModel(CifModel model) {
        models.put(model.getModelIdentifier(), model);
    }

    @Override
    public Collection<CifModel> getAllModels() {
        return models.values();
    }

    @Override
    public Set<Integer> getAllModelIdentifiers() {
        return models.keySet();
    }

    @Override
    public CifModel getFirstModel() {
        return models.firstEntry().getValue();
    }

    @Override
    public Optional<CifModel> getModel(int modelIdentifier) {
        if (models.containsKey(modelIdentifier)) {
            return Optional.of(models.get(modelIdentifier));
        }
        return Optional.empty();
    }

    public Optional<CifEntity> getEntity(int entityIdentifier) {
        if (entities.containsKey(entityIdentifier)) {
            return Optional.of(entities.get(entityIdentifier));
        }
        return Optional.empty();
    }

    public void addEntity(CifEntity entity) {
        entities.put(entity.getEntityIdentifier(), entity);
    }

    @Override
    public void removeModel(int modelIdentifier) {
        models.remove(modelIdentifier);
    }

    @Override
    public Optional<CifChain> getChain(int modelIdentifier, String chainIdentifier) {
        final Optional<CifModel> optionalModel = getModel(modelIdentifier);
        return optionalModel.flatMap(model -> model.getChain(chainIdentifier));
    }

    @Override
    public Optional<CifAtom> getAtom(UniqueAtomIdentifier atomIdentifier) {
            // does actually not compare pdb id
            return getChain(atomIdentifier.getLeafIdentifier().getModelIdentifier(), atomIdentifier.getLeafIdentifier().getChainIdentifier())
                    .flatMap(chain -> chain.getLeafSubstructure(atomIdentifier.getLeafIdentifier()))
                    .flatMap(leafSubstructure -> leafSubstructure.getAtom(atomIdentifier.getAtomSerial()));
    }

    @Override
    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    @Override
    public Structure getCopy() {
        return new CifStructure(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifStructure that = (CifStructure) o;

        return structureIdentifier != null ? structureIdentifier.equals(that.structureIdentifier) : that.structureIdentifier == null;
    }

    @Override
    public int hashCode() {
        return structureIdentifier != null ? structureIdentifier.hashCode() : 0;
    }
}
