package de.bioforscher.simulation.modules.model.updates;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.units.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public final class PotentialUpdates {

    private PotentialUpdates() {

    }

    /**
     * Converts a Map with {@link ChemicalEntity}, concentration pairs to a set of {@link PotentialUpdates}.
     *
     * @param concentrations The Map to convert
     * @return The resulting set.
     */
    public static Set<PotentialUpdate> collectAsPotentialUpdates(Map<ChemicalEntity, Quantity<MolarConcentration>>
                                                                         concentrations) {
        return concentrations.entrySet().stream()
                .map(mapEntity -> new PotentialUpdate(mapEntity.getKey(), mapEntity.getValue()))
                .collect(Collectors.toSet());
    }

}
