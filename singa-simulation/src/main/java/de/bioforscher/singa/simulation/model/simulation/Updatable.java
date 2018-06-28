package de.bioforscher.singa.simulation.model.simulation;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.modules.UpdateModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.sections.CellRegion;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

import javax.measure.Quantity;
import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public interface Updatable {

    String getStringIdentifier();

    ConcentrationContainer getConcentrationContainer();
    Quantity<MolarConcentration> getConcentration(CellSubsection cellSection, ChemicalEntity chemicalEntity);

    CellRegion getCellRegion();
    Set<CellSubsection> getAllReferencedSections();
    List<ConcentrationDelta> getPotentialSpatialDeltas();

    void addPotentialDelta(ConcentrationDelta delta);
    void clearPotentialDeltas();
    void clearPotentialDeltasBut(UpdateModule module);
    void shiftDeltas();
    void applyDeltas();

    void setObserved(boolean observed);
    boolean isObserved();
}
