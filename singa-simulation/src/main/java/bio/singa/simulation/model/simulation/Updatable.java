package bio.singa.simulation.model.simulation;

import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaManager;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import java.util.Set;

/**
 * @author cl
 */
public interface Updatable {

    String getStringIdentifier();

    ConcentrationDeltaManager getConcentrationManager();
    ConcentrationContainer getConcentrationContainer();
    void addPotentialDelta(ConcentrationDelta potentialDelta);

    CellRegion getCellRegion();
    Set<CellSubsection> getAllReferencedSections();

    void setObserved(boolean observed);
    boolean isObserved();
}
