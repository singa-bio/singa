package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.units.UnitProvider;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;

/**
 * In this representation of a membrane is divided into four parts. An outer aqueous phase, an inner aqueous phase, the
 * layer of the membrane oriented to the outer phase, and the layer of the membrane oriented to the inner phase.
 *
 * This class is modelled after the representation in:
 * Dickson 2016 - Structure-kinetic relationships of passive membrane permeation from multiscale modeling.
 *
 * @author cl
 */
public class MembraneContainer implements ConcentrationContainer {

    private CellSection outerPhaseSection;
    private CellSection innerPhaseSection;
    private Membrane membrane;

    private Map<ChemicalEntity, Quantity<MolarConcentration>> outerPhase;
    private Map<ChemicalEntity, Quantity<MolarConcentration>> outerLayer;
    private Map<ChemicalEntity, Quantity<MolarConcentration>> innerLayer;
    private Map<ChemicalEntity, Quantity<MolarConcentration>> innerPhase;

    public MembraneContainer(CellSection outerPhaseSection, CellSection innerPhaseSection, Membrane membrane) {
        this.outerPhaseSection = outerPhaseSection;
        this.innerPhaseSection = innerPhaseSection;
        this.membrane = membrane;

        this.outerPhase = new HashMap<>();
        this.outerLayer = new HashMap<>();
        this.innerLayer = new HashMap<>();
        this.innerPhase = new HashMap<>();
    }

    public MembraneContainer(MembraneContainer container) {
        this(container.outerPhaseSection, container.innerPhaseSection, container.membrane);
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity chemicalEntity) {
        Quantity<MolarConcentration> concentrationSum = this.outerPhase.get(chemicalEntity);
        concentrationSum = concentrationSum.add(this.innerPhase.get(chemicalEntity));
        concentrationSum = concentrationSum.add(this.outerLayer.get(chemicalEntity));
        concentrationSum = concentrationSum.add(this.innerLayer.get(chemicalEntity));
        return concentrationSum.divide(4.0);
    }

    public Quantity<MolarConcentration> getOuterPhaseConcentration(ChemicalEntity chemicalEntity) {
        if (this.outerPhase.containsKey(chemicalEntity)) {
            return this.outerPhase.get(chemicalEntity);
        }
        return Quantities.getQuantity(0.0, UnitProvider.MOLE_PER_LITRE);
    }

    public Quantity<MolarConcentration> getInnerPhaseConcentration(ChemicalEntity chemicalEntity) {
        if (this.innerPhase.containsKey(chemicalEntity)) {
            return this.innerPhase.get(chemicalEntity);
        }
        return Quantities.getQuantity(0.0, UnitProvider.MOLE_PER_LITRE);
    }

    public Quantity<MolarConcentration> getOuterMembraneLayerConcentration(ChemicalEntity chemicalEntity) {
        if (this.outerLayer.containsKey(chemicalEntity)) {
            return this.outerLayer.get(chemicalEntity);
        }
        return Quantities.getQuantity(0.0, UnitProvider.MOLE_PER_LITRE);
    }

    public Quantity<MolarConcentration> getInnerMembraneLayerConcentration(ChemicalEntity chemicalEntity) {
        if (this.innerLayer.containsKey(chemicalEntity)) {
            return this.innerLayer.get(chemicalEntity);
        }
        return Quantities.getQuantity(0.0, UnitProvider.MOLE_PER_LITRE);
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrationsForSection(CellSection cellSection) {
        if (cellSection.equals(this.outerPhaseSection)) {
            return this.outerPhase;
        } else if (cellSection.equals(this.innerPhaseSection))  {
            return this.innerPhase;
        } else if (cellSection.equals(this.membrane)) {
            Map<ChemicalEntity, Quantity<MolarConcentration>> concentrations = new HashMap<>();
            for(Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry: this.innerLayer.entrySet()) {
                concentrations.put(entry.getKey(), entry.getValue().add(this.outerLayer.get(entry.getKey())).divide(2.0));
            }
            return concentrations;
        } else if (cellSection.equals(this.membrane.getInnerLayer())) {
            return this.innerLayer;
        } else if (cellSection.equals(this.membrane.getOuterLayer())) {
            return this.outerLayer;
        }
        return Collections.emptyMap();
    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity) {
        if (cellSection.equals(this.outerPhaseSection)) {
            if (this.outerPhase.containsKey(chemicalEntity)) {
                return this.outerPhase.get(chemicalEntity);
            }
        } else if (cellSection.equals(this.innerPhaseSection))  {
            if (this.innerPhase.containsKey(chemicalEntity)) {
                return this.innerPhase.get(chemicalEntity);
            }
        } else if (cellSection.equals(this.membrane)) {
            Quantity<MolarConcentration> concentrationSum;
            if (this.innerLayer.containsKey(chemicalEntity)) {
                concentrationSum = (this.innerLayer.get(chemicalEntity));
                concentrationSum = concentrationSum.add(this.outerLayer.get(chemicalEntity));
                return concentrationSum.divide(2.0);
            }
        } else if (cellSection.equals(this.membrane.getInnerLayer())) {
            if (this.innerLayer.containsKey(chemicalEntity)) {
                return this.innerLayer.get(chemicalEntity);
            }
        } else if (cellSection.equals(this.membrane.getOuterLayer())) {
            if (this.outerLayer.containsKey(chemicalEntity)) {
                return this.outerLayer.get(chemicalEntity);
            }
        }
        return Quantities.getQuantity(0.0, UnitProvider.MOLE_PER_LITRE);
    }

    @Override
    public void setConcentration(ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        this.outerPhase.put(chemicalEntity, concentration);
        this.outerLayer.put(chemicalEntity, concentration);
        this.innerLayer.put(chemicalEntity, concentration);
        this.innerPhase.put(chemicalEntity, concentration);
    }

    @Override
    public void setAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        if (cellSection.equals(this.outerPhaseSection)) {
            this.outerPhase.put(chemicalEntity, concentration);
        } else if (cellSection.equals(this.innerPhaseSection))  {
            this.innerPhase.put(chemicalEntity, concentration);
        } else if (cellSection.equals(this.membrane)) {
           this.innerLayer.put(chemicalEntity, concentration);
           this.outerLayer.put(chemicalEntity, concentration);
        } else if (cellSection.equals(this.membrane.getInnerLayer())) {
            this.innerLayer.put(chemicalEntity, concentration);
        } else if (cellSection.equals(this.membrane.getOuterLayer())) {
            this.outerLayer.put(chemicalEntity, concentration);
        }
    }

    @Override
    public Set<ChemicalEntity> getAllReferencedEntities() {
        Set<ChemicalEntity> entities = new HashSet<>();
        entities.addAll(this.innerLayer.keySet());
        entities.addAll(this.innerPhase.keySet());
        entities.addAll(this.outerPhase.keySet());
        entities.addAll(this.outerLayer.keySet());
        return entities;
    }

    @Override
    public Set<CellSection> getAllReferencedSections() {
        Set<CellSection> sections = new HashSet<>();
        sections.add(this.innerPhaseSection);
        sections.add(this.outerPhaseSection);
        sections.add(this.membrane.getInnerLayer());
        sections.add(this.membrane.getOuterLayer());
        return sections;
    }

    public Membrane getMembrane() {
        return this.membrane;
    }

    public CellSection getOuterPhaseSection() {
        return this.outerPhaseSection;
    }

    public CellSection getOuterLayerSection() {
        return this.membrane.getOuterLayer();
    }

    public CellSection getInnerLayerSection() {
        return this.membrane.getInnerLayer();
    }

    public CellSection getInnerPhaseSection() {
        return this.innerPhaseSection;
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        Map<ChemicalEntity, Quantity<MolarConcentration>> result = new HashMap<>();
        for (ChemicalEntity chemicalEntity : getAllReferencedEntities()) {
            result.put(chemicalEntity, getConcentration(chemicalEntity));
        }
        return result;
    }

    @Override
    public MembraneContainer copy() {
        return new MembraneContainer(this);
    }


}
