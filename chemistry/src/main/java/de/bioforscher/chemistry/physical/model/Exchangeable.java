package de.bioforscher.chemistry.physical.model;

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
public interface Exchangeable<R extends StructuralFamily> {

    /**
     * Returns the {@link StructuralFamily} of the {@link StructuralEntity}.
     *
     * @return the {@link StructuralFamily
     */
    R getFamily();

    /**
     * Returns the {@link StructuralFamily}s to which this {@link StructuralEntity} is exchangeable.
     * By default a {@link StructuralEntity} is always exchangeable to the same {@link StructuralFamily}.
     *
     * @return a set of exchangeable types
     */
    default Set<R> getExchangeableTypes() {
        return Stream.of(getFamily()).collect(Collectors.toSet());
    }

    /**
     * Returns all {@link StructuralFamily} types that are defined, this is the concrete type of the
     * {@link Exchangeable} itself <b>plus</b> all echangable types.
     *
     * @return a set of containing types (own type + exchangeable types)
     */
    default Set<R> getContainingTypes(){
        Set<R> types = new HashSet<>();
        types.add(getFamily());
        types.addAll(getExchangeableTypes());
        return types;
    }

    /**
     * Adds an exchangeable {@link StructuralFamily}.
     *
     * @param exchangeableType the {@link StructuralFamily} to be added
     */
    default void addExchangeableType(R exchangeableType) {
        getExchangeableTypes().add(exchangeableType);
    }
}
