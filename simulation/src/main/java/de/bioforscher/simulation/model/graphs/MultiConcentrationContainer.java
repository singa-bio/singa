package de.bioforscher.simulation.model.graphs;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.units.UnitProvider;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author cl
 */
public class MultiConcentrationContainer implements ConcentrationContainer {

    private Set<ChemicalEntity> referencedEntities;
    private Map<String, Map<ChemicalEntity, Quantity<MolarConcentration>>> concentrations;

    public MultiConcentrationContainer(String defaultCompartment) {
        this.referencedEntities = new HashSet<>();
        this.concentrations = new HashMap<>();
        this.concentrations.put(defaultCompartment, new HashMap<>());
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity chemicalEntity) {
        // FIXME this always assumes mol/l
        return Quantities.getQuantity(this.concentrations.keySet().stream()
                .mapToDouble(identifier -> getAvailableConcentration(identifier, chemicalEntity).getValue().doubleValue())
                .average().orElse(0.0), UnitProvider.MOLE_PER_LITRE);

    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(String compartmentIdentifier, ChemicalEntity chemicalEntity) {
        if (this.concentrations.containsKey(compartmentIdentifier) &&
                this.concentrations.get(compartmentIdentifier).containsKey(chemicalEntity)) {
            return this.concentrations.get(compartmentIdentifier).get(chemicalEntity);
        }
        // FIXME this always assumes mol/l
        return Quantities.getQuantity(0.0, UnitProvider.MOLE_PER_LITRE);
    }

    @Override
    public void setConcentration(ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        this.concentrations.keySet().forEach(identifier -> setAvailableConcentration(identifier, chemicalEntity, concentration));
    }

    @Override
    public void setAvailableConcentration(String compartmentIdentifier, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        this.referencedEntities.add(chemicalEntity);
        if (this.concentrations.containsKey(compartmentIdentifier)) {
            this.concentrations.get(compartmentIdentifier).put(chemicalEntity, concentration);
        } else {
            Map<ChemicalEntity, Quantity<MolarConcentration>> concentrationMap = new HashMap<>();
            concentrationMap.put(chemicalEntity, concentration);
            this.concentrations.put(compartmentIdentifier, concentrationMap);
        }
    }

    @Override
    public Set<ChemicalEntity> getAllReferencedEntities() {
        return this.referencedEntities;
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        Map<ChemicalEntity, Quantity<MolarConcentration>> result = new HashMap<>();
        for (ChemicalEntity chemicalEntity : this.referencedEntities) {
            result.put(chemicalEntity, getConcentration(chemicalEntity));
        }
        return result;
    }
}
