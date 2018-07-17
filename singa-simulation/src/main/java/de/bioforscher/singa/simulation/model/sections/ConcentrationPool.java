package de.bioforscher.singa.simulation.model.sections;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The concentration pool manages the {@link MolarConcentration} of {@link ChemicalEntity}s in {@link CellSubsection}s.
 *
 * @author cl
 */
public class ConcentrationPool {

    /**
     * The associated concentrations.
     */
    private Map<ChemicalEntity, Quantity<MolarConcentration>> concentrations;

    /**
     * Creates a new, empty concentration pool.
     */
    public ConcentrationPool() {
        concentrations = new HashMap<>();
    }

    /**
     * Creates a copy of the given pool.
     * @param concentrationPool The pool to copy.
     */
    private ConcentrationPool(ConcentrationPool concentrationPool) {
        concentrations = new HashMap<>(concentrationPool.concentrations);
    }

    /**
     * Returns all entities referenced in this map.
     * @return All entities referenced in this map.
     */
    public Set<ChemicalEntity> getReferencedEntities() {
        return concentrations.keySet();
    }

    public Set<Map.Entry<ChemicalEntity, Quantity<MolarConcentration>>> getConcentrations() {
        return concentrations.entrySet();
    }

    /**
     * Returns the concentration of a entity.
     * @param entity The entity.
     * @return The concentration of a entity.
     */
    public Quantity<MolarConcentration> get(ChemicalEntity entity) {
        return concentrations.getOrDefault(entity, Environment.emptyConcentration());
    }

    /**
     * Sets the concentration of a entity.
     * @param entity Th entity.
     * @param concentration The concentration (this should be scaled to the subsection volume).
     */
    public void set(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        concentrations.put(entity, concentration);
    }

    /**
     * Creates a copy of this concentration pool.
     */
    public ConcentrationPool fullCopy() {
        return new ConcentrationPool(this);
    }
    
}
