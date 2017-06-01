package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.units.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cl
 */
public class SimpleConcentrationContainer implements ConcentrationContainer {

    private Map<ChemicalEntity, Quantity<MolarConcentration>> container;

    public SimpleConcentrationContainer() {
        this.container = new HashMap<>();
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity chemicalEntity) {
        return this.container.get(chemicalEntity);
    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity) {
        return getConcentration(chemicalEntity);
    }

    @Override
    public void setConcentration(ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        this.container.put(chemicalEntity, concentration);
    }

    @Override
    public void setAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        setConcentration(chemicalEntity, concentration);
    }

    @Override
    public Set<ChemicalEntity> getAllReferencedEntities() {
        return this.container.keySet();
    }

    @Override
    public Set<CellSection> getAllReferencedSections() {
        return Collections.emptySet();
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        return this.container;
    }
}
