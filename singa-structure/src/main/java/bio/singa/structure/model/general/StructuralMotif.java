package bio.singa.structure.model.general;

import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamilies.Matchers;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  Exchangeable defines an entity that is exchangeable in terms of mapping another label or a set of other labels to
 *  that entity. <p> <b>This constitutes a surjective mapping of {@link StructuralFamily}s.</b> <p> <b>By default an
 *  entity is always exchangeable with itself.</b>
 *
 * @author cl
 */
public class StructuralMotif implements LeafSubstructureContainer {

    private final String identifier;

    /**
     * The leaves contained in this motif.
     */
    public final LinkedHashMap<LeafIdentifier, LeafSubstructure> leafSubstructures;

    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    private final HashMap<LeafSubstructure, Set<StructuralFamily>> exchangeableFamilies;

    private StructuralMotif(String identifier) {
        this.identifier = identifier;
        leafSubstructures = new LinkedHashMap<>();
        exchangeableFamilies = new HashMap<>();
    }

    private StructuralMotif(String identifier, Collection<? extends LeafSubstructure> leafSubstructures) {
        this(identifier);
        for (LeafSubstructure leafSubstructure : leafSubstructures) {
            addLeafSubstructure(leafSubstructure);
        }
    }

    private StructuralMotif(StructuralMotif structuralMotif) {
        this(structuralMotif.identifier);
        for (LeafSubstructure leafSubstructure : structuralMotif.leafSubstructures.values()) {
            addLeafSubstructure(leafSubstructure);
        }
        for (Map.Entry<LeafSubstructure, Set<StructuralFamily>> entry : structuralMotif.exchangeableFamilies.entrySet()) {
            for (StructuralFamily structuralFamily : entry.getValue()) {
                addExchangeableFamily(entry.getKey(), structuralFamily);
            }
        }
    }

    /**
     * Creates a {@link StructuralMotif} by extracting the given residues identified by a list of {@link
     * LeafIdentifier}s.
     *
     * @param structure The {@link Structure} from which the {@link StructuralMotif} should be extracted.
     * @param leafIdentifiers The {@link LeafIdentifier}s of the residues that should compose the {@link
     * StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafIdentifiers(Structure structure, List<PdbLeafIdentifier> leafIdentifiers) {
        List<LeafSubstructure> leafSubstructures = new ArrayList<>();
        for (PdbLeafIdentifier leafIdentifier : leafIdentifiers) {
            leafIdentifier = new PdbLeafIdentifier(structure.getPdbIdentifier(), leafIdentifier.getModelIdentifier(), leafIdentifier.getChainIdentifier(), leafIdentifier.getSerial(), leafIdentifier.getInsertionCode());
            final Optional<? extends LeafSubstructure> leafSubstructure = structure.getLeafSubstructure(leafIdentifier);
            if (leafSubstructure.isPresent()) {
                leafSubstructures.add(leafSubstructure.get());
            } else {
                throw new NoSuchElementException("Unable to add leaf substructure with identifier " + leafIdentifier + " from structure " + structure.getPdbIdentifier());
            }
        }
        return new StructuralMotif(generateMotifIdentifier(leafSubstructures), leafSubstructures);
    }

    /**
     * Forms a {@link StructuralMotif} out of the given {@link LeafSubstructure}s.
     *
     * @param leafSubstructures The {@link LeafSubstructure}s that should compose the {@link StructuralMotif}.
     * @return A new {@link StructuralMotif}.
     */
    public static StructuralMotif fromLeafSubstructures(Collection<? extends LeafSubstructure> leafSubstructures) {
        return new StructuralMotif(generateMotifIdentifier(leafSubstructures), leafSubstructures);
    }

    private static String generateMotifIdentifier(Collection<? extends LeafSubstructure> substructures) {
        String pdbIdentifier = substructures.iterator().next().getIdentifier().getStructureIdentifier();
        return substructures.stream()
                .map(leafSubstructure -> leafSubstructure.getIdentifier().toString())
                .collect(Collectors.joining("_", pdbIdentifier + "_", ""));
    }

    @Override
    public List<LeafSubstructure> getAllLeafSubstructures() {
        return new LinkedList<>(leafSubstructures.values());
    }

    @Override
    public Optional<LeafSubstructure> getLeafSubstructure(LeafIdentifier leafIdentifier) {
        final LeafSubstructure leafSubstructure = leafSubstructures.get(leafIdentifier);
        if (leafSubstructure != null) {
            return Optional.of(leafSubstructure);
        }
        return Optional.empty();
    }

    @Override
    public LeafSubstructure getFirstLeafSubstructure() {
        return leafSubstructures.values().iterator().next();
    }

    @Override
    public boolean removeLeafSubstructure(LeafIdentifier leafIdentifier) {
        final LeafSubstructure leafSubstructure = leafSubstructures.remove(leafIdentifier);
        return leafSubstructure != null;
    }


    public void addLeafSubstructure(LeafSubstructure leafSubstructure) {
        leafSubstructures.put(leafSubstructure.getIdentifier(), leafSubstructure);
    }


    /**
     * Returns the size of the structural motif (the number of contained {@link LeafSubstructure}s).
     *
     * @return The size of the motif.
     */
    public int size() {
        return leafSubstructures.size();
    }

    @Override
    public Optional<? extends Atom> getAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<? extends Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                return optionalAtom;
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        for (LeafSubstructure leafSubstructure : leafSubstructures.values()) {
            final Optional<? extends Atom> optionalAtom = leafSubstructure.getAtom(atomIdentifier);
            if (optionalAtom.isPresent()) {
                leafSubstructure.removeAtom(atomIdentifier);
                return;
            }
        }
    }

    public void addExchangeableFamily(LeafIdentifier leafIdentifier, StructuralFamily structuralFamily) {
        if (StructuralFamilies.AminoAcids.isAminoAcid(structuralFamily)) {
            LeafSubstructure leafSubstructure = leafSubstructures.values().stream()
                    .filter(StructuralEntityFilter.LeafFilter.isAminoAcid())
                    .filter(aminoAcid -> aminoAcid.getIdentifier().getChainIdentifier().equals(leafIdentifier.getChainIdentifier()))
                    .filter(aminoAcid -> aminoAcid.getIdentifier().getSerial() == leafIdentifier.getSerial())
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);
            addExchangeableFamily(leafSubstructure, structuralFamily);
        } else if (StructuralFamilies.Nucleotides.isNucleotide(structuralFamily)) {
            LeafSubstructure leafSubstructure = leafSubstructures.values().stream()
                    .filter(StructuralEntityFilter.LeafFilter.isNucleotide())
                    .filter(aminoAcid -> aminoAcid.getIdentifier().getChainIdentifier().equals(leafIdentifier.getChainIdentifier()))
                    .filter(aminoAcid -> aminoAcid.getIdentifier().getSerial() == leafIdentifier.getSerial())
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            addExchangeableFamily(leafSubstructure, structuralFamily);
        } else if (StructuralFamilies.Matchers.isMatcher(structuralFamily)) {
            Matchers.getMatcherEntities(structuralFamily)
                    .forEach(matcherFamilyMember -> addExchangeableFamily(leafIdentifier, matcherFamilyMember));
        }
    }

    public void addExchangeableFamilies(LeafIdentifier leafIdentifier, Set<StructuralFamily> structuralFamily) {
        structuralFamily.forEach(matcherFamilyMember -> addExchangeableFamily(leafIdentifier, matcherFamilyMember));
    }

    public void addExchangeableFamilyToAll(StructuralFamily aminoAcidFamily) {
        leafSubstructures.values().stream()
                .filter(StructuralEntityFilter.LeafFilter.isAminoAcid())
                .map(AminoAcid.class::cast)
                .forEach(exchangeable -> addExchangeableFamily(exchangeable, aminoAcidFamily));

    }

    /**
     * Returns the {@link StructuralFamily}s to which this entity is exchangeable. By default an entity is always
     * exchangeable to the same {@link StructuralFamily}.
     *
     * @return a set of exchangeable types
     */
    public Set<StructuralFamily> getExchangeableFamilies(LeafSubstructure leafSubstructure) {
        if (!exchangeableFamilies.containsKey(leafSubstructure)) {
            return Collections.emptySet();
        }
        return exchangeableFamilies.get(leafSubstructure);
    }

    /**
     * Returns all {@link StructuralFamily} types that are defined, this is the concrete type of the entity itself
     * <b>plus</b> all exchangeable types.
     *
     * @return a set of containing types (own type + exchangeable types)
     */
    public Set<StructuralFamily> getContainingFamilies(LeafSubstructure leafSubstructure) {
        Set<StructuralFamily> types = new HashSet<>();
        types.add(leafSubstructure.getFamily());
        types.addAll(getExchangeableFamilies(leafSubstructure));
        return types;
    }

    /**
     * Adds an exchangeable {@link StructuralFamily}.
     *
     * @param exchangeableFamily the {@link StructuralFamily} to be added
     */
    public void addExchangeableFamily(LeafSubstructure leafSubstructure, StructuralFamily exchangeableFamily) {
        if (!exchangeableFamilies.containsKey(leafSubstructure)) {
            exchangeableFamilies.put(leafSubstructure, new HashSet<>());
        }
        getExchangeableFamilies(leafSubstructure).add(exchangeableFamily);
    }

    @Override
    public String toString() {
        return generateMotifIdentifier(getAllLeafSubstructures());
    }

    @Override
    public StructuralMotif getCopy() {
        return new StructuralMotif(this);
    }

    /**
     * The type of a {@link StructuralMotif}, representing inter (across multiple protein chains) and intra
     * (within one protein chain) {@link StructuralMotif}s.
     */
    public enum Type {

        INTRA("intra"), INTER("inter");

        private final String description;

        Type(String name) {
            description = name;
        }

        public static Type determine(StructuralMotif structuralMotif) {
            return structuralMotif.getAllLeafSubstructures().stream()
                    .map(leafSubstructure -> leafSubstructure.getIdentifier().getChainIdentifier())
                    .distinct()
                    .count() == 1 ? INTRA : INTER;
        }

        public String getDescription() {
            return description;
        }
    }
}
