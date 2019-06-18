package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class FixedConcentration implements InitialConcentration {

    private List<String> identifiers;
    private CellSubsection subsection;
    private ChemicalEntity entity;
    private Quantity<MolarConcentration> concentration;

    private Evidence evidence;

    public FixedConcentration(List<String> identifiers, CellSubsection subsection, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.identifiers = identifiers;
        this.subsection = subsection;
        this.entity = entity;
        this.concentration = concentration;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public CellSubsection getSubsection() {
        return subsection;
    }

    public void setSubsection(CellSubsection subsection) {
        this.subsection = subsection;
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    public Quantity<MolarConcentration> getConcentration() {
        return concentration;
    }

    public void setConcentration(Quantity<MolarConcentration> concentration) {
        this.concentration = concentration;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    @Override
    public void initialize(Updatable updatable) {
        if (identifiers.contains(updatable.getStringIdentifier())) {
            // FIXME careful fixing this does not consider the subsection
            initializeUnchecked(updatable, null);
        }
    }

    @Override
    public void initializeUnchecked(Updatable updatable, CellTopology topology) {
        updatable.getConcentrationManager().fix(entity);
        updatable.getConcentrationContainer().initialize(subsection, entity, concentration);
    }

    @Override
    public String toString() {
        return "FixedConcentration:" +
                " I = " + identifiers +
                " S = " + subsection +
                " E = " + entity +
                " C = " + concentration +
                '}';
    }
}
