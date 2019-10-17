package bio.singa.simulation.model.concentrations;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Updatable;

/**
 * @author cl
 */
public class NodeTypeCondition extends AbstractConcentrationCondition {

    private static final NodeTypeCondition IS_NODE = new NodeTypeCondition("node");
    private static final NodeTypeCondition IS_VESICLE = new NodeTypeCondition("vesicle");

    public static NodeTypeCondition isNode() {
        return IS_NODE;
    }

    public static NodeTypeCondition isVesicle() {
        return IS_VESICLE;
    }

    private String type;
    private boolean isNode;
    private boolean isVesicle;

    private NodeTypeCondition(String type) {
        super(10);
        this.type = type;
        if (type.equals("node")) {
            isNode = true;
            isVesicle = false;
        } else if(type.equals("vesicle")) {
            isNode = false;
            isVesicle = true;
        } else {
            throw new IllegalArgumentException("Node Condition requires node or vesicle input");
        }
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean test(Updatable updatable) {
        if (isNode) {
            return updatable instanceof AutomatonNode;
        } else if (isVesicle) {
            return updatable instanceof Vesicle;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "updatable is " + type;
    }

}
