package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.module.UpdateModule;

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
    List<Delta> getPotentialSpatialDeltas();

    void addPotentialDelta(Delta delta);
    void clearPotentialDeltas();
    void clearPotentialDeltasBut(UpdateModule module);
    void shiftDeltas();
    void applyDeltas();

    void setObserved(boolean observed);
    boolean isObserved();
}
