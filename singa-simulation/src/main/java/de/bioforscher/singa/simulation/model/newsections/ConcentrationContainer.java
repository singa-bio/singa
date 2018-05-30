package de.bioforscher.singa.simulation.model.newsections;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.OUTER;

/**
 * @author cl
 */
public class ConcentrationContainer {

    private Map<CellTopology, CellSubsection> subsectionTopology;
    private Map<CellSubsection, ConcentrationPool> concentrations;

    public ConcentrationContainer() {
        subsectionTopology = new HashMap<>();
        concentrations = new HashMap<>();
    }

    public void initializeSubsection(CellSubsection subsection, CellTopology topology) {
        subsectionTopology.put(topology, subsection);
        concentrations.put(subsection, new ConcentrationPool());
    }

    public void addSubsectionPool(CellSubsection subsection, CellTopology topology, ConcentrationPool concentrationPool) {
        subsectionTopology.put(topology, subsection);
        concentrations.put(subsection, concentrationPool);
    }

    public void removeSubsection(CellSubsection cellSubsection) {
        concentrations.remove(cellSubsection);
    }

    public Set<CellSubsection> getReferencedSubSections() {
        return concentrations.keySet();
    }

    public Collection<ConcentrationPool> getPoolsOfConcentration() {
        return concentrations.values();
    }

    public Set<ChemicalEntity> getReferencedEntities() {
        Set<ChemicalEntity> chemicalEntities = new HashSet<>();
        for (ConcentrationPool concentrationPool : concentrations.values()) {
            chemicalEntities.addAll(concentrationPool.getReferencedEntities());
        }
        return chemicalEntities;
    }

    public Quantity<MolarConcentration> get(CellSubsection subsection, ChemicalEntity entity) {
        ConcentrationPool concentrationPool = concentrations.get(subsection);
        if (concentrationPool == null) {
            return Environment.emptyConcentration();
        }
        return concentrationPool.get(entity);
    }

    public Quantity<MolarConcentration> get(CellTopology topology, ChemicalEntity entity) {
        CellSubsection subsection = subsectionTopology.get(topology);
        if (subsection == null) {
            return Environment.emptyConcentration();
        }
        ConcentrationPool concentrationPool = concentrations.get(subsection);
        if (concentrationPool == null) {
            return Environment.emptyConcentration();
        }
        return concentrationPool.get(entity);
    }

    public void set(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        concentrations.get(subsection).set(entity, concentration);
    }

    public void set(CellTopology topology, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        concentrations.get(subsectionTopology.get(topology)).set(entity, concentration);
    }

    public void set(CellSubsection subsection, ChemicalEntity entity, double concentration) {
        concentrations.get(subsection).set(entity, Quantities.getQuantity(concentration, subsection.getPreferredConcentrationUnit()));
    }

    public ConcentrationContainer emptyCopy() {
        ConcentrationContainer concentrationContainer = new ConcentrationContainer();
        for (Map.Entry<CellTopology, CellSubsection> entry : subsectionTopology.entrySet()) {
            concentrationContainer.initializeSubsection(entry.getValue(), entry.getKey());
        }
        return concentrationContainer;
    }

    public CellSubsection getInnerSubsection() {
        return subsectionTopology.get(INNER);
    }

    public CellSubsection getOuterSubsection() {
        return subsectionTopology.get(OUTER);
    }

    public CellSubsection getMembraneSubsection() {
        return subsectionTopology.get(CellTopology.MEMBRANE);
    }

    public CellSubsection getSubsection(CellTopology topology) {
        return  subsectionTopology.get(topology);
    }

}
