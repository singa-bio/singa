package de.bioforscher.chemistry.physical.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exchangeable defines {@link StructuralEntity}s that exchangeable in terms of mapping another label or a set of
 * other labels to that structural entity.
 * <p>
 * <b>This constitutes a surjective mapping of {@link StructuralEntityType}s.</b>
 * <p>
 * By default a {@link StructuralEntity} type is always exchangeable with itself.
 *
 * @author fk
 */
public interface Exchangeable<R extends StructuralEntityType> {

    R getType();

    default Set<R> getExchangeableTypes() {
        return Stream.of(getType()).collect(Collectors.toSet());
    }
}
