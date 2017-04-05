package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.units.quantities.MolarConcentration;

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
