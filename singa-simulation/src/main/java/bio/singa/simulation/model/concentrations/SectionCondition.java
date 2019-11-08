package bio.singa.simulation.model.concentrations;

import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Updatable;

/**
 * @author cl
 */
public class SectionCondition extends AbstractConcentrationCondition {

    public static SectionCondition forSection(CellSubsection section) {
        return new SectionCondition(section);
    }

    private CellSubsection subsection;

    private SectionCondition(CellSubsection subsection) {
        super(20);
        this.subsection = subsection;
    }

    public CellSubsection getSubsection() {
        return subsection;
    }

    @Override
    public boolean test(Updatable updatable) {
        return updatable.getCellRegion().getSubsections().contains(subsection);
    }

    @Override
    public String toString() {
        return "updatable has section " + subsection;
    }

}
