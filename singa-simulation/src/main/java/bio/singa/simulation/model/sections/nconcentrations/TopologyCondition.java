package bio.singa.simulation.model.sections.nconcentrations;

import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import static bio.singa.simulation.model.sections.CellTopology.*;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class TopologyCondition extends AbstractConcentrationCondition {

    private static final TopologyCondition IS_MEMBRANE = new TopologyCondition(MEMBRANE);
    private static final TopologyCondition IS_INNER = new TopologyCondition(INNER);
    private static final TopologyCondition IS_OUTER = new TopologyCondition(OUTER);

    public static TopologyCondition isMembrane() {
        return IS_MEMBRANE;
    }

    public static TopologyCondition isInner() {
        return IS_INNER;
    }

    public static TopologyCondition isOuter() {
        return IS_OUTER;
    }

    public static TopologyCondition isTopology(CellTopology topology) {
        switch (topology) {
            case INNER:
                return IS_MEMBRANE;
            case MEMBRANE:
                return IS_INNER;
            case OUTER:
                return  IS_OUTER;
            default:
                return null;
        }
    }

    private CellTopology topology;

    private TopologyCondition(CellTopology topology) {
        super(20);
        this.topology = topology;
    }

    public CellTopology getTopology() {
        return topology;
    }

    @Override
    public boolean test(Updatable updatable) {
        return updatable.getCellRegion().has(topology);
    }

    @Override
    public String toString() {
        return "updatable has topology " + topology;
    }

}
