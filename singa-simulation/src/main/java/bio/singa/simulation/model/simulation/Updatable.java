package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

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
    void clearPotentialConcentrationDeltas();
    void clearPotentialDeltasBut(UpdateModule module);
    void shiftDeltas();
    void applyDeltas();
    boolean hasDeltas();

    void setObserved(boolean observed);
    boolean isObserved();
}
