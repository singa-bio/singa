package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;

import javax.measure.Quantity;
import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public interface Updatable {

    String getStringIdentifier();

    ConcentrationContainer getConcentrationContainer();
    Quantity<MolarConcentration> getAvailableConcentration(ChemicalEntity chemicalEntity, CellSection cellSection);

    Set<CellSection> getAllReferencedSections();
    Set<ChemicalEntity> getAllReferencedEntities();
    List<Delta> getPotentialSpatialDeltas();

    void addPotentialDelta(Delta delta);
    void clearPotentialDeltas();
    void shiftDeltas();
    void applyDeltas();



}
