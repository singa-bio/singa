package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.Membrane;

import javax.measure.Quantity;
import java.util.*;

/**
 * In this representation of a membrane is divided into four parts. An outer aqueous phase, an inner aqueous phase, the
 * layer of the membrane oriented to the outer phase, and the layer of the membrane oriented to the inner phase.
 * This class is modelled after the representation in:
 * Dickson 2016 - Structure-kinetic relationships of passive membrane permeation from multiscale modeling.
 *
 * @author cl
 */
public class MembraneContainer implements ConcentrationContainer {

    private final CellSection outerPhaseSection;
    private final CellSection innerPhaseSection;
    private final Membrane membrane;

    private Set<ChemicalEntity<?>> referencedEntities;
    private Set<CellSection> refencedSections;

    private final Map<ChemicalEntity<?>, Quantity<MolarConcentration>> outerPhase;
    private final Map<ChemicalEntity<?>, Quantity<MolarConcentration>> outerLayer;
    private final Map<ChemicalEntity<?>, Quantity<MolarConcentration>> innerLayer;
    private final Map<ChemicalEntity<?>, Quantity<MolarConcentration>> innerPhase;

    public MembraneContainer(CellSection outerPhaseSection, CellSection innerPhaseSection, Membrane membrane) {
        this.outerPhaseSection = outerPhaseSection;
        this.innerPhaseSection = innerPhaseSection;
        this.membrane = membrane;

        outerPhase = new HashMap<>();
        outerLayer = new HashMap<>();
        innerLayer = new HashMap<>();
        innerPhase = new HashMap<>();

        referencedEntities = new HashSet<>();
        refencedSections = new HashSet<>();
    }

    public MembraneContainer(MembraneContainer container) {
        outerPhaseSection = container.outerPhaseSection;
        innerPhaseSection = container.innerPhaseSection;
        membrane = container.membrane;
        outerPhase = new HashMap<>(container.outerPhase);
        outerLayer = new HashMap<>(container.outerLayer);
        innerLayer = new HashMap<>(container.innerLayer);
        innerPhase = new HashMap<>(container.innerPhase);
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity chemicalEntity) {
        Quantity<MolarConcentration> concentrationSum = getAvailableConcentration(outerPhaseSection, chemicalEntity);
        concentrationSum = concentrationSum.add(getAvailableConcentration(innerPhaseSection, chemicalEntity));
        concentrationSum = concentrationSum.add(getAvailableConcentration(membrane.getInnerLayer(), chemicalEntity));
        concentrationSum = concentrationSum.add(getAvailableConcentration(membrane.getOuterLayer(), chemicalEntity));
        return concentrationSum.divide(4.0);
    }

    public Quantity<MolarConcentration> getOuterPhaseConcentration(ChemicalEntity chemicalEntity) {
        return outerPhase.get(chemicalEntity);
        // outerPhase.put(chemicalEntity, EnvironmentalParameters.emptyConcentration());
        // return EnvironmentalParameters.emptyConcentration();
    }

    public Quantity<MolarConcentration> getInnerPhaseConcentration(ChemicalEntity chemicalEntity) {
        return innerPhase.get(chemicalEntity);
        // innerPhase.put(chemicalEntity, EnvironmentalParameters.emptyConcentration());
        // return EnvironmentalParameters.emptyConcentration();
    }

    public Quantity<MolarConcentration> getOuterMembraneLayerConcentration(ChemicalEntity chemicalEntity) {
        return outerLayer.get(chemicalEntity);
        // outerLayer.put(chemicalEntity, EnvironmentalParameters.emptyConcentration());
        // return EnvironmentalParameters.emptyConcentration();
    }

    public Quantity<MolarConcentration> getInnerMembraneLayerConcentration(ChemicalEntity chemicalEntity) {
        return innerLayer.get(chemicalEntity);
        // innerLayer.put(chemicalEntity, EnvironmentalParameters.emptyConcentration());
        // return EnvironmentalParameters.emptyConcentration();
    }

    @Override
    public Map<ChemicalEntity<?>, Quantity<MolarConcentration>> getAllConcentrationsForSection(CellSection cellSection) {
        if (cellSection.equals(outerPhaseSection)) {
            return outerPhase;
        } else if (cellSection.equals(innerPhaseSection)) {
            return innerPhase;
        } else if (cellSection.equals(membrane)) {
            Map<ChemicalEntity<?>, Quantity<MolarConcentration>> concentrations = new HashMap<>();
            for (Map.Entry<ChemicalEntity<?>, Quantity<MolarConcentration>> entry : innerLayer.entrySet()) {
                concentrations.put(entry.getKey(), entry.getValue().add(outerLayer.get(entry.getKey())).divide(2.0));
            }
            return concentrations;
        } else if (cellSection.equals(membrane.getInnerLayer())) {
            return innerLayer;
        } else if (cellSection.equals(membrane.getOuterLayer())) {
            return outerLayer;
        }
        return Collections.emptyMap();
    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity) {
        Quantity<MolarConcentration> concentration = null;
        if (cellSection.equals(outerPhaseSection)) {
            concentration = getOuterPhaseConcentration(chemicalEntity);
        } else if (cellSection.equals(innerPhaseSection)) {
            concentration = getInnerPhaseConcentration(chemicalEntity);
        } else if (cellSection.equals(membrane)) {
            concentration = getInnerMembraneLayerConcentration(chemicalEntity)
                    .add(getOuterMembraneLayerConcentration(chemicalEntity))
                    .divide(2.0);
        } else if (cellSection.equals(membrane.getInnerLayer())) {
            concentration = getInnerMembraneLayerConcentration(chemicalEntity);
        } else if (cellSection.equals(membrane.getOuterLayer())) {
            concentration = getOuterMembraneLayerConcentration(chemicalEntity);
        }
        if (concentration == null) {
            return EnvironmentalParameters.emptyConcentration();
        }
        return concentration;
    }

    @Override
    public void setConcentration(ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        outerPhase.put(chemicalEntity, concentration);
        outerLayer.put(chemicalEntity, concentration);
        innerLayer.put(chemicalEntity, concentration);
        innerPhase.put(chemicalEntity, concentration);
    }

    @Override
    public void setAvailableConcentration(CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> concentration) {
        if (cellSection.equals(outerPhaseSection)) {
            outerPhase.put(chemicalEntity, concentration);
        } else if (cellSection.equals(innerPhaseSection)) {
            innerPhase.put(chemicalEntity, concentration);
        } else if (cellSection.equals(membrane)) {
            innerLayer.put(chemicalEntity, concentration);
            outerLayer.put(chemicalEntity, concentration);
        } else if (cellSection.equals(membrane.getInnerLayer())) {
            innerLayer.put(chemicalEntity, concentration);
        } else if (cellSection.equals(membrane.getOuterLayer())) {
            outerLayer.put(chemicalEntity, concentration);
        }
    }

    @Override
    public Set<ChemicalEntity<?>> getAllReferencedEntities() {
        return referencedEntities;
    }

    public void setReferencedEntities(Set<ChemicalEntity<?>> referencedEntities) {
        this.referencedEntities = referencedEntities;
    }

    @Override
    public Set<CellSection> getAllReferencedSections() {
        return refencedSections;
    }

    public void setRefencedSections(Set<CellSection> refencedSections) {
        this.refencedSections = refencedSections;
    }

    public Membrane getMembrane() {
        return membrane;
    }

    public CellSection getOuterPhaseSection() {
        return outerPhaseSection;
    }

    public CellSection getOuterLayerSection() {
        return membrane.getOuterLayer();
    }

    public CellSection getInnerLayerSection() {
        return membrane.getInnerLayer();
    }

    public CellSection getInnerPhaseSection() {
        return innerPhaseSection;
    }

    @Override
    public Map<ChemicalEntity<?>, Quantity<MolarConcentration>> getAllConcentrations() {
        Map<ChemicalEntity<?>, Quantity<MolarConcentration>> result = new HashMap<>();
        for (ChemicalEntity<?> chemicalEntity : getAllReferencedEntities()) {
            result.put(chemicalEntity, getConcentration(chemicalEntity));
        }
        return result;
    }

    @Override
    public MembraneContainer getCopy() {
        return new MembraneContainer(this);
    }

}
