package bio.singa.simulation.model.concentrations;

/**
 * @author cl
 */
public abstract class AbstractConcentrationCondition implements ConcentrationCondition {

    private final int priority;

    public AbstractConcentrationCondition(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

}
