package de.bioforscher.singa.structure.model.graph.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exchangeable defines {@link StructuralEntity}s that exchangeable in terms of mapping another label or a set of
 * other labels to that structural entity.
 * <p>
 * <b>This constitutes a surjective mapping of {@link StructuralFamily}s.</b>
 * <p>
 * <b>By default a {@link StructuralEntity} type is always exchangeable with itself.</b>
 *
 * @author fk
 */
public interface Exchangeable<FamilyType extends StructuralFamily> {

    /**
     * Returns the {@link StructuralFamily} of the {@link StructuralEntity}.
     *
     * @return the {@link StructuralFamily}
     */
    FamilyType getFamily();

    /**
     * Returns the {@link StructuralFamily}s to which this {@link StructuralEntity} is exchangeable.
     * By default a {@link StructuralEntity} is always exchangeable to the same {@link StructuralFamily}.
     *
     * @return a set of exchangeable types
     */
    default Set<FamilyType> getExchangeableFamilies() {
        return Stream.of(getFamily()).collect(Collectors.toSet());
    }

    /**
     * Returns all {@link StructuralFamily} types that are defined, this is the concrete type of the
     * {@link Exchangeable} itself <b>plus</b> all exchangeable types.
     *
     * @return a set of containing types (own type + exchangeable types)
     */
    default Set<FamilyType> getContainingFamilies() {
        Set<FamilyType> types = new HashSet<>();
        types.add(getFamily());
        types.addAll(getExchangeableFamilies());
        return types;
    }

    /**
     * Adds an exchangeable {@link StructuralFamily}.
     *
     * @param exchangeableFamily the {@link StructuralFamily} to be added
     */
    default void addExchangeableFamily(FamilyType exchangeableFamily) {
        getExchangeableFamilies().add(exchangeableFamily);
    }
}
