package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;

import javax.measure.Quantity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cl
 */
public class SimpleConcentrationContainer implements ConcentrationContainer {

    private final Map<ChemicalEntity<?>, Quantity<MolarConcentration>> concentrations;
    private final CellSection cellSection;

    public SimpleConcentrationContainer(CellSection cellSection) {
        concentrations = new HashMap<>();
        this.cellSection = cellSection;
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity chemicalEntity) {
        if (concentrations.containsKey(chemicalEntity)) {
            return concentrations.get(chemicalEntity);
        }
        concentrations.put(chemicalEntity, EnvironmentalParameters.emptyConcentration());
        return EnvironmentalParameters.emptyConcentration();
    }

    @Override
    public Map<ChemicalEntity<?>, Quantity<MolarConcentration>> getAllConcentrationsForSection(CellSection cellSection) {
        if (this.cellSection.equals(cellSection)) {
            return concentrations;
        }
        return Collections.emptyMap();
    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity) {
        if (this.cellSection.equals(cellSection)) {
            if (concentrations.containsKey(chemicalEntity)) {
                return getConcentration(chemicalEntity);
            }
            concentrations.put(chemicalEntity, EnvironmentalParameters.emptyConcentration());
            return EnvironmentalParameters.emptyConcentration();
        }
        return null;
    }

    @Override
    public void setConcentration(ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        concentrations.put(chemicalEntity, concentration);
    }

    @Override
    public void setAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        setConcentration(chemicalEntity, concentration);
    }

    @Override
    public Set<ChemicalEntity<?>> getAllReferencedEntities() {
        return concentrations.keySet();
    }

    @Override
    public Set<CellSection> getAllReferencedSections() {
        return Collections.singleton(cellSection);
    }

    @Override
    public Map<ChemicalEntity<?>, Quantity<MolarConcentration>> getAllConcentrations() {
        return concentrations;
    }

    @Override
    public SimpleConcentrationContainer getCopy() {
        return new SimpleConcentrationContainer(cellSection);
    }

}
