package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.units.UnitProvider;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;

/**
 * @author cl
 */
public class MultiConcentrationContainer implements ConcentrationContainer {

    private Set<ChemicalEntity> referencedEntities;
    private Map<CellSection, Map<ChemicalEntity, Quantity<MolarConcentration>>> concentrations;

    public MultiConcentrationContainer(CellSection cellSection) {
        this.referencedEntities = new HashSet<>();
        this.concentrations = new HashMap<>();
        this.concentrations.put(cellSection, new HashMap<>());
    }

    public MultiConcentrationContainer(Set<CellSection> cellSections) {
        this.referencedEntities = new HashSet<>();
        this.concentrations = new HashMap<>();
        cellSections.forEach(compartment -> this.concentrations.put(compartment, new HashMap<>()));
    }

    public MultiConcentrationContainer(MultiConcentrationContainer multiConcentrationContainer) {
        this.referencedEntities = multiConcentrationContainer.referencedEntities;
        this.concentrations = new HashMap<>();
        Set<CellSection> cellSections = multiConcentrationContainer.concentrations.keySet();
        cellSections.forEach(compartment -> this.concentrations.put(compartment, new HashMap<>()));
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity chemicalEntity) {
        // FIXME this always assumes mol/l
        return Quantities.getQuantity(this.concentrations.keySet().stream()
                .mapToDouble(identifier -> getAvailableConcentration(identifier, chemicalEntity).getValue().doubleValue())
                .average().orElse(0.0), UnitProvider.MOLE_PER_LITRE);
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrationsForSection(CellSection cellSection) {
        if (concentrations.containsKey(cellSection)) {
            return concentrations.get(cellSection);
        }
        return Collections.emptyMap();
    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity) {
        if (!this.concentrations.containsKey(cellSection)) {
            System.out.println();
        }

        return this.concentrations.get(cellSection).get(chemicalEntity);
    }

    @Override
    public void setConcentration(ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        this.concentrations.keySet().forEach(compartment -> setAvailableConcentration(compartment, chemicalEntity, concentration));
    }

    @Override
    public void setAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        this.concentrations.get(cellSection).put(chemicalEntity, concentration);
        this.referencedEntities.add(chemicalEntity);
    }

    @Override
    public Set<ChemicalEntity> getAllReferencedEntities() {
        return this.referencedEntities;
    }

    @Override
    public Set<CellSection> getAllReferencedSections() {
        return this.concentrations.keySet();
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        Map<ChemicalEntity, Quantity<MolarConcentration>> result = new HashMap<>();
        for (ChemicalEntity chemicalEntity : this.referencedEntities) {
            result.put(chemicalEntity, getConcentration(chemicalEntity));
        }
        return result;
    }

    @Override
    public MultiConcentrationContainer copy() {
        return new MultiConcentrationContainer(this);
    }
}
