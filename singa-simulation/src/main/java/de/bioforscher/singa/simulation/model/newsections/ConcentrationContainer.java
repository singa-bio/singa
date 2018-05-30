package de.bioforscher.singa.simulation.model.newsections;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.units.UnitProvider;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.OUTER;

/**
 * @author cl
 */
public class ConcentrationContainer {

    // FIXME hashing tales a shitton of time, maybe reimplement this as individual poos with method switching
    private Map<CellTopology, CellSubsection> subsectionTopology;
    private Map<CellSubsection, ConcentrationPool> concentrations;

    public ConcentrationContainer() {
        subsectionTopology = new HashMap<>();
        concentrations = new HashMap<>();
    }

    public void initializeSubsection(CellSubsection subsection, CellTopology topology) {
        putSubsectionPool(subsection, topology, new ConcentrationPool());
    }

    public void putSubsectionPool(CellSubsection subsection, CellTopology topology, ConcentrationPool concentrationPool) {
        subsectionTopology.put(topology, subsection);
        concentrations.put(subsection, concentrationPool);
    }

    public void removeSubsection(CellSubsection cellSubsection) {
        concentrations.remove(cellSubsection);
        subsectionTopology.remove(getTopologyFromSubsection(cellSubsection));
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

    public Map.Entry<CellTopology, ConcentrationPool> getPool(CellSubsection subsection) {
        CellTopology topology = getTopologyFromSubsection(subsection);
        if (topology != null) {
            return new AbstractMap.SimpleEntry<>(topology, concentrations.get(subsection));
        }
        return null;
    }

    public Map.Entry<CellSubsection, ConcentrationPool> getPool(CellTopology topology) {
        CellSubsection subsection = subsectionTopology.get(topology);
        if (subsection != null) {
            return new AbstractMap.SimpleEntry<>(subsection, concentrations.get(subsection));
        }
        return null;
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
        return get(subsection, entity);
    }

    public void set(CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        concentrations.get(subsection).set(entity, concentration);
    }

    public void set(CellTopology topology, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        set(subsectionTopology.get(topology), entity, concentration);
    }

    /**
     * Sets the concentration of the given entity in the given subsection. The concentration is assumed to be
     * {@link UnitProvider#MOLE_PER_LITRE} but is transformed to the preferred concentration.
     *
     * @param subsection The subsection.
     * @param entity The entity.
     * @param concentration The concentration in mol/l
     */
    public void set(CellSubsection subsection, ChemicalEntity entity, double concentration) {
        set(subsection, entity, Quantities.getQuantity(concentration, MOLE_PER_LITRE).to(subsection.getPreferredConcentrationUnit()));
    }

    /**
     * Sets the concentration of the given entity in the subsection corresponding to the topological descriptor. The
     * concentration is assumed to be {@link UnitProvider#MOLE_PER_LITRE} but is transformed to the preferred
     * concentration.
     *
     * @param topology The topological descriptor.
     * @param entity The entity.
     * @param concentration The concentration in mol/l
     */
    public void set(CellTopology topology, ChemicalEntity entity, double concentration) {
        CellSubsection subsection = subsectionTopology.get(topology);
        set(subsection, entity, concentration);
    }

    public CellSubsection getSubsection(CellTopology topology) {
        return subsectionTopology.get(topology);
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

    public ConcentrationContainer emptyCopy() {
        ConcentrationContainer concentrationContainer = new ConcentrationContainer();
        for (Map.Entry<CellTopology, CellSubsection> entry : subsectionTopology.entrySet()) {
            concentrationContainer.initializeSubsection(entry.getValue(), entry.getKey());
        }
        return concentrationContainer;
    }

    public ConcentrationContainer fullCopy() {
        ConcentrationContainer concentrationContainer = new ConcentrationContainer();
        for (Map.Entry<CellTopology, CellSubsection> entry : subsectionTopology.entrySet()) {
            CellTopology topology = entry.getKey();
            CellSubsection subsection = subsectionTopology.get(topology);
            concentrationContainer.putSubsectionPool(subsection, topology, getPool(subsection).getValue().fullCopy());
        }
        return concentrationContainer;
    }

    private CellTopology getTopologyFromSubsection(CellSubsection subsection) {
        for (Map.Entry<CellTopology, CellSubsection> entry : subsectionTopology.entrySet()) {
            if (Objects.equals(subsection, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
