package bio.singa.simulation.model.simulation;

import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public interface Updatable {

    String getStringIdentifier();

    ConcentrationContainer getConcentrationContainer();

    CellRegion getCellRegion();
    Set<CellSubsection> getAllReferencedSections();
    List<ConcentrationDelta> getPotentialConcentrationDeltas();

    void addPotentialDelta(ConcentrationDelta delta);
    void clearPotentialConcentrationDeltas();
    void clearPotentialDeltasBut(UpdateModule module);
    void shiftDeltas();
    void applyDeltas();
    boolean hasDeltas();

    void setObserved(boolean observed);
    boolean isObserved();
}
