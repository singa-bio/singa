package bio.singa.structure.model.pdb;

import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;

import java.util.*;

/**
 * @author cl
 */
public class PdbStructure implements Structure {

    /**
     * The branches this structure contains.
     */
    private final TreeMap<Integer, PdbModel> models;

    /**
     * The PDB identifier of the structure.
     */
    private String pdbIdentifier;

    /**
     * The title of the structure.
     */
    private String title;

    private int lastAddedAtomIdentifier;

    private List<PdbLinkEntry> linkEntries;

    private Map<String, List<String>> biologicalAssemblies;

    private double resolution;

    public PdbStructure() {
        models = new TreeMap<>();
        linkEntries = new ArrayList<>();
        biologicalAssemblies = new HashMap<>();
    }

    public PdbStructure(PdbStructure structure) {
        pdbIdentifier = structure.getPdbIdentifier();
        title = structure.title;
        resolution = structure.resolution;
        models = new TreeMap<>();
        lastAddedAtomIdentifier = structure.lastAddedAtomIdentifier;
        for (PdbModel model : structure.models.values()) {
            models.put(model.getModelIdentifier(), model.getCopy());
        }
        // TODO this is only a shallow copy
        linkEntries = new ArrayList<>(structure.linkEntries);
        biologicalAssemblies = new HashMap<>(structure.biologicalAssemblies);
    }

    @Override
    public String getPdbIdentifier() {
        return pdbIdentifier;
    }

    public void setPdbIdentifier(String pdbIdentifier) {
        if (pdbIdentifier.isEmpty()) {
            pdbIdentifier = PdbLeafIdentifier.DEFAULT_PDB_IDENTIFIER;
        }
        this.pdbIdentifier = pdbIdentifier.toLowerCase();
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Collection<PdbModel> getAllModels() {
        return models.values();
    }

    @Override
    public Set<Integer> getAllModelIdentifiers() {
        return new HashSet<>(models.keySet());
    }

    @Override
    public PdbModel getFirstModel() {
        return models.firstEntry().getValue();
    }

    @Override
    public Optional<Model> getModel(int modelIdentifier) {
        if (models.containsKey(modelIdentifier)) {
            return Optional.of(models.get(modelIdentifier));
        }
        return Optional.empty();
    }

    @Override
    public void removeModel(int modelIdentifier) {
        models.remove(modelIdentifier);
    }

    public void addModel(PdbModel model) {
        models.put(model.getModelIdentifier(), model);
    }

    @Override
    public Optional<Chain> getChain(int modelIdentifier, String chainIdentifier) {
        final Optional<Model> optionalModel = getModel(modelIdentifier);
        return optionalModel.flatMap(model -> model.getChain(chainIdentifier));
    }

    @Override
    public List<Chain> getAllChains() {
        List<Chain> allChains = new ArrayList<>();
        for (PdbModel model : models.values()) {
            allChains.addAll(model.getAllChains());
        }
        return allChains;
    }

    @Override
    public PdbChain getFirstChain() {
        return getFirstModel().getFirstChain();
    }

    @Override
    public List<PdbLeafSubstructure> getAllLeafSubstructures() {
        List<PdbLeafSubstructure> allLeafSubstructures = new ArrayList<>();
        for (PdbModel model : models.values()) {
            allLeafSubstructures.addAll(model.getAllLeafSubstructures());
        }
        return allLeafSubstructures;
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final Optional<Chain> chainOptional = getChain(leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier());
        return chainOptional.flatMap(chain -> chain.getLeafSubstructure(leafIdentifier));
    }

    @Override
    public LeafSubstructure getFirstLeafSubstructure() {
        return getFirstModel().getFirstChain().getFirstLeafSubstructure();
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
    public Optional<PdbAtom> getAtom(Integer atomIdentifier) {
        for (PdbLeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<PdbAtom> atom = leafSubstructure.getAtom(atomIdentifier);
            if (atom.isPresent()) {
                return atom;
            }
        }
        return Optional.empty();
    }

    public Optional<Map.Entry<UniqueAtomIdentifier, PdbAtom>> getUniqueAtomEntry(int atomSerial) {
        for (PdbModel model : getAllModels()) {
            for (PdbChain chain : model.getAllChains()) {
                for (PdbLeafSubstructure leafSubstructure : chain.getAllLeafSubstructures()) {
                    for (PdbAtom atom : leafSubstructure.getAllAtoms()) {
                        if (atom.getAtomIdentifier() == atomSerial) {
                            UniqueAtomIdentifier identifier = new UniqueAtomIdentifier(leafSubstructure.getIdentifier(), atom.getAtomIdentifier());
                            return Optional.of(new AbstractMap.SimpleEntry<>(identifier, atom));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Map.Entry<UniqueAtomIdentifier, Atom>> getAtomByCoordinate(Vector3D coordinate, double eps) {
        for (Model model : getAllModels()) {
            for (Chain chain : model.getAllChains()) {
                for (LeafSubstructure leafSubstructure : chain.getAllLeafSubstructures()) {
                    for (Atom atom : leafSubstructure.getAllAtoms()) {
                        if (atom.getPosition().almostEqual(coordinate, eps)) {
                            UniqueAtomIdentifier identifier = new UniqueAtomIdentifier(leafSubstructure.getIdentifier(), atom.getAtomIdentifier());
                            return Optional.of(new AbstractMap.SimpleEntry<>(identifier, atom));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }


    /**
     * Adds an {@link Atom} to the {@link Structure}
     * FIXME: atom serial overflow may happen (if exceeds 9999)
     *
     * @param chainIdentifier The identifier of the {@link Chain} to which it should be added.
     * @param threeLetterCode The three-letter code of the associated {@link LeafSubstructure}.
     * @param position The position of the {@link Atom}.
     */
    public void addAtom(String chainIdentifier, String threeLetterCode, Vector3D position) {
        Optional<PdbChain> optionalChain = getFirstModel().getChain(chainIdentifier);
        if (optionalChain.isPresent()) {
            PdbChain chain = (PdbChain) optionalChain.get();
            PdbLigand leafSubstructure = new PdbLigand(chain.getNextLeafIdentifier(), new StructuralFamily("?", threeLetterCode));
            lastAddedAtomIdentifier++;
            leafSubstructure.addAtom(new PdbAtom(lastAddedAtomIdentifier, ElementProvider.UNKOWN, "CA", position));
            chain.addLeafSubstructure(leafSubstructure);
        } else {
            throw new IllegalStateException("Unable to add atom to chain " + chainIdentifier + ", chain could not be found.");
        }
    }

    @Override
    public Optional<Atom> getAtom(UniqueAtomIdentifier atomIdentifier) {
        // does actually not compare pdb id
        return getChain(atomIdentifier.getLeafIdentifier().getModelIdentifier(), atomIdentifier.getLeafIdentifier().getChainIdentifier())
                .flatMap(chain -> chain.getLeafSubstructure(atomIdentifier.getLeafIdentifier()))
                .flatMap(leafSubstructure -> leafSubstructure.getAtom(atomIdentifier.getAtomSerial()));
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (PdbLeafSubstructure leafSubstructure : getAllLeafSubstructures()) {
            final Optional<PdbAtom> atom = leafSubstructure.getAtom(atomIdentifier);
            if (atom.isPresent()) {
                leafSubstructure.removeAtom(atomIdentifier);
                return;
            }
        }
    }

    public void addLinkEntry(PdbLinkEntry linkEntry) {
        linkEntries.add(linkEntry);
    }

    public List<PdbLinkEntry> getLinkEntries() {
        return linkEntries;
    }

    public int getLastAddedAtomIdentifier() {
        return lastAddedAtomIdentifier;
    }

    public void setLastAddedAtomIdentifier(int lastAddedAtomIdentifier) {
        this.lastAddedAtomIdentifier = lastAddedAtomIdentifier;
    }

    public Map<String, List<String>> getBiologicalAssemblies() {
        return biologicalAssemblies;
    }

    public void setBiologicalAssemblies(Map<String, List<String>> biologicalAssemblies) {
        this.biologicalAssemblies = biologicalAssemblies;
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
        return new PdbStructure(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdbStructure that = (PdbStructure) o;

        return pdbIdentifier != null ? pdbIdentifier.equals(that.pdbIdentifier) : that.pdbIdentifier == null;
    }

    @Override
    public int hashCode() {
        return pdbIdentifier != null ? pdbIdentifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
